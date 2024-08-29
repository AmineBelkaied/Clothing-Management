package com.clothing.management.repository;
import com.clothing.management.dto.DayCount.*;
import com.clothing.management.entities.Color;
import com.clothing.management.entities.ProductsPacket;
import com.clothing.management.entities.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface IProductsPacketRepository extends JpaRepository<ProductsPacket , Long> {

    @Transactional
    @Query(value = "select * from products_packet  where packet_id = :packetId", nativeQuery = true)
    List<ProductsPacket> findByPacketId(Long packetId);


    @Query(value = "SELECT NEW com.clothing.management.dto.DayCount.SoldProductsDayCountDTO( " +
            "pp.product.id, " +
            "pp.product.color , pp.product.size , " +
            "pp.product.quantity, " +
            "SUM(CASE WHEN pp.packet.status IN ('Livrée', 'Payée') THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pp.packet.status IN ('En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier') THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pp.packet.status = 'En rupture' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN (pp.packet.status = 'Retour' OR pp.packet.status = 'Retour reçu') AND pp.packet.exchangeId IS NULL THEN 1 ELSE 0 END)) " +
            "FROM ProductsPacket pp " +
            "WHERE pp.product.color.name <> '?' AND pp.product.size.reference <> '?' " +// (p.status = 1 OR p.status = 2 OR p.packet.status = 'En rupture') " +
            "AND DATE(pp.packet.date) >= DATE(:beginDate) " +
            "AND DATE(pp.packet.date) <= DATE(:endDate) " +
            "AND pp.product.model.id = :modelId " +
            "AND pp.packet.status IN ('Livrée', 'Payée','En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier') " +
            "GROUP BY pp.product.color.name, pp.product.size.reference ORDER BY DATE(pp.packet.date) ASC ")
    List<SoldProductsDayCountDTO> soldProductsCountByDate(@Param("modelId") Long modelId,@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Query(value = "SELECT NEW com.clothing.management.dto.DayCount.OffersDayCountDTO(" +
            "DATE(pp.packet.date), " +
            "pp.packet.id , " +
            "pp.offer ," +
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
    List<OffersDayCountDTO> offersCountByDate(@Param("beginDate") String beginDate, @Param("endDate") String endDate);


   @Query(value = "SELECT NEW com.clothing.management.dto.DayCount.ProductsDayCountDTO(" +
           "DATE(pp.packet.date), pp.packet.id, " +
           "pp.product.model.id, pp.product.model.name, " +
           "pp.product.color, pp.product.size, " +
           "SUM(CASE WHEN pp.packet.status IN ('Livrée', 'Payée') THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN pp.packet.status IN ('En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier') THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN pp.packet.status = 'En rupture' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN (pp.packet.status = 'Retour' OR pp.packet.status = 'Retour reçu') AND pp.packet.exchangeId IS NULL THEN 1 ELSE 0 END), " +//retour
           "SUM(CASE WHEN pp.packet.status IN ('Livrée', 'Payée') THEN pp.profits ELSE 0 END))" +
           "FROM ProductsPacket pp " +
           "WHERE DATE(pp.packet.date) >= DATE(:beginDate) " +
           "AND DATE(pp.packet.date) <= DATE(:endDate) " +
           "AND pp.packet.status IN ('Livrée', 'Payée','En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier','Retour', 'Retour reçu') " +
           "GROUP BY DATE(pp.packet.date), pp.product.model.id " +
           "ORDER BY DATE(pp.packet.date) ASC")

    List<ProductsDayCountDTO> statAllModels(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Query(value = "SELECT NEW com.clothing.management.dto.DayCount.ProductsDayCountDTO( " +
            "DATE(pp.packet.date), pp.product.id, " +
            "pp.product.model.id ,  pp.product.model.name, " +
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
    List<ProductsDayCountDTO> productsCountByDate(@Param("modelId") Long modelId,@Param("beginDate") String beginDate, @Param("endDate") String endDate);


    @Query(value = "SELECT NEW com.clothing.management.dto.DayCount.ColorsDayCountDTO(" +
            "DATE(pp.packet.date), " +
            "pp.product.color , "+
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
    List<ColorsDayCountDTO> statByColorAndModels(@Param("beginDate") String beginDate, @Param("endDate") String endDate, @Param("modelIds") List<Long> modelIds);

    //Date packetDate, Color color, countPayed, long countProgress
    @Query(value = "SELECT NEW com.clothing.management.dto.DayCount.ColorsDayCountDTO(" +
            "DATE(pp.packet.date), " +
            "pp.product.color, "+
            "SUM(CASE WHEN pp.packet.status IN ('Livrée', 'Payée') THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pp.packet.status IN ('En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier') THEN 1 ELSE 0 END))" +
            "FROM ProductsPacket pp " +
            "WHERE DATE(pp.packet.date) >= DATE(:beginDate) " +
            "AND DATE(pp.packet.date) <= DATE(:endDate) " +
            "AND pp.packet.status IN ('Livrée', 'Payée','En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier','Retour', 'Retour reçu') " +
            "GROUP BY pp.product.color.id, DATE(pp.packet.date) " +
            "ORDER BY DATE(pp.packet.date) ASC")
    List<ColorsDayCountDTO> statAllModelsByColor(@Param("beginDate") String beginDate, @Param("endDate") String endDate);
    @Query(value = "SELECT NEW com.clothing.management.dto.DayCount.PacketsStatCountDTO(" +
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
    List<PacketsStatCountDTO> statAllPackets(@Param("beginDate") String beginDate, @Param("endDate") String endDate,@Param("deliveryCompanyName") String deliveryCompanyName);

    @Query(value = "SELECT NEW com.clothing.management.dto.DayCount.PacketsStatCountDTO(" +
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
    List<PacketsStatCountDTO> statAllPackets(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Query(value = "SELECT NEW com.clothing.management.dto.DayCount.ModelDayCountDTO(" +
            "DATE(pp.packet.date), pp.product.id, " +
            "pp.product.color , pp.product.size , " +
            "SUM(CASE WHEN pp.packet.status IN ('Livrée', 'Payée') THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pp.packet.status IN ('En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier') THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN (pp.packet.status = 'Retour' OR pp.packet.status = 'Retour reçu') AND pp.packet.haveExchange = false THEN 1 ELSE 0 END))" +
            "FROM ProductsPacket pp " +
            "WHERE pp.product.color.name <> '?' AND pp.product.size.reference <> '?' " +
            "AND DATE(pp.packet.date) >= DATE(:beginDate) AND DATE(pp.packet.date) <= DATE(:endDate) " +
            "AND pp.product.model.id = :modelId " +
            "AND pp.packet.status IN ('Livrée', 'Payée','En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier','Retour', 'Retour reçu') " +
            "GROUP BY pp.product.id, DATE(pp.packet.date) ORDER BY DATE(pp.packet.date) ASC")
    List<ModelDayCountDTO> statModelSoldProgress(@Param("modelId") Long modelId, @Param("beginDate") String beginDate, @Param("endDate") String endDate);

}
