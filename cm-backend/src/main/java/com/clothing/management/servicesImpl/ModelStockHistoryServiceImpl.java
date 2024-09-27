package com.clothing.management.servicesImpl;

import com.clothing.management.entities.ModelStockHistory;
import com.clothing.management.repository.IModelStockHistoryRepository;
import com.clothing.management.services.ModelStockHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModelStockHistoryServiceImpl implements ModelStockHistoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelStockHistoryServiceImpl.class);

    private final IModelStockHistoryRepository modelStockHistoryRepository;

    public ModelStockHistoryServiceImpl(IModelStockHistoryRepository modelStockHistoryRepository) {
        this.modelStockHistoryRepository = modelStockHistoryRepository;
    }

    @Override
    public void saveDayHistory(List<ModelStockHistory> countStock) {
        try {
            modelStockHistoryRepository.saveAll(countStock);
            LOGGER.info("Successfully saved {} stock history records.", countStock.size());
        } catch (Exception e) {
            LOGGER.error("Error saving stock history data: {}", e.getMessage());
        }
    }
}
