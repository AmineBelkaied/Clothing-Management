package com.clothing.management.repository;
import com.clothing.management.dto.DayCount.PagesStatCountDTO;
import com.clothing.management.dto.DayCount.StatesStatCountDTO;
import com.clothing.management.entities.Packet;
import com.clothing.management.models.DashboardCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface IPacketRepository extends JpaRepository<Packet, Long> {

    @Query(value=" SELECT * FROM packet p WHERE DATEDIFF(NOW() , p.date) < 2", nativeQuery = true)
    List<Packet> findAllByDate(@Param("date") Date d);

    @Query(value="SELECT p FROM Packet p WHERE p.status NOT IN :statuses AND p.valid")
    List<Packet> findAllDiggiePackets(@Param("statuses") List<String> statuses);

    @Transactional
    @Query(value=" SELECT * FROM packet p WHERE p.barcode = :barCode OR p.id = :barCode", nativeQuery = true)
    Optional<Packet> findByBarCode(@Param("barCode") String barCode);

    @Query(value="SELECT COUNT(p.id) FROM packet p " +
            "WHERE p.customer_phone_nb LIKE %:phoneNumber% " +
            "AND ((p.barcode != '' AND p.exchange_Id IS NULL) " +
            "OR p.status <> 'Pas Serieux')", nativeQuery = true)
    int findAllPacketsByPhone_number(@Param("phoneNumber") String phoneNumber);

    @Query(value="SELECT NEW com.clothing.management.models.DashboardCard(" +
            " p.status, " +
            "COUNT(p.status), " +
            "SUM(CASE WHEN DATE(p.date) >= DATE(:beginDate) " +
            "AND DATE(p.date) <= DATE(:endDate) " +
            "THEN 1 ELSE 0 END)) " +
            "FROM Packet p WHERE (p.status <> 'Problème') GROUP BY p.status")
    List<DashboardCard> createNotification(@Param("beginDate") String beginDate, @Param("endDate") String endDate);//DATEDIFF(CURRENT_DATE() , p.date)>0 AND

    @Query(value ="SELECT p FROM Packet p " +
            "WHERE CAST(p.id as String) LIKE %:searchField% " +
            "OR  p.customerName LIKE %:searchField% " +
            "OR p.customerPhoneNb LIKE %:searchField% " +
            "OR p.barcode LIKE %:searchField% " +
            "OR p.packetDescription LIKE %:searchField%")
    Page<Packet> findAllPacketsByField(@Param("searchField") String searchField, Pageable pageable);

    @Transactional
    @Query(value ="SELECT p FROM Packet p " +
            "WHERE CAST(p.id as String) LIKE %:searchField% " +
            "OR  p.customerName LIKE %:searchField% " +
            "OR p.customerPhoneNb LIKE %:searchField% " +
            "OR p.barcode LIKE %:searchField% " +
            "OR p.packetDescription LIKE %:searchField% " +
            "AND DATE(p.date) >= DATE(:beginDate) " +
            "AND DATE(p.date) <= DATE(:endDate)")
    Page<Packet> findAllPacketsByFieldAndDate(@Param("searchField") String searchField, @Param("beginDate") Date beginDate, @Param("endDate") Date endDate, Pageable pageable);

    @Query(value ="SELECT p FROM Packet p WHERE p.status IN (:selectedList) AND (p.status IN (:ignoredDateStatusList) OR (DATE(p.date) >= DATE(:beginDate) AND DATE(p.date) <= DATE(:endDate)))")
    Page<Packet> findAllPacketsByStatus(@Param("ignoredDateStatusList") List<String> ignoredDateStatusList, @Param("selectedList") List<String> selectedList, @Param("beginDate") Date beginDate, @Param("endDate") Date endDate, Pageable pageable);

    @Query(value ="SELECT p FROM Packet p WHERE p.status = 'Confirmée'")
    List<Packet> findValidationPackets();

    @Transactional
    @Query(value ="SELECT p FROM Packet p WHERE p.status IN (:selectedList)")
    Page<Packet> findAllPacketsByStatus(@Param("selectedList") List<String> selectedList, Pageable pageable);


    @Query(value ="SELECT p FROM Packet p WHERE DATE(p.date) >= DATE(:beginDate) AND DATE(p.date) <= DATE(:endDate)")
    Page<Packet> findAllPacketsByDate(@Param("beginDate") Date beginDate, @Param("endDate") Date endDate, Pageable pageable);

    @Query(value ="SELECT p FROM Packet p WHERE DATE(p.date) >= DATE(:beginDate) AND DATE(p.date) <= DATE(:endDate) AND p.status IN (:statusList)")
    Page<Packet> findAllPacketsByDateAndStatus(@Param("beginDate") Date beginDate, @Param("endDate") Date endDate, @Param("statusList") List<String> statusList, Pageable pageable);

    @Query(value ="SELECT p FROM Packet p WHERE DATE(p.date) >= DATE(:beginDate) AND DATE(p.date) <= DATE(:endDate)")
    List<Packet> findAllPacketsByDate(@Param("beginDate") Date beginDate, @Param("endDate") Date endDate);

    @Query(value = "SELECT NEW com.clothing.management.dto.DayCount.PagesStatCountDTO( " +
            "DATE(p.date), p.fbPage.name, " +
            "SUM(CASE WHEN p.status IN ('Livrée', 'Payée') THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN p.status IN ('En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier') THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN (p.status = 'Retour' OR p.status = 'Retour reçu') AND p.exchangeId IS NULL THEN 1 ELSE 0 END)) " +
            "FROM Packet p " +
            "WHERE DATE(p.date) >= DATE(:beginDate) " +
            "AND DATE(p.date) <= DATE(:endDate) " +
            "AND p.status IN ('Livrée', 'Payée','En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier', 'Retour' ,'Retour reçu') " +
            "GROUP BY p.fbPage.name ORDER BY DATE(p.date) ASC ")
    List<PagesStatCountDTO> findAllPacketsPages(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Query(value = "SELECT NEW com.clothing.management.dto.DayCount.StatesStatCountDTO( " +
            "DATE(p.date), p.city.governorate.name, " +
            "SUM(CASE WHEN p.status IN ('Livrée', 'Payée') THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN p.status IN ('En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier') THEN 1 ELSE 0 END), " +
            "SUM(CASE WHEN (p.status = 'Retour' OR p.status = 'Retour reçu') AND p.exchangeId IS NULL THEN 1 ELSE 0 END)) " +
            "FROM Packet p " +
            "WHERE DATE(p.date) >= DATE(:beginDate) " +
            "AND DATE(p.date) <= DATE(:endDate) " +
            "AND p.status IN ('Livrée', 'Payée','En cours (1)', 'En cours (2)', 'En cours (3)', 'A verifier', 'Retour' ,'Retour reçu') " +
            "GROUP BY p.city.governorate.id ORDER BY DATE(p.date) ASC ")
    List<StatesStatCountDTO> findAllPacketsStates(@Param("beginDate") String beginDate, @Param("endDate") String endDate);


    @Modifying
    @Query(value="DELETE FROM packet WHERE customer_name='' AND customer_phone_nb='';", nativeQuery = true)
    int deleteEmptyPacket();

    @Modifying
    @Query(value="UPDATE packet SET city_id = :cityId WHERE id = :packetId", nativeQuery = true)
    void saveCity(@Param("packetId") Long packetId, @Param("cityId") Long cityId);

    @Modifying
    @Query(value="UPDATE packet SET fbpage_id = :fbPageId WHERE id = :packetId", nativeQuery = true)
    void saveFbPage(@Param("packetId") Long packetId, @Param("fbPageId") Long fbPageId);

    @Modifying
    @Query(value="UPDATE packet SET customer_name = :name WHERE id = :packetId", nativeQuery = true)
    void saveCustomerName(@Param("packetId") Long packetId, @Param("name") String name);

    @Modifying
    @Query(value="UPDATE packet SET address = :address WHERE id = :packetId", nativeQuery = true)
    void saveAddress(@Param("packetId") Long packetId, @Param("address") String address);

    @Modifying
    @Query(value="UPDATE packet SET customer_phone_nb = :customerPhoneNumber , old_client = :oldClient WHERE id = :packetId", nativeQuery = true)
    void savePhoneNumber(@Param("packetId") Long packetId, @Param("customerPhoneNumber") String customerPhoneNumber,@Param("oldClient") int oldClient);

    @Modifying
    @Query(value="UPDATE packet SET date = :date WHERE id = :packetId", nativeQuery = true)
    void saveDate(@Param("packetId") Long packetId, @Param("date") Date date);

    @Modifying
    @Query(value="UPDATE packet SET barcode = :barcode WHERE id = :packetId", nativeQuery = true)
    void saveBarcode(@Param("packetId") Long packetId, @Param("barcode") String barcode);

    Long countPacketByFbPage_Id(Long id);

    Long countPacketByDeliveryCompany_Id(Long id);

}
