package com.clothing.management.repository;

import com.clothing.management.dto.PacketDTO;
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

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface IPacketRepository extends JpaRepository<Packet, Long> {
    @Query(value=" SELECT * FROM packet p WHERE DATEDIFF(NOW() , p.date) < 2", nativeQuery = true)
    List<Packet> findAllByDate(@Param("date") Date d);


    @Query(value="SELECT p FROM Packet p WHERE p.status NOT IN :statuses " +
            "AND p.valid")
    public List<Packet> findAllDiggiePackets(@Param("statuses") List<String> statuses);

    @Transactional
    @Query(value=" SELECT * FROM packet p WHERE p.barcode = :barCode OR p.id = :barCode", nativeQuery = true)
    Optional<Packet> findByBarCode(@Param("barCode") String barCode);

    @Query(value="SELECT COUNT(p.id) FROM packet p WHERE p.customer_phone_nb LIKE %:phoneNumber% " +
            "AND ((p.barcode != '' AND p.exchange_Id IS NULL) OR p.status <> 'Pas Serieux')", nativeQuery = true)
    public int findAllPacketsByPhone_number(@Param("phoneNumber") String phoneNumber);

    @Query(value="SELECT NEW com.clothing.management.models.DashboardCard(" +
            " p.status, " +
            "COUNT(p.status), " +
            "SUM(CASE WHEN DATE(p.date) >= DATE(:startDate) AND DATE(p.date) <= DATE(:endDate) THEN 1 ELSE 0 END)) " +
            "FROM Packet p WHERE (p.status <> 'Problème') GROUP BY p.status")
    List<DashboardCard> createNotification(@Param("startDate") String startDate, @Param("endDate") String endDate);//DATEDIFF(CURRENT_DATE() , p.date)>0 AND

    @Query(value ="SELECT p FROM Packet p WHERE CAST(p.id as String) LIKE %:searchField% OR  p.customerName LIKE %:searchField% OR p.customerPhoneNb LIKE %:searchField% OR p.barcode LIKE %:searchField%")
    Page<Packet> findAllPacketsByField(@Param("searchField") String searchField, Pageable pageable);
    @Transactional
    @Query(value ="SELECT p FROM Packet p WHERE CAST(p.id as String) LIKE %:searchField% OR  p.customerName LIKE %:searchField% OR p.customerPhoneNb LIKE %:searchField% OR p.barcode LIKE %:searchField% AND DATE(p.date) >= DATE(:startDate) AND DATE(p.date) <= DATE(:endDate)")
    Page<Packet> findAllPacketsByFieldAndDate(@Param("searchField") String searchField, @Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable pageable);

    @Query(value ="SELECT p FROM Packet p WHERE p.status = 'Confirmée' OR (p.valid = false AND p.barcode <> '' AND p.status <> 'Annuler')")
    Page<Packet> findAllNotValidatedPackets(Pageable pageable);

    @Query(value ="SELECT p FROM Packet p WHERE p.status IN (:selectedList) AND (p.status IN (:ignoredDateStatusList) OR (DATE(p.date) >= DATE(:startDate) AND DATE(p.date) <= DATE(:endDate)))")
    Page<Packet> findAllPacketsByStatus(@Param("ignoredDateStatusList") List<String> ignoredDateStatusList, @Param("selectedList") List<String> selectedList, @Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable pageable);

    @Transactional
    @Query(value ="SELECT p FROM Packet p WHERE p.status IN (:selectedList)")
    Page<Packet> findAllPacketsByStatus(@Param("selectedList") List<String> selectedList, Pageable pageable);


    @Query(value ="SELECT p FROM Packet p WHERE DATE(p.date) >= DATE(:startDate) AND DATE(p.date) <= DATE(:endDate)")
    Page<Packet> findAllPacketsByDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable pageable);

    @Query(value ="SELECT p FROM Packet p WHERE DATE(p.date) >= DATE(:startDate) AND DATE(p.date) <= DATE(:endDate) AND p.status IN (:statusList)")
    Page<Packet> findAllPacketsByDateAndStatus(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("statusList") List<String> statusList, Pageable pageable);

    @Query(value ="SELECT p FROM Packet p WHERE DATE(p.date) >= DATE(:startDate) AND DATE(p.date) <= DATE(:endDate)")
    List<Packet> findAllPacketsByDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Modifying
    @Query(value="DELETE FROM packet WHERE customer_name='' AND customer_phone_nb='';", nativeQuery = true)
    public int deleteEmptyPacket();

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

}
