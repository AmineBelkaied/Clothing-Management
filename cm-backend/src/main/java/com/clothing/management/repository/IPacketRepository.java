package com.clothing.management.repository;

import com.clothing.management.entities.Packet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IPacketRepository extends JpaRepository<Packet, Long> {

    @Query(value=" SELECT * FROM packet p WHERE DATEDIFF(NOW() , p.date) < 2", nativeQuery = true)
    public List<Packet> findAllByDate(@Param("date") Date d);

    @Query(value=" SELECT * FROM packet p WHERE DATE(p.date) = DATE(NOW()) ORDER BY p.id DESC", nativeQuery = true)
    public List<Packet> findAllTodayPackets();

}
