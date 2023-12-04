package com.clothing.management.scheduler;

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

    @Scheduled(cron = "0 10 9 ? * *")
    public int cronJobSch() throws Exception{

        System.out.println("CRON STARTED");
        int x = packetService.deleteEmptyPacket();
        System.out.println(x+"rows deleted");
        List<Packet> packets = Collections.synchronizedList(packetService.findAllDiggiePackets());
        synchronized (packets) {
            Iterator<Packet> iterator = packets.iterator();
            while (iterator.hasNext()) {
                Packet packet = iterator.next();
                try {
                     this.packetService.getLastStatus(packet, DeliveryCompany.FIRST.toString());
                } catch (IOException e) {

                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(2000); // You can adjust the sleep time as needed
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }

            }
        }
        System.out.println("CRON ENDED");
        return packets.size();
    }

    @Scheduled(cron = "0 0 20 ? * *")
    public void cronModelJobSch(){
        try{
        List <ModelStockHistory> countStock = productService.countStock();
        modelStockHistoryService.saveDayHistory(countStock);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}