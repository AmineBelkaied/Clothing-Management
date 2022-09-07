package com.clothing.management.repository;


import com.clothing.management.entities.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IColorRepository extends JpaRepository<Color, Long> {

    @Query("SELECT c FROM Color c where c.reference = :colorRef")
    Color findByReference(@Param("colorRef") String colorRef);
}
