package com.clothing.management.repository;
import com.clothing.management.dto.StatDTO.*;
import com.clothing.management.dto.StatDTO.ChartDTO.ChartDTO;
import com.clothing.management.dto.StatDTO.TableDTO.ModelTableDTO;
import com.clothing.management.dto.StatDTO.TableDTO.OfferTableDTO;
import com.clothing.management.dto.StatDTO.TableDTO.PageTableDTO;
import com.clothing.management.entities.ProductsPacket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface IProductsPacketRepository extends JpaRepository<ProductsPacket , Long> {
    @Transactional
    @Query(value = "select * from products_packet  where packet_id = :packetId", nativeQuery = true)
    List<ProductsPacket> findByPacketId(Long packetId);

    @Query(value = "SELECT NEW com.clothing.management.dto.StatDTO.SoldProductsDayCountDTO( " +
            "pp.product.id, " +
            "pp.product.color.id , pp.product.size.id , " +
            "pp.product.quantity, " +
            "SUM(CASE WHEN pp.packet.status IN ('Livrée', 'Payée') THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pp.packet.status IN ('Confirmée','En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier') THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pp.packet.status = 'En rupture' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pp.packet.haveExchange = TRUE THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pp.packet.status = 'Retour' THEN 1 ELSE 0 END)) " +
            "FROM ProductsPacket pp " +
            "WHERE pp.product.model.id = :modelId " +
            "AND pp.packet.status IN ('Livrée', 'Payée','Confirmée','En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier','En rupture','Retour','Retour reçu') " +
            "AND DATE(pp.packet.date) IS NOT NULL " +
            "AND pp.product.color IS NOT NULL " +
            "AND pp.product.size IS NOT NULL " +
            "AND DATE(pp.packet.date) >= DATE(:beginDate) " +
            "AND DATE(pp.packet.date) <= DATE(:endDate) " +
            "GROUP BY pp.product.color, pp.product.size")
    List<SoldProductsDayCountDTO> soldProductsCountByDate(@Param("modelId") Long modelId,@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Query(value = "SELECT NEW com.clothing.management.dto.StatDTO.TableDTO.OfferTableDTO(" +
            "pp.offer.id , " +
            "pp.offer.name , " +
            "COUNT(DISTINCT(CASE WHEN pp.packet.status IN :statusList THEN CONCAT(pp.packet.id, '-', pp.packetOfferId) ELSE NULL END)), " +
            "COUNT(DISTINCT(CASE WHEN pp.packet.status IN ('Confirmée','En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier') THEN CONCAT(pp.packet.id, '-', pp.packetOfferId) ELSE NULL END)), " +
            "COUNT(DISTINCT(CASE WHEN (pp.packet.status = 'Retour' OR pp.packet.status = 'Retour reçu') AND pp.packet.exchangeId IS NULL THEN CONCAT(pp.packet.id, '-', pp.packetOfferId) ELSE NULL END)), " +//retour
            "SUM(CASE WHEN pp.packet.status IN :statusList THEN pp.profits ELSE 0 END))" +
            "FROM ProductsPacket pp " +
            "WHERE DATE(pp.packet.date) >= DATE(:beginDate) " +
            "AND DATE(pp.packet.date) <= DATE(:endDate) " +
            "AND pp.packet.status IN ('Livrée', 'Payée','Confirmée','En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier','Retour', 'Retour reçu') " +
            "GROUP BY pp.offer.id ")
    List<OfferTableDTO> statOffersTable(@Param("beginDate") String beginDate, @Param("endDate") String endDate, @Param("statusList") List<String> statusList);

    @Query(value = "SELECT NEW com.clothing.management.dto.StatDTO.ChartDTO.ChartDTO(" +
            "DATE(pp.packet.date), " +
            "pp.offer.id, " +
            "pp.offer.name, " +
            "CASE WHEN pp.packet.status IN :statusList THEN 1 ELSE 0 END) " +
            "FROM ProductsPacket pp " +
            "WHERE DATE(pp.packet.date) >= DATE(:beginDate) " +
            "AND DATE(pp.packet.date) <= DATE(:endDate) " +
            "AND pp.packet.status IN :statusList " +
            "GROUP BY DATE(pp.packet.date), pp.packet.id, pp.offer.id, pp.packetOfferId " +
            "ORDER BY DATE(pp.packet.date) ASC")
    List<ChartDTO> statOffersChart(@Param("beginDate") String beginDate, @Param("endDate") String endDate, @Param("statusList") List<String> statusList);

    @Query(value = "SELECT NEW com.clothing.management.dto.StatDTO.TableDTO.PageTableDTO(" +
            "pp.packet.fbPage.id, " +
            "pp.packet.fbPage.name, " +
            "SUM(CASE WHEN pp.packet.status IN :statusList THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pp.packet.status IN ('Confirmée','En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier') THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN (pp.packet.status = 'Retour' OR pp.packet.status = 'Retour reçu') AND pp.packet.exchangeId IS NULL THEN 1 ELSE 0 END), " +//retour
            "SUM(CASE WHEN pp.packet.status IN :statusList THEN pp.profits ELSE 0 END))" +
            "FROM ProductsPacket pp " +
            "WHERE DATE(pp.packet.date) >= DATE(:beginDate) " +
            "AND DATE(pp.packet.date) <= DATE(:endDate) " +
            "AND pp.packet.status IN ('Livrée', 'Payée','Confirmée','En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier','Retour', 'Retour reçu') " +
            "GROUP BY pp.packet.fbPage.name ")
    List<PageTableDTO> statAllPages(@Param("beginDate") String beginDate, @Param("endDate") String endDate, @Param("statusList") List<String> statusList);




    @Query(value = "SELECT NEW com.clothing.management.dto.StatDTO.ChartDTO.ChartDTO(" +
            "DATE(pp.packet.date), " +
            "pp.packet.fbPage.id, " +
            "pp.packet.fbPage.name, " +
            "CASE WHEN pp.packet.status IN :statusList THEN 1 ELSE 0 END) " +
            "FROM ProductsPacket pp " +
            "WHERE DATE(pp.packet.date) >= DATE(:beginDate) " +
            "AND DATE(pp.packet.date) <= DATE(:endDate) " +
            "AND pp.packet.status IN :statusList " +
            "GROUP BY DATE(pp.packet.date), pp.packet.id, pp.packet.fbPage.id " +
            "ORDER BY DATE(pp.packet.date) ASC")
    List<ChartDTO> statPagesChart(@Param("beginDate") String beginDate, @Param("endDate") String endDate, @Param("statusList") List<String> statusList);

    @Query(value = "SELECT NEW com.clothing.management.dto.StatDTO.ProductsDayCountDTO( " +
            "DATE(pp.packet.date), pp.product.id, " +
            "pp.product.model.id ,  pp.product.model.name, " +
            "pp.product.color , pp.product.size , " +
            "SUM(CASE WHEN pp.packet.status IN ('Livrée', 'Payée') THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pp.packet.status IN ('Confirmée','En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier') THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pp.packet.status = 'En rupture' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN (pp.packet.status = 'Retour' OR pp.packet.status = 'Retour reçu') AND pp.packet.exchangeId IS NULL THEN 1 ELSE 0 END), " +//retour
            "SUM(CASE WHEN pp.packet.status IN ('Livrée', 'Payée') THEN pp.profits ELSE 0 END)) " +
            "FROM ProductsPacket pp " +
            "WHERE DATE(pp.packet.date) >= DATE(:beginDate) " +
            "AND DATE(pp.packet.date) <= DATE(:endDate) " +
            "AND pp.product.model.id = :modelId " +
            "AND pp.packet.status IN ('Livrée', 'Payée','Confirmée','En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier','En rupture','Retour', 'Retour reçu') " +
            "GROUP BY pp.product.color.name, pp.product.size.reference ORDER BY DATE(pp.packet.date) ASC ")
    List<ProductsDayCountDTO> productsCountByDate(@Param("modelId") Long modelId,@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Query(value = "SELECT NEW com.clothing.management.dto.StatDTO.ColorsDayCountDTO(" +
            "DATE(pp.packet.date), " +
            "pp.product.color, "+
            "SUM(CASE WHEN pp.packet.status IN ('Livrée', 'Payée') THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pp.packet.status IN ('Confirmée','En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier') THEN 1 ELSE 0 END))" +
            "FROM ProductsPacket pp " +
            "WHERE DATE(pp.packet.date) >= DATE(:beginDate) " +
            "AND DATE(pp.packet.date) <= DATE(:endDate) " +
            "AND pp.packet.status IN ('Livrée', 'Payée','Confirmée','En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier') " +
            "GROUP BY pp.product.color.id, DATE(pp.packet.date) " +
            "ORDER BY DATE(pp.packet.date) ASC")
    List<ColorsDayCountDTO> statAllModelsByColor(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Query(value = "SELECT NEW com.clothing.management.dto.StatDTO.TableDTO.ModelTableDTO(" +
            "pp.product.model, " +
            "SUM(CASE WHEN pp.packet.status IN :statusList THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pp.packet.status IN ('Confirmée','En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier') THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pp.packet.status = 'Retour' AND pp.packet.exchangeId IS NULL THEN 1 ELSE 0 END), " +//retour
            "SUM(CASE WHEN pp.packet.status = 'Retour reçu' AND pp.packet.exchangeId IS NULL THEN 1 ELSE 0 END), " +//retour
            "SUM(CASE WHEN pp.packet.status = 'En rupture' THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pp.packet.status IN :statusList THEN pp.profits ELSE 0 END))" +
            "FROM ProductsPacket pp " +
            "WHERE DATE(pp.packet.date) >= DATE(:beginDate) " +
            "AND DATE(pp.packet.date) <= DATE(:endDate) " +
            "AND pp.packet.status IN ('Livrée', 'Payée','Confirmée','En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier','Retour', 'Retour reçu','En rupture') " +
            "GROUP BY pp.product.model.id")
    List<ModelTableDTO> statAllModels(@Param("beginDate") String beginDate, @Param("endDate") String endDate, @Param("statusList") List<String> statusList);

    @Query(value = "SELECT NEW com.clothing.management.dto.StatDTO.ChartDTO.ChartDTO(" +
            "DATE(pp.packet.date), " +
            "pp.product.model.id, " +
            "pp.product.model.name, " +
            "SUM(CASE WHEN pp.packet.status IN :statusList THEN 1 ELSE 0 END)) " +
            "FROM ProductsPacket pp " +
            "WHERE DATE(pp.packet.date) >= DATE(:beginDate) " +
            "AND DATE(pp.packet.date) <= DATE(:endDate) " +
            "AND pp.packet.status IN :statusList " +
            "GROUP BY DATE(pp.packet.date), pp.product.model.id " +
            "ORDER BY DATE(pp.packet.date) ASC")
    List<ChartDTO> statModelsChart(@Param("beginDate") String beginDate, @Param("endDate") String endDate, @Param("statusList") List<String> statusList);


    @Query(value = "SELECT NEW com.clothing.management.dto.StatDTO.PacketsStatCountDTO(" +
            "DATE(p.date), " +
            "SUM(CASE WHEN p.status IN ('Livrée') THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN p.status IN ('Payée') THEN 1 ELSE 0 END), " +
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


    @Query(value = "SELECT NEW com.clothing.management.dto.StatDTO.PacketsStatCountDTO(" +
            "DATE(p.date), " +
            "SUM(CASE WHEN p.status IN ('Livrée') THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN p.status IN ('Payée') THEN 1 ELSE 0 END), " +
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

    @Query(value = "SELECT NEW com.clothing.management.dto.StatDTO.ProductDayCountDTO(" +
            "DATE(pp.packet.date), pp.product.id, " +
            "pp.product.color , pp.product.size , " +
            "SUM(CASE WHEN pp.packet.status IN ('Livrée', 'Payée') THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pp.packet.status IN ('Confirmée','En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier') THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN (pp.packet.status = 'Retour' OR pp.packet.status = 'Retour reçu') AND pp.packet.haveExchange = false THEN 1 ELSE 0 END))" +
            "FROM ProductsPacket pp " +
            "WHERE DATE(pp.packet.date) >= DATE(:beginDate) AND DATE(pp.packet.date) <= DATE(:endDate) " +
            "AND pp.product.model.id = :modelId " +
            "AND pp.packet.status IN ('Livrée', 'Payée','Confirmée','En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier','Retour', 'Retour reçu') " +
            "GROUP BY pp.product.id, DATE(pp.packet.date) ORDER BY DATE(pp.packet.date) ASC")
    List<ProductDayCountDTO> statModelSoldProgress(@Param("modelId") Long modelId, @Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Query("SELECT COUNT(*) FROM ProductsPacket pp where pp.product.model.id = :id AND pp.packet.status IN ('Livrée', 'Payée','Confirmée','En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier') ")
    long countProductsPacketByModelId(@Param("id") Long id);

    @Query("SELECT COUNT(*) FROM ProductsPacket pp where pp.product.id = :id AND pp.packet.status IN ('Livrée', 'Payée','Confirmée','En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier') ")
    long countProductsPacketByProductId(@Param("id") Long id);

    Long countProductsPacketByOfferId(Long id);

    Long countProductsPacketByProduct_Color_Id(Long id);

    Long countProductsPacketByProduct_Size_Id(Long id);

    @Modifying
    @Transactional
    @Query("UPDATE Packet p " +
            "SET p.changedPrice = true " +
            "WHERE p.id IN ( " +
            "    SELECT pp.packet.id " +
            "    FROM ProductsPacket pp " +
            "    WHERE pp.offer.id = :offerId " +
            "    AND pp.packet.status NOT IN ('Livrée', 'Payée', 'Confirmée', 'En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier') " +
            ")")
    int updatePacketsByOfferId(long offerId);
}
