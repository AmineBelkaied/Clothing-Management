package com.clothing.management.repository;
import com.clothing.management.dto.PacketsStatCountDTO;
import com.clothing.management.dto.ProductsDayCountDTO;
import com.clothing.management.entities.Color;
import com.clothing.management.entities.Offer;
import com.clothing.management.entities.ProductsPacket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface IProductsPacketRepository extends JpaRepository<ProductsPacket , Long> {

    @Transactional
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
            "DATE(pp.packet.date), pp.product.id, " +
            "pp.offer, pp.product.model.id ,  pp.product.model.name, " +
            "pp.product.color , pp.product.size , " +
            "SUM(CASE WHEN pp.packet.status IN ('Livrée', 'Payée') THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pp.packet.status IN ('En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier') THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pp.packet.status = 'En rupture' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN (pp.packet.status = 'Retour' OR pp.packet.status = 'Retour reçu') AND pp.packet.exchangeId IS NULL THEN 1 ELSE 0 END), " +//retour
            "SUM(CASE WHEN pp.packet.status IN ('Livrée', 'Payée') THEN pp.profits ELSE 0 END)) " +
            "FROM ProductsPacket pp " +
            "WHERE pp.product.color.name <> '?' AND pp.product.size.reference <> '?' " +// (p.status = 1 OR p.status = 2 OR p.packet.status = 'En rupture') " +
            "AND DATE(pp.packet.date) >= DATE(:beginDate) " +
            "AND DATE(pp.packet.date) <= DATE(:endDate) " +
            "AND pp.product.model.id = :modelId " +
            "AND pp.packet.status IN ('En rupture','Livrée', 'Payée','En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier','Retour', 'Retour reçu') " +
            "GROUP BY pp.product.color.name, pp.product.size.reference ORDER BY DATE(pp.packet.date) ASC ")
    public List<ProductsDayCountDTO> productsCountByDate(@Param("modelId") Long modelId,@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Query(value = "SELECT NEW com.clothing.management.dto.ProductsDayCountDTO(" +
            "DATE(pp.packet.date), " +
            "pp.packet.id , " +
            "pp.offer , " +
            "pp.packetOfferId, " +
            "SUM(CASE WHEN pp.packet.status IN ('Livrée', 'Payée') THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pp.packet.status IN ('En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier') THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN (pp.packet.status = 'Retour' OR pp.packet.status = 'Retour reçu') AND pp.packet.exchangeId IS NULL THEN 1 ELSE 0 END)," +
            "SUM(CASE WHEN pp.packet.status IN ('Livrée', 'Payée') THEN pp.profits ELSE 0 END))" +
            "FROM ProductsPacket pp " +
            "WHERE DATE(pp.packet.date) >= DATE(:beginDate) " +//(p.status = 1 OR p.status = 2 OR p.packet.status = 'En rupture') " +
            "AND DATE(pp.packet.date) <= DATE(:endDate) " +
            "AND pp.packet.status IN ('Livrée', 'Payée','En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier','Retour', 'Retour reçu') " +
            "GROUP BY DATE(pp.packet.date), pp.packet.id, pp.offer, pp.packetOfferId ORDER BY DATE(pp.packet.date) ASC ")
    public List<ProductsDayCountDTO> offersCountByDate(@Param("beginDate") String beginDate, @Param("endDate") String endDate);


   @Query(value = "SELECT NEW com.clothing.management.dto.ProductsDayCountDTO(" +
           "DATE(pp.packet.date), pp.packet.id, " +
           "pp.offer, pp.product.model.id, pp.product.model.name, " +
           "pp.product.color, pp.product.size, " +
           "SUM(CASE WHEN pp.packet.status IN ('Livrée', 'Payée') THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN pp.packet.status IN ('En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier') THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN (pp.packet.status = 'Retour' OR pp.packet.status = 'Retour reçu') AND pp.packet.exchangeId IS NULL THEN 1 ELSE 0 END), " +//retour
           "SUM(CASE WHEN pp.packet.status IN ('Livrée', 'Payée') THEN pp.profits ELSE 0 END))" +
           "FROM ProductsPacket pp " +
           "WHERE DATE(pp.packet.date) >= DATE(:beginDate) " +
           "AND DATE(pp.packet.date) <= DATE(:endDate) " +
           "AND pp.packet.status IN ('Livrée', 'Payée','En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier','Retour', 'Retour reçu') " +
           "GROUP BY DATE(pp.packet.date), pp.product.model.id " +
           "ORDER BY DATE(pp.packet.date) ASC")

    public List<ProductsDayCountDTO> statAllModels(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Query(value = "SELECT NEW com.clothing.management.dto.ProductsDayCountDTO(" +
            "DATE(pp.packet.date), pp.product.id, " +
            "pp.offer, pp.product.model.id , pp.product.model.name, " +
            "pp.product.color , pp.product.size, "+
            "SUM(CASE WHEN pp.packet.status IN ('Livrée', 'Payée') THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pp.packet.status IN ('En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier') THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN (pp.packet.status = 'Retour' OR pp.packet.status = 'Retour reçu') AND pp.packet.exchangeId IS NULL THEN 1 ELSE 0 END), " +//retour
            "SUM(CASE WHEN pp.packet.status IN ('Livrée', 'Payée') THEN pp.profits ELSE 0 END))" +
            "FROM ProductsPacket pp " +
            "WHERE pp.product.model.id IN :modelIds " +
            "AND DATE(pp.packet.date) >= DATE(:beginDate) " +
            "AND DATE(pp.packet.date) <= DATE(:endDate) " +
            "AND pp.packet.status IN ('Livrée', 'Payée','En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier','Retour', 'Retour reçu') " +
            "GROUP BY pp.product.color.id, DATE(pp.packet.date) ORDER BY DATE(pp.packet.date) ASC")
    public List<ProductsDayCountDTO> statByColorAndModels(@Param("beginDate") String beginDate, @Param("endDate") String endDate, @Param("modelIds") List<Long> modelIds);


    @Query(value = "SELECT NEW com.clothing.management.dto.ProductsDayCountDTO(" +
            "DATE(pp.packet.date), pp.product.id, " +
            "pp.offer, pp.product.model.id , pp.product.model.name, " +
            "pp.product.color , pp.product.size, "+
            "SUM(CASE WHEN pp.packet.status IN ('Livrée', 'Payée') THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pp.packet.status IN ('En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier') THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN (pp.packet.status = 'Retour' OR pp.packet.status = 'Retour reçu') AND pp.packet.exchangeId IS NULL THEN 1 ELSE 0 END), " +//retour
            "SUM(CASE WHEN pp.packet.status IN ('Livrée', 'Payée') THEN pp.profits ELSE 0 END))" +
            "FROM ProductsPacket pp " +
            "WHERE DATE(pp.packet.date) >= DATE(:beginDate) " +
            "AND DATE(pp.packet.date) <= DATE(:endDate) " +
            "AND pp.packet.status IN ('Livrée', 'Payée','En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier','Retour', 'Retour reçu') " +
            "GROUP BY pp.product.color.id, DATE(pp.packet.date) " +
            "ORDER BY DATE(pp.packet.date) ASC")
    public List<ProductsDayCountDTO> statAllModelsByColor(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    //(Date date, Long countPayed, Long countOut, Long countExchange, Long countReturn, Long countOos, Long countAll)
    @Query(value = "SELECT NEW com.clothing.management.dto.PacketsStatCountDTO(" +
            "DATE(p.date), " +
            "SUM(CASE WHEN p.status IN ('Livrée', 'Payée') THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN p.valid = true AND (p.haveExchange = false OR p.status IN ('En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier','Livrée', 'Payée')) AND p.status <> 'Annuler' THEN 1 ELSE 0 END), " +// out
            "SUM(CASE WHEN p.haveExchange = true AND p.status IN ('Retour', 'Retour reçu') THEN 1 ELSE 0 END), " +//echange
            "SUM(CASE WHEN p.status IN ('Retour', 'Retour reçu') AND p.haveExchange = false THEN 1 ELSE 0 END), " +//retour
            "SUM(CASE WHEN p.status = 'En rupture' THEN 1 ELSE 0 END), " +//En rupture
            "SUM(CASE WHEN p.status IN ('En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier') THEN 1 ELSE 0 END), " +
            "SUM(1)) " +//tout
            "FROM Packet p " +
            "WHERE DATE(p.date) >= DATE(:beginDate) " +
            "AND DATE(p.date) <= DATE(:endDate) " +
            "AND p.deliveryCompany.name = :deliveryCompanyName " +
            "GROUP BY DATE(p.date) ORDER BY DATE(p.date) ASC")
    public List<PacketsStatCountDTO> statAllPackets(@Param("beginDate") String beginDate, @Param("endDate") String endDate,@Param("deliveryCompanyName") String deliveryCompanyName);


    @Query(value = "SELECT NEW com.clothing.management.dto.PacketsStatCountDTO(" +
            "DATE(p.date), " +
            "SUM(CASE WHEN p.status IN ('Livrée', 'Payée') THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN p.valid = true AND (p.haveExchange = false OR p.status IN ('En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier','Livrée', 'Payée')) AND p.status <> 'Annuler' THEN 1 ELSE 0 END), " +// out
            "SUM(CASE WHEN p.haveExchange = true AND p.status IN ('Retour', 'Retour reçu') THEN 1 ELSE 0 END), " +//echange
            "SUM(CASE WHEN p.status IN ('Retour', 'Retour reçu') AND p.haveExchange = false THEN 1 ELSE 0 END), " +//retour
            "SUM(CASE WHEN p.status = 'En rupture' THEN 1 ELSE 0 END), " +//En rupture
            "SUM(CASE WHEN p.status IN ('En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier') THEN 1 ELSE 0 END), " +
            "SUM(1)) " +//tout
            "FROM Packet p " +
            "WHERE DATE(p.date) >= DATE(:beginDate) " +
            "AND DATE(p.date) <= DATE(:endDate) " +
            "GROUP BY DATE(p.date) ORDER BY DATE(p.date) ASC")
    public List<PacketsStatCountDTO> statAllPackets(@Param("beginDate") String beginDate, @Param("endDate") String endDate);


    @Query(value = "SELECT NEW com.clothing.management.dto.ProductsDayCountDTO(" +
            "DATE(pp.packet.date), pp.product.id, " +
            "pp.offer, pp.product.model.id ,  pp.product.model.name, " +
            "pp.product.color , pp.product.size , " +
            "SUM(CASE WHEN pp.packet.status IN ('Livrée', 'Payée') THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pp.packet.status IN ('En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier') THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN (pp.packet.status = 'Retour' OR pp.packet.status = 'Retour reçu') AND pp.packet.haveExchange = false THEN 1 ELSE 0 END), " +//retour
            "SUM(CASE WHEN pp.packet.status IN ('Livrée', 'Payée') THEN pp.profits ELSE 0 END))" +
            "FROM ProductsPacket pp " +
            "WHERE pp.product.color.name <> '?' AND pp.product.size.reference <> '?' " +
            "AND DATE(pp.packet.date) >= DATE(:beginDate) AND DATE(pp.packet.date) <= DATE(:endDate) " +
            "AND pp.product.model.id = :modelId " +
            "AND pp.packet.status IN ('Livrée', 'Payée','En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier','Retour', 'Retour reçu') " +
            "GROUP BY pp.product.id, DATE(pp.packet.date) ORDER BY DATE(pp.packet.date) ASC")
    public List<ProductsDayCountDTO> statModelSoldProgress(@Param("modelId") Long modelId, @Param("beginDate") String beginDate, @Param("endDate") String endDate);

}
