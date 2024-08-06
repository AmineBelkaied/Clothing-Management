package com.clothing.management.repository;

import com.clothing.management.entities.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IModelRepository extends JpaRepository<Model, Long> {

}
