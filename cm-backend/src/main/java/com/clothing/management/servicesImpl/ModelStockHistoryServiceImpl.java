package com.clothing.management.servicesImpl;
import com.clothing.management.entities.ModelStockHistory;
import com.clothing.management.repository.IModelStockHistoryRepository;
import com.clothing.management.repository.IPacketRepository;
import com.clothing.management.services.ModelStockHistoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModelStockHistoryServiceImpl implements ModelStockHistoryService {

    private final IModelStockHistoryRepository modelStockHistoryRepository;

    public ModelStockHistoryServiceImpl(IModelStockHistoryRepository modelStockHistoryRepository) {
        this.modelStockHistoryRepository = modelStockHistoryRepository;
    }


    public void saveDayHistory(List<ModelStockHistory> countStock)
    {
        modelStockHistoryRepository.saveAll(countStock);
    }

}
