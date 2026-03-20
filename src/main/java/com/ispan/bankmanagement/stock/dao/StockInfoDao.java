package com.ispan.bankmanagement.stock.dao;

import com.ispan.bankmanagement.stock.entity.StockInfoEntity;
import com.ispan.bankmanagement.util.ConnUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StockInfoDao {

    public List<StockInfoEntity> GetAll() {
        String sql = "select stock_id, stock_name from stock_info";
        ArrayList<StockInfoEntity> list = new ArrayList<>();

        try (Connection conn = ConnUtil.getConn();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery();
        ) {
            while (rs.next()) {
                StockInfoEntity stockInfo = new StockInfoEntity();
                stockInfo.setStockId(rs.getInt("stock_id"));
                stockInfo.setStockName(rs.getString("stock_name"));
                list.add(stockInfo);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}
