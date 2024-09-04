package com.clothing.management.repository;

import com.clothing.management.entities.Color;
import com.clothing.management.entities.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ISizeRepository extends JpaRepository<Size, Long> {
    Optional<Size> findByReferenceIsIgnoreCase(String sizeRef);
}
