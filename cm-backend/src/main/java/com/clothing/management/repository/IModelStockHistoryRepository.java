package com.clothing.management.repository;

import com.clothing.management.entities.ModelStockHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IModelStockHistoryRepository extends JpaRepository<ModelStockHistory, Long >  {
}
