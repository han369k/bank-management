package com.ispan.bankmanagement.stock;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class InfoDao extends BaseDao {

    //lambda語法
    private final RowMapper<StockInfoEntity> rowMapper = rs -> {
        StockInfoEntity entity = new StockInfoEntity();
        entity.setStockName(rs.getString("stock_name"));
        entity.setStockId(rs.getInt("stock_id"));
        //V2新增
        entity.setStatus(rs.getBoolean("status"));
        return entity;
    };

    //查詢全部
    public List<StockInfoEntity> GetAll(Connection conn) throws SQLException {
        String getAll = "select stock_id, stock_name, status from stock_info";
        return Query(conn, getAll, rowMapper);
    }

    //查詢單筆
    public StockInfoEntity GetById(Connection conn, int id) throws SQLException {
        String getById = "select stock_id, stock_name, status from stock_info where stock_id = ?";
        // 把查到的 List 丟進流裡，抓第一個，抓不到就回傳 null
        return Query(conn, getById, rowMapper, id).stream().findFirst().orElse(null);
    }

    //新增單筆
    public int InsertInfo(Connection conn, StockInfoEntity stockInfo) throws SQLException {
        String insert = "insert into stock_info (stock_id, stock_name, status) values (?, ?, ?)";
        return Update(conn, insert, stockInfo.getStockId(), stockInfo.getStockName(), stockInfo.getStatus());
    }

    //依據id刪除
    public int DeleteById(Connection conn, int id) throws SQLException {
        String delete = "delete from stock_info where stock_id = ?";
        return Update(conn, delete, id);
    }

    //依據更新資料
    //V2修改: 改為更新股票上下架狀態
    public int UpdateInfo(Connection conn, StockInfoEntity stockInfo) throws SQLException {
        String update = "update stock_info set status = ? where stock_id = ?";
        return Update(conn, update, stockInfo.getStatus(), stockInfo.getStockId());

    }
}