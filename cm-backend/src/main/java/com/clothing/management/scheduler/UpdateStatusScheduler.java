package com.clothing.management.scheduler;

import com.clothing.management.auth.constant.AppConstants;
import com.clothing.management.auth.mastertenant.config.DBContextHolder;
import com.clothing.management.auth.mastertenant.entity.MasterTenant;
import com.clothing.management.auth.mastertenant.service.MasterTenantService;
import com.clothing.management.auth.util.SessionUtils;
import com.clothing.management.entities.GlobalConf;
import com.clothing.management.entities.ModelStockHistory;
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

import java.io.IOException;
import java.util.*;

@Component
public class UpdateStatusScheduler implements SchedulingConfigurer {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateStatusScheduler.class);
    private final ModelStockHistoryService modelStockHistoryService;
    private final PacketService packetService;
    private final ProductService productService;
    private final MasterTenantService masterTenantService;
    private final GlobalConfService globalConfService;

    private final SessionUtils sessionUtils;
    private final static int THREAD_SLEEP_INTERVAL = 1000;
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
        masterTenants.stream().filter(masterTenant -> !masterTenant.getDbName().equalsIgnoreCase(serverDbPrefix + AppConstants.MASTER_DB))
                .forEach(masterTenant -> {
                    DBContextHolder.setCurrentDb(masterTenant.getDbName());
                    GlobalConf globalConf = globalConfService.getGlobalConf();
                    String updateStatusCronExp = (globalConf != null && globalConf.getCronExpression() != null)
                            ? globalConf.getCronExpression()
                            : defaultUpdateStatusCronExp;

                    // register cron task for updating packet status
                    taskRegistrar.addTriggerTask(() -> startUpdateStatusCronTask(masterTenant), triggerContext -> {
                        CronTrigger cronTrigger = new CronTrigger(updateStatusCronExp);
                        Date nextExecutionTime = cronTrigger.nextExecutionTime(triggerContext);
                        assert nextExecutionTime != null;
                        return nextExecutionTime.toInstant();
                    });

                    // register cron task for counting stock
                    taskRegistrar.addTriggerTask(() -> startCountStockCronTask(masterTenant), triggerContext -> {
                        CronTrigger cronTrigger = new CronTrigger(defaultCountStockCronExp);
                        Date nextExecutionTime = cronTrigger.nextExecutionTime(triggerContext);
                        assert nextExecutionTime != null;
                        return nextExecutionTime.toInstant();
                    });
                });
    }

    public int startUpdateStatusCronTask(MasterTenant masterTenant) {
        LOG.info("--- UPDATE STATUS CRON STARTED FOR TENANT --- " + masterTenant.getTenantName());
        DBContextHolder.setCurrentDb(masterTenant.getDbName());
        packetService.deleteEmptyPacket();

        List<Packet> packets = Collections.synchronizedList(packetService.findSyncPackets());
        synchronized (packets) {
            User currentUser = sessionUtils.getCurrentUser();
            for (Packet packet : packets) {
                try {
                    this.packetService.getLastStatus(packet,currentUser);
                    LOG.info("UPDATE STATUS FINISHED FOR PACKET >> " + packet.toString());
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    throw new RuntimeException("e1" + e);
                }
                try {
                    //System.out.println("sleep");
                    Thread.sleep(THREAD_SLEEP_INTERVAL);
                } catch (InterruptedException e2) {
                    System.out.println("e2" + e2);
                    e2.printStackTrace();
                }
            }
        }
        LOG.info("--- UPDATE STATUS CRON ENDED FOR TENANT --- " + masterTenant.getTenantName());
        return packets.size();
    }

    public void startCountStockCronTask(MasterTenant masterTenant) {
        try {
            LOG.info("--- COUNT STOCK CRON STARTED FOR TENANT --- " + masterTenant.getTenantName());
            DBContextHolder.setCurrentDb(masterTenant.getDbName());
            List<ModelStockHistory> countStock = productService.countStock();
            modelStockHistoryService.saveDayHistory(countStock);
            LOG.info("--- COUNT STOCK CRON ENDED FOR TENANT --- " + masterTenant.getTenantName());
        } catch (Exception e) {
            System.out.println("e3"+e);
            e.printStackTrace();
        }
    }
}