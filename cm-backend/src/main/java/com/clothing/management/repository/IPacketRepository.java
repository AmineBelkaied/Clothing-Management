package com.clothing.management.repository;

import com.clothing.management.entities.Packet;
import com.clothing.management.models.DashboardCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;


import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface IPacketRepository extends JpaRepository<Packet, Long> {
    @Query(value=" SELECT * FROM packet p WHERE DATEDIFF(NOW() , p.date) < 2", nativeQuery = true)
    List<Packet> findAllByDate(@Param("date") Date d);

    @Query(value="SELECT * FROM packet p WHERE p.status != 'Payée' and p.status != 'Annuler' " +
            "AND p.status != 'Pas serieux' AND p.status != 'Livrée' " +
            "AND p.status != 'Retour reçu' AND p.status != 'Retour' " +
            "AND p.status != 'Supprimé' AND p.status != 'En rupture' " +
            "AND p.status != 'Problème' AND p.barcode != '' " +
            "AND p.barcode AND p.valid NOT LIKE 'b%' ORDER BY p.id DESC;", nativeQuery = true)
    public List<Packet> findAllDiggiePackets();

    @Query(value="UPDATE packet p SET p.attempt = p.attempt + 1 WHERE  id= :packetId", nativeQuery = true)
    Optional<Packet> addAttempt(@Param("packetId") Long packetId);

    @Query(value=" SELECT * FROM packet p WHERE p.barcode = :barCode", nativeQuery = true)
    Optional<Packet> findByBarCode(@Param("barCode") String barCode);

    @Query(value="SELECT COUNT(p.id) FROM packet p WHERE p.customer_phone_nb LIKE %:phoneNumber% " +
            "AND p.barcode != '' AND p.barcode AND p.exchange_Id IS NULL", nativeQuery = true)
    public int findAllPacketsByPhone_number(@Param("phoneNumber") String phoneNumber);

    @Modifying
    @Query(value="DELETE FROM packet WHERE customer_name='' AND customer_phone_nb='';", nativeQuery = true)
    public int deleteEmptyPacket();

    @Query(value="SELECT NEW com.clothing.management.models.DashboardCard( p.status, COUNT(p.status)) FROM Packet p GROUP BY p.status")
    List<DashboardCard> createDashboard();

    @Query(value="SELECT NEW com.clothing.management.models.DashboardCard( p.status, COUNT(p.status)) FROM Packet p WHERE (p.status <> 'Problème' AND p.status <> 'Annuler' AND p.status <> 'Supprimé') GROUP BY p.status")
    List<DashboardCard> createNotification();//DATEDIFF(CURRENT_DATE() , p.date)>0 AND

   //@Query(value = getQuery(searchField, endDate, se), countQuery = COUNT_FIELD_QUERY, nativeQuery = true)
    //defaut Page<Packet> findAllPackets(@Param("searchField") String searchField, @Param("startDate") String startDate, @Param("endDate") String endDate, Pageable pageable);
    @Query(value ="SELECT p FROM Packet p WHERE CAST(p.id as String) LIKE %:searchField% OR  p.customerName LIKE %:searchField% OR p.customerPhoneNb LIKE %:searchField% OR p.barcode LIKE %:searchField%")
    Page<Packet> findAllPacketsByField(@Param("searchField") String searchField, Pageable pageable);

    @Query(value ="SELECT p FROM Packet p WHERE CAST(p.id as String) LIKE %:searchField% OR  p.customerName LIKE %:searchField% OR p.customerPhoneNb LIKE %:searchField% OR p.barcode LIKE %:searchField% AND DATE(p.date) >= DATE(:startDate) AND DATE(p.date) <= DATE(:endDate)")
    Page<Packet> findAllPacketsByFieldAndDate(@Param("searchField") String searchField, @Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable pageable);

    @Query(value ="SELECT p FROM Packet p WHERE p.status IN (:selectedList) AND (p.status IN (:ignoredDateStatusList) OR (DATE(p.date) >= DATE(:startDate) AND DATE(p.date) <= DATE(:endDate)))")
    Page<Packet> findAllPacketsByStatus(@Param("ignoredDateStatusList") List<String> ignoredDateStatusList, @Param("selectedList") List<String> selectedList, @Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable pageable);

    @Query(value ="SELECT p FROM Packet p WHERE DATE(p.date) >= DATE(:startDate) AND DATE(p.date) <= DATE(:endDate)")
    Page<Packet> findAllPacketsByDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable pageable);

    @Query(value ="SELECT p FROM Packet p WHERE DATE(p.date) >= DATE(:startDate) AND DATE(p.date) <= DATE(:endDate) AND p.status IN (:statusList)")
    Page<Packet> findAllPacketsByDateAndStatus(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("statusList") List<String> statusList, Pageable pageable);

    @Query(value ="SELECT p FROM Packet p WHERE DATE(p.date) >= DATE(:startDate) AND DATE(p.date) <= DATE(:endDate)")
    List<Packet> findAllPacketsByDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

        /*@Query(value="SELECT * FROM packet p WHERE DATE(p.date) = DATE(NOW()) ORDER BY p.id DESC",
            countQuery = "SELECT count(*) FROM packet p WHERE DATE(p.date) = DATE(NOW()) ORDER BY p.id DESC",
            nativeQuery = true)
    Page<Packet> findAllTodayPackets(Pageable pageable);

        @Query(value ="SELECT p FROM Packet p WHERE (p.customerName LIKE %:searchField% OR p.customerPhoneNb LIKE %:searchField% OR p.address LIKE %:searchField% OR p.city.name LIKE %:searchField% OR p.fbPage.name LIKE %:searchField%) AND p.status IN (:statusList)")
    Page<Packet> findAllPacketsByFieldAndStatus(@Param("searchField") String searchField, @Param("statusList") List<String> statusList, Pageable pageable);
        @Query(value ="SELECT p FROM Packet p WHERE (p.customerName LIKE %:searchField% OR p.customerPhoneNb LIKE %:searchField% OR p.address LIKE %:searchField% OR p.city.name LIKE %:searchField% OR p.fbPage.name LIKE %:searchField%) AND DATE(p.date) >= DATE(:startDate) AND DATE(p.date) <= DATE(:endDate) AND p.status IN (:statusList)")
    Page<Packet> findAllPacketsByFieldAndDateAndStatus(@Param("searchField") String searchField, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("statusList") List<String> statusList, Pageable pageable);
    */
}
