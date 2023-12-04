package com.clothing.management.services;

import com.clothing.management.entities.ModelStockHistory;

import java.util.List;

public interface ModelStockHistoryService {
    public void saveDayHistory(List<ModelStockHistory> msh);
}
