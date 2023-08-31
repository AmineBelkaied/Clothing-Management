package com.clothing.management.repository;

import com.clothing.management.entities.Packet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface IPacketRepository extends JpaRepository<Packet, Long> {
    @Query(value=" SELECT * FROM packet p WHERE DATEDIFF(NOW() , p.date) < 2", nativeQuery = true)
    List<Packet> findAllByDate(@Param("date") Date d);

    @Query(value=" SELECT * FROM packet p WHERE DATE(p.date) = DATE(NOW()) ORDER BY p.id DESC", nativeQuery = true)
    Page<Packet> findAllTodayPackets(Pageable pageable);

    @Query(value="SELECT * FROM packet p WHERE p.id > 7317 and p.status != 'Retour Echange' and p.status != 'Payée' and p.status != 'Retour reçu' and p.status != 'Supprimé' p.status != 'Retour Expediteur' AND p.barcode != '' AND p.barcode NOT LIKE 'b%' ORDER BY p.id DESC;", nativeQuery = true)
    List<Packet> findAllDiggiePackets();

    @Query(value=" SELECT * FROM packet p WHERE p.barcode = :barCode", nativeQuery = true)
    Optional<Packet> findByBarCode(@Param("barCode") String barCode);

   //@Query(value = getQuery(searchText, endDate, se), countQuery = COUNT_FIELD_QUERY, nativeQuery = true)
    //defaut Page<Packet> findAllPackets(@Param("searchText") String searchText, @Param("startDate") String startDate, @Param("endDate") String endDate, Pageable pageable);
}
