package com.clothing.management.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "fb_page", indexes = {
        @Index(name = "idx_id", columnList = "id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"packets","offers"})
public class FbPage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String link;

    @JsonIgnore
    @OneToMany(mappedBy = "fbPage", fetch = FetchType.LAZY)
    private List<Packet> packets;

    @JsonIgnore
    @ManyToMany(mappedBy = "fbPages", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Offer> offers = new HashSet<>();

    @Column(nullable = false)
    private boolean enabled;

    @Builder.Default
    private boolean deleted = false;
}