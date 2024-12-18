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
            "SUM(CASE WHEN pp.packet.status IN :deliveredStatuses THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pp.packet.status IN :activeAndConfirmedStatuses THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pp.packet.status = :outOfStockStatus THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pp.packet.haveExchange = TRUE THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pp.packet.status = :returnStatus THEN 1 ELSE 0 END)) " +
            "FROM ProductsPacket pp " +
            "WHERE pp.product.model.id = :modelId " +
            "AND pp.packet.status IN :activeConfirmedDeliveredReturnAndOosStatuses " +
            "AND DATE(pp.packet.date) IS NOT NULL " +
            "AND pp.product.color IS NOT NULL " +
            "AND pp.product.size IS NOT NULL " +
            "AND DATE(pp.packet.date) >= DATE(:beginDate) " +
            "AND DATE(pp.packet.date) <= DATE(:endDate) " +
            "GROUP BY pp.product.color, pp.product.size")
    List<SoldProductsDayCountDTO> soldProductsCountByDate(@Param("modelId") Long modelId, @Param("beginDate") String beginDate, @Param("endDate") String endDate, @Param("outOfStockStatus") String outOfStockStatus, @Param("returnStatus") String returnStatus, @Param("deliveredStatuses") List<String> deliveredStatuses,
                                                          @Param("activeAndConfirmedStatuses") List<String> activeAndConfirmedStatuses, @Param("activeConfirmedDeliveredReturnAndOosStatuses") List<String> activeConfirmedDeliveredReturnAndOosStatuses);

    @Query(value = "SELECT NEW com.clothing.management.dto.StatDTO.TableDTO.OfferTableDTO(" +
            "pp.offer.id , " +
            "pp.offer.name , " +
            "COUNT(DISTINCT(CASE WHEN pp.packet.status IN :statusList THEN CONCAT(pp.packet.id, '-', pp.packetOfferId) ELSE NULL END)), " +
            "COUNT(DISTINCT(CASE WHEN pp.packet.status IN :activeAndConfirmedStatuses THEN CONCAT(pp.packet.id, '-', pp.packetOfferId) ELSE NULL END)), " +
            "COUNT(DISTINCT(CASE WHEN pp.packet.status IN :returnStatuses AND pp.packet.exchangeId IS NULL THEN CONCAT(pp.packet.id, '-', pp.packetOfferId) ELSE NULL END)), " +//retour
            "SUM(CASE WHEN pp.packet.status IN :statusList THEN pp.profits ELSE 0 END))" +
            "FROM ProductsPacket pp " +
            "WHERE DATE(pp.packet.date) >= DATE(:beginDate) " +
            "AND DATE(pp.packet.date) <= DATE(:endDate) " +
            "AND pp.packet.status IN :activeConfirmedDeliveredAndReturnStatuses " +
            "GROUP BY pp.offer.id ")
    List<OfferTableDTO> statOffersTable(@Param("beginDate") String beginDate, @Param("endDate") String endDate, @Param("statusList") List<String> statusList, @Param("returnStatuses") List<String> returnStatuses,
                                        @Param("activeAndConfirmedStatuses") List<String> activeAndConfirmedStatuses, @Param("activeConfirmedDeliveredAndReturnStatuses") List<String> activeConfirmedDeliveredAndReturnStatuses);

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
            "SUM(CASE WHEN pp.packet.status IN :status THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pp.packet.status IN :activeAndConfirmedStatuses THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pp.packet.status IN :returnStatuses AND pp.packet.exchangeId IS NULL THEN 1 ELSE 0 END), " +//retour
            "SUM(CASE WHEN pp.packet.status IN :status THEN pp.profits ELSE 0 END))" +
            "FROM ProductsPacket pp " +
            "WHERE DATE(pp.packet.date) >= DATE(:beginDate) " +
            "AND DATE(pp.packet.date) <= DATE(:endDate) " +
            "AND pp.packet.status IN :activeConfirmedDeliveredAndReturnStatuses " +
            "GROUP BY pp.packet.fbPage.name ")
    List<PageTableDTO> statAllPages(@Param("beginDate") String beginDate, @Param("endDate") String endDate, @Param("status") List<String> status,
                                    @Param("activeAndConfirmedStatuses") List<String> activeAndConfirmedStatuses, @Param("returnStatuses") List<String> returnStatuses, @Param("activeConfirmedDeliveredAndReturnStatuses") List<String> activeConfirmedDeliveredAndReturnStatuses);


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
            "SUM(CASE WHEN pp.packet.status IN :deliveredStatuses THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pp.packet.status IN :activeAndConfirmedStatuses THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pp.packet.status = :outOfStockStatus THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pp.packet.status IN :returnStatuses AND pp.packet.exchangeId IS NULL THEN 1 ELSE 0 END), " +//retour
            "SUM(CASE WHEN pp.packet.status IN :deliveredStatuses THEN pp.profits ELSE 0 END)) " +
            "FROM ProductsPacket pp " +
            "WHERE DATE(pp.packet.date) >= DATE(:beginDate) " +
            "AND DATE(pp.packet.date) <= DATE(:endDate) " +
            "AND pp.product.model.id = :modelId " +
            "AND pp.packet.status IN :activeConfirmedDeliveredReturnAndOosStatuses " +
            "GROUP BY pp.product.color.name, pp.product.size.reference ORDER BY DATE(pp.packet.date) ASC ")
    List<ProductsDayCountDTO> productsCountByDate(@Param("modelId") Long modelId,@Param("beginDate") String beginDate, @Param("endDate") String endDate, @Param("outOfStockStatus") String outOfStockStatus, @Param("returnStatuses") List<String> returnStatuses, @Param("deliveredStatuses") List<String> deliveredStatuses,
                                                  @Param("activeAndConfirmedStatuses") List<String> activeAndConfirmedStatuses, @Param("activeConfirmedDeliveredReturnAndOosStatuses") List<String> activeConfirmedDeliveredReturnAndOosStatuses);

    @Query(value = "SELECT NEW com.clothing.management.dto.StatDTO.ColorsDayCountDTO(" +
            "DATE(pp.packet.date), " +
            "pp.product.color, "+
            "SUM(CASE WHEN pp.packet.status IN :deliveredStatuses THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pp.packet.status IN :activeAndConfirmedStatuses THEN 1 ELSE 0 END))" +
            "FROM ProductsPacket pp " +
            "WHERE DATE(pp.packet.date) >= DATE(:beginDate) " +
            "AND DATE(pp.packet.date) <= DATE(:endDate) " +
            "AND pp.packet.status IN :activeConfirmedAndDeliveredStatuses " +
            "GROUP BY pp.product.color.id, DATE(pp.packet.date) " +
            "ORDER BY DATE(pp.packet.date) ASC")
    List<ColorsDayCountDTO> statAllModelsByColor(@Param("beginDate") String beginDate, @Param("endDate") String endDate, @Param("deliveredStatuses") List<String> deliveredStatuses,
                                                 @Param("activeAndConfirmedStatuses") List<String> activeAndConfirmedStatuses, @Param("activeConfirmedAndDeliveredStatuses") List<String> activeConfirmedAndDeliveredStatuses);

    @Query(value = "SELECT NEW com.clothing.management.dto.StatDTO.TableDTO.ModelTableDTO(" +
            "pp.product.model, " +
            "SUM(CASE WHEN pp.packet.status IN :statusList THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pp.packet.status IN :activeAndConfirmedStatuses THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pp.packet.status = :returnStatus AND pp.packet.exchangeId IS NULL THEN 1 ELSE 0 END), " +//retour
            "SUM(CASE WHEN pp.packet.status = :returnReceivedStatus AND pp.packet.exchangeId IS NULL THEN 1 ELSE 0 END), " +//retour
            "SUM(CASE WHEN pp.packet.status = :outOfStockStatus THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pp.packet.status IN :statusList THEN pp.profits ELSE 0 END))" +
            "FROM ProductsPacket pp " +
            "WHERE DATE(pp.packet.date) >= DATE(:beginDate) " +
            "AND DATE(pp.packet.date) <= DATE(:endDate) " +
            "AND pp.packet.status IN :activeConfirmedDeliveredReturnAndOosStatuses " +
            "GROUP BY pp.product.model.id")
    List<ModelTableDTO> statAllModels(@Param("beginDate") String beginDate, @Param("endDate") String endDate, @Param("statusList") List<String> statusList, @Param("outOfStockStatus") String outOfStockStatus, @Param("returnStatus") String returnStatus,
                                      @Param("returnReceivedStatus") String returnReceivedStatus, @Param("activeAndConfirmedStatuses") List<String> activeAndConfirmedStatuses, @Param("activeConfirmedDeliveredReturnAndOosStatuses") List<String> activeConfirmedDeliveredReturnAndOosStatuses);

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
            "SUM(CASE WHEN p.status = :deliveredStatus THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN p.status = :paidStatus THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN p.valid = true AND (p.haveExchange = false OR p.status IN :activeAndDeliveredStatuses) AND p.status <> :canceledStatus THEN 1 ELSE 0 END), " +// out
            "SUM(CASE WHEN p.haveExchange = true AND p.status IN :returnStatuses THEN 1 ELSE 0 END), " +//echange
            "SUM(CASE WHEN p.status IN :returnStatuses AND p.haveExchange = false THEN 1 ELSE 0 END), " +//retour
            "SUM(CASE WHEN p.status = :outOfStockStatus THEN 1 ELSE 0 END), " +//En rupture
            "SUM(CASE WHEN p.status IN :activeStatuses THEN 1 ELSE 0 END), " +
            "SUM(1)) " +//tout
            "FROM Packet p " +
            "WHERE DATE(p.date) >= DATE(:beginDate) " +
            "AND DATE(p.date) <= DATE(:endDate) " +
            "AND p.deliveryCompany.name = :deliveryCompanyName " +
            "GROUP BY DATE(p.date) ORDER BY DATE(p.date) ASC")
    List<PacketsStatCountDTO> statAllPackets(@Param("beginDate") String beginDate, @Param("endDate") String endDate, @Param("deliveryCompanyName") String deliveryCompanyName, @Param("deliveredStatus") String deliveredStatus, @Param("paidStatus") String paidStatus,
                                             @Param("canceledStatus") String canceledStatus, @Param("outOfStockStatus") String outOfStockStatus, @Param("returnStatuses") List<String> returnStatuses,
                                             @Param("activeStatuses") List<String> activeStatuses, @Param("activeAndDeliveredStatuses") List<String> activeAndDeliveredStatuses);


    @Query(value = "SELECT NEW com.clothing.management.dto.StatDTO.PacketsStatCountDTO(" +
            "DATE(p.date), " +
            "SUM(CASE WHEN p.status = :deliveredStatus THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN p.status = :paidStatus THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN p.valid = true AND (p.haveExchange = false OR p.status IN :activeAndDeliveredStatuses) AND p.status <> :canceledStatus THEN 1 ELSE 0 END), " +// out
            "SUM(CASE WHEN p.haveExchange = true AND p.status IN :returnStatuses THEN 1 ELSE 0 END), " +//echange
            "SUM(CASE WHEN p.status IN :returnStatuses AND p.haveExchange = false THEN 1 ELSE 0 END), " +//retour
            "SUM(CASE WHEN p.status = :outOfStockStatus THEN 1 ELSE 0 END), " +//En rupture
            "SUM(CASE WHEN p.status IN :activeStatuses THEN 1 ELSE 0 END), " +
            "SUM(1)) " +//tout
            "FROM Packet p " +
            "WHERE DATE(p.date) >= DATE(:beginDate) " +
            "AND DATE(p.date) <= DATE(:endDate) " +
            "GROUP BY DATE(p.date) ORDER BY DATE(p.date) ASC")
    List<PacketsStatCountDTO> statAllPackets(@Param("beginDate") String beginDate, @Param("endDate") String endDate, @Param("deliveredStatus") String deliveredStatus, @Param("paidStatus") String paidStatus,
                                             @Param("canceledStatus") String canceledStatus, @Param("outOfStockStatus") String outOfStockStatus, @Param("returnStatuses") List<String> returnStatuses, @Param("activeStatuses") List<String> activeStatuses,
                                             @Param("activeAndDeliveredStatuses") List<String> activeAndDeliveredStatuses);

    @Query(value = "SELECT NEW com.clothing.management.dto.StatDTO.ProductDayCountDTO(" +
            "DATE(pp.packet.date), pp.product.id, " +
            "pp.product.color , pp.product.size , " +
            "SUM(CASE WHEN pp.packet.status IN :deliveredStatuses THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pp.packet.status IN :activeAndConfirmedStatuses THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN pp.packet.status IN :returnStatuses AND pp.packet.haveExchange = false THEN 1 ELSE 0 END))" +
            "FROM ProductsPacket pp " +
            "WHERE DATE(pp.packet.date) >= DATE(:beginDate) AND DATE(pp.packet.date) <= DATE(:endDate) " +
            "AND pp.product.model.id = :modelId " +
            "AND pp.packet.status IN :activeConfirmedDeliveredAndReturnStatuses " +
            "GROUP BY pp.product.id, DATE(pp.packet.date) ORDER BY DATE(pp.packet.date) ASC")
    List<ProductDayCountDTO> statModelSoldProgress(@Param("modelId") Long modelId, @Param("beginDate") String beginDate,
                                                   @Param("endDate") String endDate, @Param("deliveredStatuses") List<String> deliveredStatuses,
                                                   @Param("activeAndConfirmedStatuses") List<String> activeAndConfirmedStatuses, @Param("returnStatuses") List<String> returnStatuses, @Param("activeConfirmedDeliveredAndReturnStatuses") List<String> activeConfirmedDeliveredAndReturnStatuses);

    @Query("SELECT COUNT(*) FROM ProductsPacket pp where pp.product.model.id = :id AND pp.packet.status IN :activeConfirmedAndDeliveredStatuses")
    long countProductsPacketByModelId(@Param("id") Long id, @Param("activeConfirmedAndDeliveredStatuses") List<String> activeConfirmedAndDeliveredStatuses);

    @Query("SELECT COUNT(*) FROM ProductsPacket pp where pp.product.id = :id AND pp.packet.status IN :activeConfirmedAndDeliveredStatuses")
    long countProductsPacketByProductId(@Param("id") Long id, @Param("activeConfirmedAndDeliveredStatuses") List<String> activeConfirmedAndDeliveredStatuses);

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
            "    AND pp.packet.status NOT IN :activeConfirmedAndDeliveredAStatuses " +
            ")")
    int updatePacketsByOfferId(long offerId, List<String> activeConfirmedAndDeliveredAStatuses);
}
