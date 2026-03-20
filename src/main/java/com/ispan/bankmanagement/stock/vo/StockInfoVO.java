package com.ispan.bankmanagement.stock.vo;

import com.ispan.bankmanagement.stock.entity.StockInfoEntity;

public class StockInfoVO {
    private int stockId;
    private String stockName;

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
}
