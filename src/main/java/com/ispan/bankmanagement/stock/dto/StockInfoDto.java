package com.ispan.bankmanagement.stock.dto;

public class StockInfoDto {
    private int stockId;
    private String stockName;
    //V2新增
    private String status;

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

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}