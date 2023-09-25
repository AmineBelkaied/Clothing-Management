package com.clothing.management.scheduler;

import com.clothing.management.entities.Packet;
import com.clothing.management.enums.DeliveryCompany;
import com.clothing.management.services.PacketService;
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
    PacketService packetService;

    @Scheduled(cron = "0 40 9 ? * *")
    public int cronJobSch() throws Exception{
        System.out.println("CRON STARTED");
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
            }
        }
        System.out.println("CRON ENDED");
        return packets.size();
    }
}