package com.ispan.bankmanagement.stock.dto;

import com.ispan.bankmanagement.stock.entity.StockInfoEntity;

public class StockInfoDto {
    private  int stockId;
    private String stockName;

    public static StockInfoDto ConvertEntityToDto(StockInfoEntity stockInfoEntity) {
        StockInfoDto stockInfoDto = new StockInfoDto();
        stockInfoDto.setStockId(stockInfoEntity.getStockId());
        stockInfoDto.setStockName(stockInfoEntity.getStockName());

        return stockInfoDto;
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
