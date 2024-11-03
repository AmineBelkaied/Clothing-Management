package com.clothing.management.repository;

import com.clothing.management.entities.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IModelRepository extends JpaRepository<Model, Long> {
    Optional<Model> findByNameIsIgnoreCase(String name);

    @Modifying
    @Query("UPDATE Model m SET m.isDeleted = true, m.isEnabled = false WHERE m.id = :id")
    void softDeleteModel(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Model m SET m.isDeleted = false WHERE m.id = :id")
    void rollBackModel(@Param("id") Long id);

}
