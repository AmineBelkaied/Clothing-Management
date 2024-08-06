package com.clothing.management.repository;
import com.clothing.management.entities.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IOfferRepository extends JpaRepository<Offer, Long> {
    Offer findByName(String name);

    @Modifying
    @Query("UPDATE Offer o SET o.deleted = true WHERE o.id IN :ids")
    public void setDeletedByIds(@Param("ids") List<Long> ids);

    @Query(value = "SELECT * FROM offer o WHERE o.enabled = true", nativeQuery = true)
    List<Offer> findAllEnabledOffers();

}
