package com.clothing.management.repository;
import com.clothing.management.dto.ModelStockHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IModelStockHistoryRepository extends JpaRepository<ModelStockHistory, Long >  {
    @Query(value = "SELECT NEW com.clothing.management.dto.ModelStockHistory("+
            "m.id,DATE(m.date),m.modelId,m.modelName,m.quantity) " +
            "FROM ModelStockHistory m " +
            "WHERE DATE(m.date) >= DATE(:beginDate) " +
            "AND DATE(m.date) <= DATE(:endDate)")
    List<ModelStockHistory> statStockByDate(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

}
