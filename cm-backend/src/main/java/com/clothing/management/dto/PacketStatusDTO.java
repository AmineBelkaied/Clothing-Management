package com.clothing.management.dto;
import com.clothing.management.entities.Packet;
import com.clothing.management.entities.PacketStatus;
import com.clothing.management.entities.User;

import java.util.Date;

public class PacketStatusDTO {
    private Date date;
    private String status;
    private String user;

    public PacketStatusDTO() {
    }
    public PacketStatusDTO(PacketStatus packetStatus) {
        this.date = packetStatus.getDate();
        this.status = packetStatus.getStatus();
        this.user = packetStatus.getUser().getUserName();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

}
