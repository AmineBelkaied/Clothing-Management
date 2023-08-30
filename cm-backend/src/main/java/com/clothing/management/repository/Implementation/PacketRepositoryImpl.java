package com.clothing.management.repository.Implementation;

import com.clothing.management.entities.Packet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Component
public class PacketRepositoryImpl {

    private final static String SELECT_PACKET_QUERY = "SELECT * FROM packet p INNER JOIN city ON p.city_id = city.id INNER JOIN fb_page ON p.fbpage_id = fb_page.id WHERE ";
    private final static String SEARCH_BY_TEXT_QUERY = "p.customer_name LIKE :searchText OR p.customer_phone_nb LIKE :searchText OR p.address LIKE :searchText OR city.name LIKE :searchText OR fb_page.name LIKE :searchText";
    private final static String SEARCH_BY_START_DATE = "DATE(p.date) = ?2";
    private final static String SEARCH_BY_START_DATE_AND_END_DATE = "DATE(p.date) >= ?2 AND DATE(p.date) <= ?3";
    private final static String COUNT_FIELD_QUERY = "SELECT count(*) FROM packet p INNER JOIN city ON p.city_id = city.id INNER JOIN fb_page ON p.fbpage_id = fb_page.id" +
            " where p.customer_name LIKE %:searchText% OR p.customer_phone_nb LIKE %:searchText% OR p.address LIKE %:searchText% " +
            " OR city.name LIKE %:searchText% OR fb_page.name LIKE %:searchText% ";
    @PersistenceContext
    private EntityManager entityManager;

     public List<Packet> findAllPackets(String searchText, String startDate, String endDate) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(SELECT_PACKET_QUERY);
         Query query = null;
        if(searchText != null){
            queryBuilder.append(SEARCH_BY_TEXT_QUERY);
            System.out.println(queryBuilder.toString());
            query = entityManager.createNativeQuery(queryBuilder.toString());
            query.setParameter("searchText", "%" + searchText + "%");
        }

        if(startDate != null && endDate == null) {
            queryBuilder.append(SEARCH_BY_START_DATE);
            query = entityManager.createNativeQuery(queryBuilder.toString());
            query.setParameter(2, startDate);
        }
        if(startDate != null && endDate != null) {
            queryBuilder.append(SEARCH_BY_START_DATE_AND_END_DATE);
            query = entityManager.createNativeQuery(queryBuilder.toString());
            query.setParameter(3, startDate);
        }
        return query.getResultList();
    }
}
