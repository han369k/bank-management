package com.ispan.bankmanagement.stock.entity;


public class StockInfoEntity {
    private int stockId;
    private String stockName;
    //V2新增 表示股票上下市狀態
    public Boolean status;

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

    public Boolean getStatus() {
        return status;
    }
    public void setStatus(Boolean status) {
        this.status = status;
    }
}