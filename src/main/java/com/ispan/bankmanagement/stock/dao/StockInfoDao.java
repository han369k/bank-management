package com.ispan.bankmanagement.stock.dao;

import com.ispan.bankmanagement.stock.entity.StockInfoEntity;
import java.util.List;

public class StockInfoDao extends BaseDao {

    //lambda語法
    private final RowMapper<StockInfoEntity> rowMapper = rs -> {
        StockInfoEntity entity = new StockInfoEntity();
        entity.setStockName(rs.getString("stock_name"));
        entity.setStockId(rs.getInt("stock_id"));
        return entity;
    };

    public List<StockInfoEntity> GetAll() {//查詢全部
        String getAll = "select stock_id, stock_name from stock_info";
        return Query(getAll, rowMapper);
    }

    public StockInfoEntity GetById(int id) {//查詢單筆
        String getById = "select stock_id, stock_name from stock_info where stock_id = ?";
        // 把查到的 List 丟進流裡，抓第一個，抓不到就回傳 null
        return Query(getById, rowMapper, id).stream().findFirst().orElse(null);
    }

    public void InsertStockInfo(StockInfoEntity stockInfo) {//新增單筆
        String insert = "insert into stock_info (stock_id, stock_name) values (?, ?)";
        Update(insert, stockInfo.getStockId(), stockInfo.getStockName());
    }

    public void DeleteById(int id) {//依據id刪除
        String delete = "delete from stock_info where stock_id = ?";
        Update(delete, id);
    }

    public void UpdateStockInfo(StockInfoEntity stockInfo) {//依據id更新資料
        String update = "update stock_info set stock_name = ? where stock_id = ?";
        Update(update, stockInfo.getStockName(), stockInfo.getStockId());
    }
}
