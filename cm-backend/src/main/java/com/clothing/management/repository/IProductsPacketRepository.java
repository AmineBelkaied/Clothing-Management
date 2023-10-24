package com.clothing.management.repository;

import com.clothing.management.dto.ProductsDayCountDTO;
import com.clothing.management.entities.ProductsPacket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IProductsPacketRepository extends JpaRepository<ProductsPacket , Long> {

    @Query(value = "select * from products_packet  where packet_id = :packetId", nativeQuery = true)
    public List<ProductsPacket> findByPacketId(Long packetId);

    @Query(value = "SELECT NEW com.clothing.management.dto.ProductsDayCountDTO(DATE(p.packetDate), p.product.id, p.product.model.id , p.product.color.name , p.product.size.reference , COUNT(p.product.id))" +
            "FROM ProductsPacket p WHERE (p.status = 1 OR p.status = 2) " +
            "AND p.product.color.name <> '?' AND p.product.size.reference <> '?' " +
            "AND DATE(p.packetDate) >= Date(:beginDate) " +
            "AND DATE(p.packetDate) <= DATE(:endDate) " +
            "AND p.product.model.id = :modelId " +
            "GROUP BY p.product.color.name, p.product.size.reference ORDER BY p.product.id ASC ")
    public List<ProductsDayCountDTO> productsCountByDate(@Param("modelId") Long modelId, @Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Query(value = "SELECT NEW com.clothing.management.dto.ProductsDayCountDTO(DATE(p.packetDate), p.product.id, p.product.model.id , p.product.color.name , p.product.size.reference , COUNT(p.product.id))" +
            "FROM ProductsPacket p WHERE (p.status = 1 OR p.status = 2) " +
            "AND p.product.color.name <> '?' AND p.product.size.reference <> '?' " +
            "AND DATE(p.packetDate) >= Date(:beginDate) " +
            "AND DATE(p.packetDate) <= DATE(:endDate) " +
            "AND p.product.model.id = :modelId "+
            "GROUP BY p.product.id,Date(p.packetDate) ORDER BY p.packetDate ASC ")
    public List<ProductsDayCountDTO> statModelSold(@Param("modelId")  Long modelId,@Param("beginDate") String beginDate, @Param("endDate") String endDate);
}
