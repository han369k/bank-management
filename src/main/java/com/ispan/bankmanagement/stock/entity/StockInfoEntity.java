package com.ispan.bankmanagement.stock.entity;

import com.ispan.bankmanagement.stock.dto.StockInfoDto;

public class StockInfoEntity {
    private int stockId;
    private String stockName;

    public static StockInfoEntity ConvertDtoToEntity(StockInfoDto stockInfoDto) {
        StockInfoEntity stockInfoEntity = new StockInfoEntity();
        stockInfoEntity.setStockId(stockInfoDto.getStockId());
        stockInfoEntity.setStockName(stockInfoDto.getStockName());
        return stockInfoEntity;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public int getStockId() {
        return stockId;
    }
    public String getStockName() {
        return stockName;
    }
}

