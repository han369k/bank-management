package com.ispan.bankmanagement.stock.service;

import com.ispan.bankmanagement.stock.dao.StockInfoDao;
import com.ispan.bankmanagement.stock.entity.StockInfoEntity;
import com.ispan.bankmanagement.util.ConnUtil;

import java.sql.Connection;
import java.sql.SQLException;

public class StockInfoService {

    //同時新增兩筆股票資料
    public void UpdateTwoStockInfoService(StockInfoEntity stockInfoEntity1, StockInfoEntity stockInfoEntity2) {
        Connection conn = null;
        try {
            conn = ConnUtil.getConn();
            conn.setAutoCommit(false);//關閉自動提交
            StockInfoDao stockInfoDao = new StockInfoDao();
            stockInfoDao.InsertStockInfo(conn, stockInfoEntity1);
            stockInfoDao.InsertStockInfo(conn, stockInfoEntity2);
            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    //關閉前還原 AutoCommit 狀態
                    conn.setAutoCommit(true);
                    System.err.println("新增失敗，已執行回滾，資料庫不會有任何變動。");
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void TestInsertStockInfoService(StockInfoEntity stockInfoEntity) {
        Connection conn = null;
        try {
            conn = ConnUtil.getConn();
            conn.setAutoCommit(false);//關閉自動提交
            StockInfoDao stockInfoDao = new StockInfoDao();
            stockInfoDao.InsertStockInfo(conn, stockInfoEntity);
            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
