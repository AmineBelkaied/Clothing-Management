package com.clothing.management.repository.repositoryImpl;

import com.clothing.management.entities.Packet;
import com.clothing.management.enums.DiggieStatus;
import com.clothing.management.repository.IPacketRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class PacketRepositoryImpl {

    @PersistenceContext
    private EntityManager entityManager;

    public Page<Packet> findAllPackets(String searchText, String startDate, String endDate, String status, Pageable pageable) {CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Packet> criteriaQuery = criteriaBuilder.createQuery(Packet.class);
        Root<Packet> root = criteriaQuery.from(Packet.class);

        List<Predicate> predicates = new ArrayList<>();

        if(searchText != null) {
            predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(root.get("id").as(String.class), "%" + searchText + "%"),
                    criteriaBuilder.like(root.get("customerName"), "%" + searchText + "%"),
                    criteriaBuilder.like(root.get("customerPhoneNb"), "%" + searchText + "%"),
                    criteriaBuilder.like(root.get("packetDescription"), "%" + searchText + "%"),
                    criteriaBuilder.like(root.get("barcode"), "%" + searchText + "%")
            ));
        }

        if(startDate != null && endDate != null && searchText== null)
            if( (status!= null &&
                    (
                            !status.equals(DiggieStatus.RETOUR.getStatus())
                                    && !status.equals(DiggieStatus.A_VERIFIER.getStatus())
                                    && !status.equals(DiggieStatus.INJOIYABLE.getStatus())
                                    && !status.equals(DiggieStatus.DELETED.getStatus())
                                    && !status.equals(DiggieStatus.ENDED.getStatus())
                                    && !status.equals(DiggieStatus.EN_COURS_1.getStatus())
                                    && !status.equals(DiggieStatus.EN_COURS_2.getStatus())
                                    && !status.equals(DiggieStatus.EN_COURS_3.getStatus())
                                    && !status.equals(DiggieStatus.NON_CONFIRMEE.getStatus())
                    )
                )
                    || status== null){

                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("date").as(LocalDate.class), LocalDate.parse(startDate)));
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("date").as(LocalDate.class), LocalDate.parse(endDate)));

            }


        if(status == null)
            predicates.add(criteriaBuilder.notLike(root.get("status"),  DiggieStatus.DELETED.getStatus()));

        else if(status != null && searchText == null) {
            predicates.add(root.get("status").in(Arrays.asList(status.split(","))));
        }

        criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id")));

        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
        TypedQuery<Packet> query = entityManager.createQuery(criteriaQuery);
        Long totalItems = getResultSizeBeforePagination(criteriaQuery);

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<Packet> resultList = query.getResultList();
        //System.out.println("query:"+query);
        return new PageImpl<>(resultList, pageable, totalItems);
    }

    public List<Packet> findAllPacketsByDate( String startDate, String endDate) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Packet> criteriaQuery = criteriaBuilder.createQuery(Packet.class);
        Root<Packet> root = criteriaQuery.from(Packet.class);

        List<Predicate> predicates = new ArrayList<>();

        if(startDate != null && endDate != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("date").as(LocalDate.class), LocalDate.parse(startDate)));
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("date").as(LocalDate.class), LocalDate.parse(endDate)));
        }

        criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id")));

        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id")));

        TypedQuery<Packet> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }


    public long getResultSizeBeforePagination(CriteriaQuery<Packet> criteriaQuery) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        // Create a subquery to count rows matching the criteria
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Packet> root = countQuery.from(Packet.class);
        countQuery.select(criteriaBuilder.count(root));
        countQuery.where(criteriaQuery.getRestriction()); // Copy the same criteria

        TypedQuery<Long> typedCountQuery = entityManager.createQuery(countQuery);
        return typedCountQuery.getSingleResult();
    }
}
