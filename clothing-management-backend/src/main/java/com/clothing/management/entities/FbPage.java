package com.clothing.management.entities;

import javax.persistence.*;
import java.util.List;

@Entity
public class FbPage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @OneToMany(mappedBy = "fbPage")
    List<Packet> packets;

    public FbPage(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Packet> getPackets() {
        return packets;
    }

    public void setPackets(List<Packet> packets) {
        this.packets = packets;
    }

    @Override
    public String toString() {
        return "FbPage{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", packets=" + packets +
                '}';
    }
}
