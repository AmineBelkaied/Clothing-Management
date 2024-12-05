package com.clothing.management.repository;
import com.clothing.management.dto.StatDTO.ChartDTO.ChartDTO;
import com.clothing.management.entities.ModelStockHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IModelStockHistoryRepository extends JpaRepository<ModelStockHistory, Long >  {

    @Query(value = "SELECT NEW com.clothing.management.dto.StatDTO.ChartDTO.ChartDTO("+
            "DATE(m.date),m.model.id,m.model.name,m.quantity) " +
            "FROM ModelStockHistory m " +
            "WHERE DATE(m.date) >= DATE(:beginDate) " +
            "AND DATE(m.date) <= DATE(:endDate) group by DATE(m.date), m.model")
    List<ChartDTO> statStockByDate(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Query("SELECT NEW com.clothing.management.entities.ModelStockHistory(" +
            "m.model, m.quantity) " +
            "FROM ModelStockHistory m " +
            "WHERE DATE(m.date) = DATE(:date)")
    List<ModelStockHistory> statValues(@Param("date") Date date);
}
