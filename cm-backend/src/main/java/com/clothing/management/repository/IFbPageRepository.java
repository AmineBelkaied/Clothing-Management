package com.clothing.management.repository;

import com.clothing.management.entities.Color;
import com.clothing.management.entities.FbPage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IFbPageRepository extends JpaRepository<FbPage, Long > {

    Optional<FbPage> findByNameIsIgnoreCase(String name);
}
