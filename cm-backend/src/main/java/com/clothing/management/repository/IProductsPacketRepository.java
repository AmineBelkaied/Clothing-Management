package com.clothing.management.repository;

import com.clothing.management.entities.ProductsPacket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IProductsPacketRepository extends JpaRepository<ProductsPacket , Long> {

    @Query(value = "select * from products_packet  where packet_id = :packetId", nativeQuery = true)
    public List<ProductsPacket> findByPacketId(Long packetId);
}
