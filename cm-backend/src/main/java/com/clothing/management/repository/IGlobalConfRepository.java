package com.clothing.management.repository;

import com.clothing.management.entities.GlobalConf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IGlobalConfRepository extends JpaRepository<GlobalConf, Long> {
    
}