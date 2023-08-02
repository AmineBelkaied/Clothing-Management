package com.clothing.management.repository;

import com.clothing.management.entities.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IOfferRepository extends JpaRepository<Offer, Long> {
    Offer findByName(String name);
}
