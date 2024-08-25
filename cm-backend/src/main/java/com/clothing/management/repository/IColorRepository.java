package com.clothing.management.repository;


import com.clothing.management.entities.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IColorRepository extends JpaRepository<Color, Long> {

    Optional<Color> findByReferenceIsIgnoreCase(String colorRef);

    Optional<Color> findByNameIsIgnoreCase(String name);
}
