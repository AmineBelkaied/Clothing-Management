package com.clothing.management.repository;
import com.clothing.management.entities.PacketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPacketStatusRepository extends JpaRepository<PacketStatus, Long > {
}
