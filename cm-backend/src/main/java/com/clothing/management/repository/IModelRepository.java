package com.clothing.management.repository;

import com.clothing.management.entities.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IModelRepository extends JpaRepository<Model, Long> {

    @Query("SELECT m FROM Model m where m.reference = :modelRef")
    Model findByReference(@Param("modelRef") String modelRef);
}
