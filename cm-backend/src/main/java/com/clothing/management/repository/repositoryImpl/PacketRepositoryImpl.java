package com.clothing.management.repository.repositoryImpl;

import com.clothing.management.entities.Packet;
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
import java.util.List;

@Component
public class PacketRepositoryImpl {

    @PersistenceContext
    private EntityManager entityManager;

    public Page<Packet> findAllPackets(String searchText, String startDate, String endDate, String status, Pageable pageable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Packet> criteriaQuery = criteriaBuilder.createQuery(Packet.class);
        Root<Packet> root = criteriaQuery.from(Packet.class);

        List<Predicate> predicates = new ArrayList<>();

        if(searchText != null) {
            predicates.add(criteriaBuilder.or(
                    criteriaBuilder.like(root.get("customerName"), "%" + searchText + "%"),
                    criteriaBuilder.like(root.get("customerPhoneNb"), "%" + searchText + "%"),
                    criteriaBuilder.like(root.get("address"), "%" + searchText + "%"),
                    criteriaBuilder.like(root.get("city").get("name"), "%" + searchText + "%"),
                    criteriaBuilder.like(root.get("fbPage").get("name"), "%" + searchText + "%")
            ));
        }

        if(startDate != null && endDate != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("date").as(LocalDate.class), LocalDate.parse(startDate)));
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("date").as(LocalDate.class), LocalDate.parse(endDate)));
        }

        if(status != null)
            predicates.add(criteriaBuilder.equal(root.get("status"), status));

        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        TypedQuery<Packet> query = entityManager.createQuery(criteriaQuery);

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<Packet> resultList = query.getResultList();

        return new PageImpl<>(resultList, pageable, resultList.size());
    }
}
