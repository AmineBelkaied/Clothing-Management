package com.clothing.management.repository;

import com.clothing.management.dto.ProductsDayCountDTO;
import com.clothing.management.entities.Packet;
import jakarta.transaction.Transactional;
import com.clothing.management.models.DashboardCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;


import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface IPacketRepository extends JpaRepository<Packet, Long> {
    @Query(value=" SELECT * FROM packet p WHERE DATEDIFF(NOW() , p.date) < 2", nativeQuery = true)
    List<Packet> findAllByDate(@Param("date") Date d);

    /*@Query(value="SELECT * FROM packet p WHERE DATE(p.date) = DATE(NOW()) ORDER BY p.id DESC",
            countQuery = "SELECT count(*) FROM packet p WHERE DATE(p.date) = DATE(NOW()) ORDER BY p.id DESC",
            nativeQuery = true)
    Page<Packet> findAllTodayPackets(Pageable pageable);*/

    @Query(value="SELECT * FROM packet p WHERE p.status != 'Payée' and p.status != 'Annuler' and p.status != 'Pas serieux' and p.status != 'Livrée' AND p.status != 'Retour reçu' and p.status != 'Retour' and p.status != 'Supprimé' AND p.status != 'En rupture' AND p.status != 'Problème' AND p.barcode != '' AND p.barcode AND p.valid NOT LIKE 'b%' ORDER BY p.id DESC;", nativeQuery = true)
    public List<Packet> findAllDiggiePackets();

    @Query(value="UPDATE packet p SET p.attempt = p.attempt + 1 WHERE  id= :packetId", nativeQuery = true)
    Optional<Packet> addAttempt(@Param("packetId") Long packetId);

    @Query(value=" SELECT * FROM packet p WHERE p.barcode = :barCode", nativeQuery = true)
    Optional<Packet> findByBarCode(@Param("barCode") String barCode);

    @Query(value="SELECT COUNT(p.id) FROM packet p WHERE p.customer_phone_nb LIKE %:phoneNumber% AND p.status != 'Supprimé' AND p.status != 'Non confirmée' AND p.exchange = false AND p.status != 'Annuler' AND p.status != 'Retour reçu' and p.status != 'Retour'", nativeQuery = true)
    public int findAllPacketsByPhone_number(@Param("phoneNumber") String phoneNumber);

    @Modifying
    @Query(value="DELETE FROM packet WHERE customer_name='' AND customer_phone_nb='';", nativeQuery = true)
    public int deleteEmptyPacket();

    @Query(value="SELECT NEW com.clothing.management.models.DashboardCard( p.status, COUNT(p.status)) FROM Packet p GROUP BY p.status")
    List<DashboardCard> createDashboard();

    @Query(value="SELECT NEW com.clothing.management.models.DashboardCard( p.status, COUNT(p.status)) FROM Packet p WHERE (p.status = 'Non Confirmée' OR p.status = 'Injoignable' OR p.status = 'A verifier') GROUP BY p.status")
    List<DashboardCard> createNotification();//DATEDIFF(CURRENT_DATE() , p.date)>0 AND

   //@Query(value = getQuery(searchText, endDate, se), countQuery = COUNT_FIELD_QUERY, nativeQuery = true)
    //defaut Page<Packet> findAllPackets(@Param("searchText") String searchText, @Param("startDate") String startDate, @Param("endDate") String endDate, Pageable pageable);
    @Query(value ="SELECT p FROM Packet p WHERE p.customerName LIKE %:searchText% OR p.customerPhoneNb LIKE %:searchText% OR p.address LIKE %:searchText% OR p.city.name LIKE %:searchText% OR p.fbPage.name LIKE %:searchText%")
    Page<Packet> findAllPacketsByField(@Param("searchText") String searchText, Pageable pageable);

    @Query(value ="SELECT p FROM Packet p WHERE (p.customerName LIKE %:searchField% OR p.customerPhoneNb LIKE %:searchField% OR p.address LIKE %:searchField% OR p.city.name LIKE %:searchField% OR p.fbPage.name LIKE %:searchField%) AND p.date BETWEEN :startDate AND :endDate")
    Page<Packet> findAllPacketsByFieldAndDate(@Param("searchField") String searchField, @Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable pageable);

    @Query(value ="SELECT p FROM Packet p WHERE (p.customerName LIKE %:searchField% OR p.customerPhoneNb LIKE %:searchField% OR p.address LIKE %:searchField% OR p.city.name LIKE %:searchField% OR p.fbPage.name LIKE %:searchField%) AND p.status IN (:statusList)")
    Page<Packet> findAllPacketsByFieldAndStatus(@Param("searchField") String searchField, @Param("statusList") List<String> statusList, Pageable pageable);

    @Query(value ="SELECT p FROM Packet p WHERE p.status IN (:statusList)")
    Page<Packet> findAllPacketsByStatus(@Param("statusList") List<String> statusList, Pageable pageable);

    @Query(value ="SELECT p FROM Packet p WHERE DATE(p.date) >= DATE(:startDate) AND DATE(p.date) <= DATE(:endDate)")
    Page<Packet> findAllPacketsByDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable pageable);

    @Query(value ="SELECT p FROM Packet p WHERE DATE(p.date) >= DATE(:startDate) AND DATE(p.date) <= DATE(:endDate) AND p.status IN (:statusList)")
    Page<Packet> findAllPacketsByDateAndStatus(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("statusList") List<String> statusList, Pageable pageable);

    @Query(value ="SELECT p FROM Packet p WHERE (p.customerName LIKE %:searchField% OR p.customerPhoneNb LIKE %:searchField% OR p.address LIKE %:searchField% OR p.city.name LIKE %:searchField% OR p.fbPage.name LIKE %:searchField%) AND DATE(p.date) >= DATE(:startDate) AND DATE(p.date) <= DATE(:endDate) AND p.status IN (:statusList)")
    Page<Packet> findAllPacketsByFieldAndDateAndStatus(@Param("searchField") String searchField, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("statusList") List<String> statusList, Pageable pageable);

    @Query(value ="SELECT p FROM Packet p WHERE DATE(p.date) >= DATE(:startDate) AND DATE(p.date) <= DATE(:endDate)")
    List<Packet> findAllPacketsByDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
