package com.clothing.management.services;

import com.clothing.management.dto.ModelStockHistory;

import java.util.List;

public interface ModelStockHistoryService {
    void saveDayHistory(List<ModelStockHistory> msh);
}
