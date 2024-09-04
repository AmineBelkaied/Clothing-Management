package com.clothing.management.repository;

import com.clothing.management.entities.Model;
import com.clothing.management.entities.Offer;
import com.clothing.management.entities.OfferModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public interface IOfferModelRepository extends JpaRepository<OfferModel , Long> {
    @Transactional
    @Modifying
    @Query(value = "INSERT into offer_model (offer_id, model_id, quantity) values (:offerId, :modelId, :quantity)" , nativeQuery = true)
     void addOfferModel(@Param("offerId") Long offerId, @Param("modelId") Long modelId, @Param("quantity") float quantity);

    @Query("SELECT offerModel FROM OfferModel offerModel where offerModel.offer.id= :offerId")
    Set<OfferModel> findByOfferId(@Param("offerId") Long offerId);

    @Query("SELECT om FROM OfferModel om JOIN om.offer o JOIN o.fbPages fb WHERE fb.id = :fbPageId")
    List<OfferModel> findOffersByFbPageId(@Param("fbPageId") Long fbPageId);

    @Transactional
    @Modifying
    @Query("DELETE FROM OfferModel om WHERE om.offer.id = :offerId")
    void deleteByOfferId(@Param("offerId") Long offerId);
}
