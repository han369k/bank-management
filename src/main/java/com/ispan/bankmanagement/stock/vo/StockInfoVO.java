package com.ispan.bankmanagement.stock.vo;

import com.ispan.bankmanagement.stock.entity.StockInfoEntity;

public class StockInfoVO {
    private int stockId;
    private String stockName;
    private String status;//V2新增: 上市or下市

    public static StockInfoVO ConvertEntityToVO(StockInfoEntity stockInfoEntity) {
        StockInfoVO stockInfoVO = new StockInfoVO();
        stockInfoVO.setStockId(stockInfoEntity.getStockId());
        stockInfoVO.setStockName(stockInfoEntity.getStockName());

        return stockInfoVO;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}