package com.clothing.management.repository;

import com.clothing.management.entities.Packet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPacketRepository extends JpaRepository<Packet, Long> {
}
