package com.clothing.management.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
public class FbPage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String link;
    @JsonIgnore
    @OneToMany(mappedBy = "fbPage")
    List<Packet> packets;

    public FbPage() {
    }

    public FbPage(Long id, String link) {
        this.id = id;
        this.link = link;
    }

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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return "FbPage{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", link='" + link + '\''+
                '}';
    }
}
