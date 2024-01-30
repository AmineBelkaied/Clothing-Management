package com.clothing.management.scheduler;

import com.clothing.management.dto.DeliveryResponse;
import com.clothing.management.auth.mastertenant.config.DBContextHolder;
import com.clothing.management.auth.mastertenant.entity.MasterTenant;
import com.clothing.management.auth.mastertenant.entity.MasterUser;
import com.clothing.management.auth.mastertenant.repository.MasterTenantRepository;
import com.clothing.management.auth.mastertenant.service.MasterTenantService;
import com.clothing.management.auth.mastertenant.service.MasterUserService;
import com.clothing.management.entities.ModelStockHistory;
import com.clothing.management.entities.Packet;
import com.clothing.management.repository.enums.DeliveryCompany;
import com.clothing.management.services.ModelStockHistoryService;
import com.clothing.management.services.PacketService;
import com.clothing.management.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Component
public class UpdateStatusScheduler {
    @Autowired
    ModelStockHistoryService modelStockHistoryService;
    @Autowired
    PacketService packetService;
    @Autowired
    ProductService productService;
    @Autowired
    MasterTenantService masterTenantService;
    @Scheduled(cron = "*/5 * * * * *")
    public int cronJobSch() throws Exception{

        List<MasterTenant> masterTenants = masterTenantService.findAllMasterTenants();
        masterTenants.stream().filter(masterTenant -> !masterTenant.getDbName().equalsIgnoreCase("master_db"))
                .forEach(masterTenant -> {
            DBContextHolder.setCurrentDb(masterTenant.getDbName());
            System.out.println("CRON STARTED FOR TENANT - " + masterTenant.getTenantName());
            int x = packetService.deleteEmptyPacket();
            System.out.println(x + "rows deleted");
            List<Packet> packets = Collections.synchronizedList(packetService.findAllDiggiePackets());
            synchronized (packets) {
                Iterator<Packet> iterator = packets.iterator();
                while (iterator.hasNext()) {
                    Packet packet = iterator.next();
                    try {
                        this.packetService.getLastStatus(packet);
                    } catch (IOException e) {

                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        Thread.sleep(2000); // You can adjust the sleep time as needed
                    } catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }

                }
            }
            System.out.println("CRON ENDED");
            //return packets.size();
        });
        return 200;
    }

    @Scheduled(cron = "0 0 3 ? * *")
    public void cronModelJobSch(){
        try{
        List <ModelStockHistory> countStock = productService.countStock();
        modelStockHistoryService.saveDayHistory(countStock);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}