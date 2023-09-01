package com.clothing.management.repository.repositoryImpl;

import com.clothing.management.entities.Packet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.text.ParseException;
import java.util.List;

@Component
public class PacketRepositoryOldImpl {

    private final static String SELECT_PACKET_QUERY = "SELECT p FROM com.clothing.management.entities.Packet p WHERE ";
    private final static String SEARCH_BY_TEXT_QUERY = "(p.customerName LIKE :searchText OR p.customerPhoneNb LIKE :searchText OR p.address LIKE :searchText OR p.city.name LIKE :searchText OR p.fbPage.name LIKE :searchText)";
    private final static String SEARCH_BY_DATE_RANGE = "DATE(p.date) >= DATE(:startDate) AND DATE(p.date) <= DATE(:endDate)";
    private final static String SEARCH_BY_STATUS = "p.status = :status";
    @PersistenceContext
    private EntityManager entityManager;

    public Page<Packet> findAllPackets(int page, int size, String searchText, String startDate, String endDate, String status) throws ParseException {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(SELECT_PACKET_QUERY);
        TypedQuery<Packet> query = null;
        if (searchText != null && !status.isEmpty()) {
            query = createSearchByTextQuery(searchText, queryBuilder);
        }
        if (startDate != null && endDate != null) {
            query = createSearchByDateRangeQuery(searchText, startDate, endDate, queryBuilder);
        }
        if (status != null && !status.isEmpty()) {
            query = createSearchByStatusQuery(searchText, startDate, endDate, status, queryBuilder);
        }
        int firstResult = page * size;
        query.setFirstResult(firstResult);
        query.setMaxResults(size);
        System.out.println("query : "  + queryBuilder.toString());

        List<Packet> records = query.getResultList();


        return new PageImpl<>(records, PageRequest.of(page, size), records.size());
    }


    private TypedQuery<Packet> createSearchByTextQuery(String searchText, StringBuilder queryBuilder) {
        TypedQuery<Packet> query = entityManager.createQuery(queryBuilder.append(SEARCH_BY_TEXT_QUERY).toString(), Packet.class);
        query.setParameter("searchText", "%" + searchText + "%");
        return query;
    }

    private TypedQuery<Packet> createSearchByDateRangeQuery(String searchText, String startDate, String endDate, StringBuilder queryBuilder) {
        TypedQuery<Packet> query = entityManager.createQuery(queryBuilder.append(searchText != null ? " AND " + SEARCH_BY_DATE_RANGE : SEARCH_BY_DATE_RANGE).toString(), Packet.class);
        if (searchText != null)
            query.setParameter("searchText", "%" + searchText + "%");
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query;
    }

    private TypedQuery<Packet> createSearchByStatusQuery(String searchText, String startDate, String endDate, String status, StringBuilder queryBuilder) {
        TypedQuery<Packet> query = entityManager.createQuery(queryBuilder.append(searchText != null || (startDate != null && endDate != null) ? " AND " + SEARCH_BY_STATUS : SEARCH_BY_STATUS).toString(), Packet.class);
        if (searchText != null)
            query.setParameter("searchText", "%" + searchText + "%");
        if (startDate != null && endDate != null) {
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
        }
        query.setParameter("status", status);
        return query;
    }

}
