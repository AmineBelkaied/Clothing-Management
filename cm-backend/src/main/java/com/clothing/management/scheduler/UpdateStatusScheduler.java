package com.clothing.management.scheduler;

import com.clothing.management.auth.constant.AppConstants;
import com.clothing.management.auth.mastertenant.config.DBContextHolder;
import com.clothing.management.auth.mastertenant.entity.MasterTenant;
import com.clothing.management.auth.mastertenant.service.MasterTenantService;
import com.clothing.management.auth.util.SessionUtils;
import com.clothing.management.entities.GlobalConf;
import com.clothing.management.dto.ModelStockHistory;
import com.clothing.management.entities.Packet;
import com.clothing.management.entities.User;
import com.clothing.management.services.GlobalConfService;
import com.clothing.management.services.ModelStockHistoryService;
import com.clothing.management.services.PacketService;
import com.clothing.management.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
public class UpdateStatusScheduler implements SchedulingConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateStatusScheduler.class);
    private static final int THREAD_SLEEP_INTERVAL = 1000;

    private final ModelStockHistoryService modelStockHistoryService;
    private final PacketService packetService;
    private final ProductService productService;
    private final MasterTenantService masterTenantService;
    private final GlobalConfService globalConfService;
    private final SessionUtils sessionUtils;

    @Value("${default.update.status.cron-expression}")
    private String defaultUpdateStatusCronExp;

    @Value("${default.count.stock.cron-expression}")
    private String defaultCountStockCronExp;

    @Value("${server.database.prefix}")
    private String serverDbPrefix;

    public UpdateStatusScheduler(ModelStockHistoryService modelStockHistoryService, PacketService packetService, ProductService productService, MasterTenantService masterTenantService, GlobalConfService globalConfService, SessionUtils sessionUtils) {
        this.modelStockHistoryService = modelStockHistoryService;
        this.packetService = packetService;
        this.productService = productService;
        this.masterTenantService = masterTenantService;
        this.globalConfService = globalConfService;
        this.sessionUtils = sessionUtils;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        List<MasterTenant> masterTenants = masterTenantService.findAllMasterTenants();
        masterTenants.stream()
                .filter(masterTenant -> !masterTenant.getDbName().equalsIgnoreCase(serverDbPrefix + AppConstants.MASTER_DB))
                .forEach(masterTenant -> {
                    DBContextHolder.setCurrentDb(masterTenant.getDbName());
                    GlobalConf globalConf = globalConfService.getGlobalConf();
                    String updateStatusCronExp = (globalConf != null && globalConf.getCronExpression() != null)
                            ? globalConf.getCronExpression()
                            : defaultUpdateStatusCronExp;

                    // Register cron task for updating packet status
                    taskRegistrar.addTriggerTask(
                            () -> startUpdateStatusCronTask(masterTenant),
                            triggerContext -> {
                                CronTrigger cronTrigger = new CronTrigger(updateStatusCronExp);
                                Date nextExecutionTime = cronTrigger.nextExecutionTime(triggerContext);
                                return nextExecutionTime != null ? nextExecutionTime.toInstant() : null;
                            }
                    );

                    // Register cron task for counting stock
                    taskRegistrar.addTriggerTask(
                            () -> startCountStockCronTask(masterTenant),
                            triggerContext -> {
                                CronTrigger cronTrigger = new CronTrigger(defaultCountStockCronExp);
                                Date nextExecutionTime = cronTrigger.nextExecutionTime(triggerContext);
                                return nextExecutionTime != null ? nextExecutionTime.toInstant() : null;
                            }
                    );
                });
    }

    @Transactional("tenantTransactionManager")
    public int startUpdateStatusCronTask(MasterTenant masterTenant) {
        LOGGER.info("UPDATE STATUS CRON STARTED FOR TENANT: {}", masterTenant.getTenantName());
        DBContextHolder.setCurrentDb(masterTenant.getDbName());
        packetService.deleteEmptyPacket();
        User currentUser = sessionUtils.getCurrentUser();
        List<Packet> packets = Collections.synchronizedList(packetService.findSyncPackets());

        synchronized (packets) {
            for (Packet packet : packets) {
                try {
                    packetService.getLastStatus(packet, currentUser);
                    LOGGER.info("UPDATE STATUS FINISHED FOR PACKET: {}", packet);
                } catch (IOException | InterruptedException e) {
                    LOGGER.error("Error updating status for packet {}: {}", packet, e.getMessage(), e);
                } catch (Exception e) {
                    LOGGER.error("Unexpected error occurred: {}", e.getMessage(), e);
                    throw new RuntimeException("Unexpected error occurred", e);
                }
                try {
                    Thread.sleep(THREAD_SLEEP_INTERVAL);
                } catch (InterruptedException e) {
                    LOGGER.error("Thread interrupted during sleep: {}", e.getMessage(), e);
                    Thread.currentThread().interrupt();
                }
            }
        }
        LOGGER.info("UPDATE STATUS CRON ENDED FOR TENANT: {}", masterTenant.getTenantName());
        return packets.size();
    }

    public void startCountStockCronTask(MasterTenant masterTenant) {
        try {
            LOGGER.info("COUNT STOCK CRON STARTED FOR TENANT: {}", masterTenant.getTenantName());
            DBContextHolder.setCurrentDb(masterTenant.getDbName());
            List<ModelStockHistory> countStock = productService.countStock();
            modelStockHistoryService.saveDayHistory(countStock);
            LOGGER.info("COUNT STOCK CRON ENDED FOR TENANT: {}", masterTenant.getTenantName());
        } catch (Exception e) {
            LOGGER.error("Error while saving count stock: {}", e.getMessage(), e);
        }
    }
}
