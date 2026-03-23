package com.ispan.bankmanagement.stock.dto;

public class StockInfoDto {
    private int stockId;
    private String stockName;

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
