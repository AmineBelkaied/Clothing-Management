package com.clothing.management.repository;

import com.clothing.management.entities.City;
import com.clothing.management.entities.Governorate;
import com.clothing.management.entities.Packet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface ICityRepository extends JpaRepository<City, Long >  {
    @Query(value ="SELECT g.id AS governorate_id," +
            "g.name AS governorate_name," +
            "c.id AS city_id," +
            "c.name AS city_name," +
            "c.postalCode FROM City c JOIN Governorate g ON c.governorate.id = g.id " +
            "ORDER BY g.id")
    List< Object > findAllgroupedCities();

}
