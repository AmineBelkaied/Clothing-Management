package com.clothing.management.repository;

import com.clothing.management.dto.ProductsDayCountDTO;
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

    @Query(value="SELECT * FROM packet p WHERE DATE(p.date) = DATE(NOW()) ORDER BY p.id DESC",
            countQuery = "SELECT count(*) FROM packet p WHERE DATE(p.date) = DATE(NOW()) ORDER BY p.id DESC",
            nativeQuery = true)
    Page<Packet> findAllTodayPackets(Pageable pageable);

    @Query(value="SELECT * FROM packet p WHERE p.status != 'Payée' and p.status != 'Annuler' and p.status != 'Pas serieux' and p.status != 'Livrée' AND p.status != 'Retour reçu' and p.status != 'Retour' and p.status != 'Supprimé' AND p.status != 'En rupture' AND p.status != 'Problème' AND p.barcode != '' AND p.barcode NOT LIKE 'b%' ORDER BY p.id DESC;", nativeQuery = true)
    public List<Packet> findAllDiggiePackets();

    @Query(value=" SELECT * FROM packet p WHERE p.barcode = :barCode", nativeQuery = true)
    Optional<Packet> findByBarCode(@Param("barCode") String barCode);

    @Query(value="SELECT COUNT(p.id) FROM packet p WHERE p.customer_phone_nb LIKE %:phoneNumber% AND p.status != 'Supprimé' AND p.exchange = false AND p.status != 'Annuler'", nativeQuery = true)
    public int findAllPacketsByPhone_number(@Param("phoneNumber") String phoneNumber);

    @Modifying
    @Query(value="DELETE FROM packet WHERE customer_name='' AND customer_phone_nb='' AND fbpage_id IS NULL;", nativeQuery = true)
    public int deleteEmptyPacket();

    @Query(value="SELECT NEW com.clothing.management.models.DashboardCard( p.status, COUNT(p.status)) FROM Packet p GROUP BY p.status")
    List<DashboardCard> createDashboard();

    @Query(value="SELECT NEW com.clothing.management.models.DashboardCard( p.status, COUNT(p.status)) FROM Packet p WHERE DATEDIFF(CURRENT_DATE() , p.date)>0 AND (p.status = 'Non Confirmée' OR p.status = 'Injoiyable' OR p.status = 'A verifier') GROUP BY p.status")
    List<DashboardCard> createNotification();

   //@Query(value = getQuery(searchText, endDate, se), countQuery = COUNT_FIELD_QUERY, nativeQuery = true)
    //defaut Page<Packet> findAllPackets(@Param("searchText") String searchText, @Param("startDate") String startDate, @Param("endDate") String endDate, Pageable pageable);
}
