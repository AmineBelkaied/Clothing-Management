package com.clothing.management.repository;
import com.clothing.management.dto.PacketsStatCountDTO;
import com.clothing.management.dto.ProductsDayCountDTO;
import com.clothing.management.entities.ProductsPacket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IProductsPacketRepository extends JpaRepository<ProductsPacket , Long> {

    @Query(value = "select * from products_packet  where packet_id = :packetId", nativeQuery = true)
    public List<ProductsPacket> findByPacketId(Long packetId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE packet p " +
            "SET p.stock = :stock, " +
            "p.status = (CASE WHEN :stock = 0 THEN 'En rupture' " +
            "WHEN (p.stock = 0 AND :stock > 0) THEN 'Non confirmée' " +
            "ELSE p.status " +
            "END) " +
            "WHERE p.id IN :productIds", nativeQuery = true)
    public int updateUnconfirmedPacketStock_By_ProductId(@Param("productIds") List<Long> productIds, @Param("stock") Integer stock);

    @Query(value = "SELECT p.id " +
            "FROM packet p " +
            "JOIN products_packet pp ON pp.packet_id = p.id " +
            "WHERE pp.packet_id = p.id " +
            "AND pp.product_id = :productId " +
            "AND pp.status = 0 " +
            "AND p.status IN ('Non confirmée', 'En Rupture','Injoignable') " +
            "GROUP BY pp.packet_id", nativeQuery = true)
    public List<Long> getUnconfirmedPacketStock_By_ProductId(Long productId);

    @Query(value = "SELECT NEW com.clothing.management.dto.ProductsDayCountDTO( " +
            "DATE(p.packetDate), p.product.id, " +
            "p.offer, p.product.model.id ,  p.product.model.name, " +
            "p.product.color , p.product.size , " +
            "SUM(CASE WHEN p.packet.exchangeId IS NOT NULL THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN p.packet.status = 'En rupture' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN p.status = 1 THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN p.status = 1 OR p.status = 2 THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN p.packet.status = 'En rupture' THEN 0 ELSE 1 END)) " +//En rupture
            "FROM ProductsPacket p " +
            "WHERE (p.status = 1 OR p.status = 2 OR p.packet.status = 'En rupture') " +
            "AND p.product.color.name <> '?' AND p.product.size.reference <> '?' " +
            "AND DATE(p.packetDate) >= DATE(:beginDate) " +
            "AND DATE(p.packetDate) <= DATE(:endDate) " +
            "AND p.product.model.id = :modelId " +
            "GROUP BY p.product.color.name, p.product.size.reference ORDER BY DATE(p.packetDate) ASC ")
    public List<ProductsDayCountDTO> productsCountByDate(@Param("modelId") Long modelId,@Param("beginDate") String beginDate, @Param("endDate") String endDate);
    @Query(value = "SELECT NEW com.clothing.management.dto.ProductsDayCountDTO(" +
            "DATE(p.packetDate), p.offer, " +
            "COUNT(DISTINCT(CASE WHEN p.status = 1 OR (p.status = 2) THEN p.packetOfferId END))" +
            ") " +
            "FROM ProductsPacket p " +
            "WHERE (p.status = 1 OR p.status = 2 OR p.packet.status = 'En rupture') " +
            "AND DATE(p.packetDate) >= DATE(:beginDate) " +
            "AND DATE(p.packetDate) <= DATE(:endDate) " +
            "GROUP BY DATE(p.packetDate), p.packet.id, p.packetOfferId  ORDER BY DATE(p.packetDate) ASC ")
    public List<ProductsDayCountDTO> offersCountByDate(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Query(value = "SELECT NEW com.clothing.management.dto.ProductsDayCountDTO(" +
            "DATE(p.packetDate), p.product.id, " +
            "p.offer, p.product.model.id ,  p.product.model.name, " +
            "p.product.color , p.product.size , " +
            "SUM(CASE WHEN p.packet.exchangeId IS NOT NULL THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN p.packet.status = 'En rupture' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN p.status = 1 THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN p.status = 1 OR p.status = 2 THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN p.packet.status = 'En rupture' THEN 0 ELSE 1 END)) " +//En rupture
            "FROM ProductsPacket p " +
            "WHERE (p.status = 1 OR p.status = 2) " +
            "AND p.product.color.name <> '?' AND p.product.size.reference <> '?' " +
            "AND DATE(p.packetDate) >= DATE(:beginDate) AND DATE(p.packetDate) <= DATE(:endDate) " +
            "AND p.product.model.id = :modelId " +
            "GROUP BY p.product.id, DATE(p.packetDate) ORDER BY DATE(p.packetDate) ASC")
    public List<ProductsDayCountDTO> statModelSoldProgress(@Param("modelId") Long modelId, @Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Query(value = "SELECT NEW com.clothing.management.dto.ProductsDayCountDTO(" +
            "DATE(p.packetDate), p.product.id, " +
            "p.offer, p.product.model.id , p.product.model.name, " +
            "p.product.color , p.product.size, "+
            "SUM(CASE WHEN p.packet.exchangeId IS NOT NULL THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN p.packet.status = 'En rupture' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN p.status = 1 THEN 1 ELSE 0 END), " +
            "COUNT(p), " +
            "COUNT(p)) " +//En rupture
            "FROM ProductsPacket p " +
            "WHERE (p.status = 1 OR p.status = 2) " +
            "AND DATE(p.packetDate) >= DATE(:beginDate) " +
            "AND DATE(p.packetDate) <= DATE(:endDate) " +
            "GROUP BY p.product.model.id, DATE(p.packetDate) ORDER BY DATE(p.packetDate) ASC")
    public List<ProductsDayCountDTO> statAllModels(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Query(value = "SELECT NEW com.clothing.management.dto.ProductsDayCountDTO(" +
            "DATE(p.packetDate), p.product.id, " +
            "p.offer, p.product.model.id , p.product.model.name, " +
            "p.product.color , p.product.size, "+
            "SUM(CASE WHEN p.packet.exchangeId IS NOT NULL THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN p.packet.status = 'En rupture' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN p.status = 1 THEN 1 ELSE 0 END), " +
            "SUM(1), " +
            "SUM(CASE WHEN p.packet.status = 'En rupture' THEN 1 ELSE 0 END)) " +//En rupture
            "FROM ProductsPacket p " +
            "WHERE (p.status = 1 OR p.status = 2) " +
            "AND p.product.model.id IN :modelIds " +
            "AND DATE(p.packetDate) >= DATE(:beginDate) " +
            "AND DATE(p.packetDate) <= DATE(:endDate) " +
            "GROUP BY p.product.color.id, DATE(p.packetDate) ORDER BY DATE(p.packetDate) ASC")
    public List<ProductsDayCountDTO> statByColorAndModels(@Param("beginDate") String beginDate, @Param("endDate") String endDate, @Param("modelIds") List<Long> modelIds);

    @Query(value = "SELECT NEW com.clothing.management.dto.ProductsDayCountDTO(" +
            "DATE(p.packetDate), p.product.id, " +
            "p.offer, p.product.model.id , p.product.model.name, " +
            "p.product.color , p.product.size, "+
            "SUM(CASE WHEN p.packet.exchangeId IS NOT NULL THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN p.packet.status = 'En rupture' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN p.status = 1 THEN 1 ELSE 0 END), " +
            "SUM(1), " +
            "SUM(CASE WHEN p.packet.status = 'En rupture' THEN 1 ELSE 0 END)) " +//En rupture
            "FROM ProductsPacket p " +
            "WHERE (p.status = 1 OR p.status = 2) " +
            "AND DATE(p.packetDate) >= DATE(:beginDate) " +
            "AND DATE(p.packetDate) <= DATE(:endDate) " +
            "GROUP BY p.product.color.id, DATE(p.packetDate) ORDER BY DATE(p.packetDate) ASC")
    public List<ProductsDayCountDTO> statAllModelsByColor(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    //count: number,payed: number, return: number, exchange: number, out: number
    @Query(value = "SELECT NEW com.clothing.management.dto.PacketsStatCountDTO(" +
            "DATE(p.date), " +
            "SUM(CASE WHEN p.status = 'Livrée' OR p.status = 'Payée' THEN 1 ELSE 0 END), " +//payé
            "SUM(CASE WHEN p.valid = true THEN 1 ELSE 0 END), " +// out
            "SUM(CASE WHEN p.exchangeId IS NOT NULL THEN 1 ELSE 0 END), " +//echange
            "SUM(CASE WHEN p.status = 'Retour' OR p.status = 'Retour reçu' THEN 1 ELSE 0 END), " +//retour
            "COUNT(p), " +//tout
            "SUM(CASE WHEN p.status = 'En rupture' THEN 1 ELSE 0 END)) " +//En rupture
            "FROM Packet p " +
            "WHERE DATE(p.date) >= DATE(:beginDate) " +
            "AND DATE(p.date) <= DATE(:endDate) " +
            "GROUP BY DATE(p.date) ORDER BY DATE(p.date) ASC")
    public List<PacketsStatCountDTO> statAllPackets(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

}
