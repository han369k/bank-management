package com.ispan.bankmanagement.stock.service;

import com.ispan.bankmanagement.stock.dao.StockInfoDao;
import com.ispan.bankmanagement.stock.dto.StockInfoDto;
import com.ispan.bankmanagement.stock.entity.StockInfoEntity;
import com.ispan.bankmanagement.util.ConnUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static com.ispan.bankmanagement.stock.entity.StockInfoEntity.ConvertDtoToEntity;
import static com.ispan.bankmanagement.util.ConnUtil.closeResource;
import static com.ispan.bankmanagement.util.ConnUtil.getConn;

public class StockInfoService {
    private final StockInfoDao stockInfoDao = new StockInfoDao();

    //同時新增兩筆股票資料
    public void UpdateTwoStockInfoService(StockInfoEntity stockInfoEntity1, StockInfoEntity stockInfoEntity2) {
        Connection conn = null;
        try {
            conn = ConnUtil.getConn();
            conn.setAutoCommit(false);//關閉自動提交
            stockInfoDao.InsertStockInfo(conn, stockInfoEntity1);
            stockInfoDao.InsertStockInfo(conn, stockInfoEntity2);
            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("新增失敗，已執行回滾，資料庫不會有任何變動。");
                } catch (SQLException e1) {
                    e1.printStackTrace();
                } finally {
                    closeResource(conn);
                }
            }
        }
    }

    //新增單筆股票基本資訊
    public boolean InsertStockInfoService(StockInfoDto stockInfoDto) {
        // 防呆：如果前端傳來空物件，直接回傳 false，不執行資料庫操作
        if (stockInfoDto == null) {
            return false;
        }
        try (Connection conn = ConnUtil.getConn()) {
            try {
                conn.setAutoCommit(false);//關閉自動提交
                StockInfoEntity stockInfoEntity = ConvertDtoToEntity(stockInfoDto);
                int row = stockInfoDao.InsertStockInfo(conn, stockInfoEntity);
                conn.commit();
                return row > 0;
            } catch (SQLException e) {
                conn.rollback();// 發生 SQL 錯誤（如重複 ID）則回滾
                throw e;
            }
        } catch (SQLException e) {
            // 這裡會處理連線錯誤或內層丟出的錯誤
            throw new RuntimeException("新增股票資訊失敗，ID: " + stockInfoDto.getStockId(), e);
        }
    }

    //取得全部的股票基本資訊
    public List<StockInfoDto> GetAllStocksInfoService() {
        try (Connection conn = ConnUtil.getConn();) {
            //拿到資料庫原始資料
            List<StockInfoEntity> daoList = stockInfoDao.GetAll(conn);

            //完成整份清單的轉換 (Mapping) 將daoList (Stream)，全部轉成 (Map) VO，最後包成一個 List。」
            return daoList.stream()
                    .map(StockInfoDto::ConvertEntityToDto)
                    .toList();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //取得單筆股票基本資訊
    public StockInfoDto GetStockInfoService(int id) {
        try (Connection conn = ConnUtil.getConn();) {
            StockInfoEntity stockInfoEntity = stockInfoDao.GetById(conn, id);
            if (stockInfoEntity == null) {
                return null;
            }
            return StockInfoDto.ConvertEntityToDto(stockInfoEntity);
        } catch (SQLException e) {
            throw new RuntimeException("查詢股票資訊時發生資料庫錯誤，ID: " + id, e);
        }
    }

    //依據id刪除股票資訊
    public boolean DeleteStockInfoService(int id) {
        try (Connection conn = ConnUtil.getConn();) {
            int rows = stockInfoDao.DeleteById(conn, id);
            return rows > 0;// 如果有刪到(>0)就回傳 true，沒刪到(==0)就回傳 false
        } catch (SQLException e) {
            throw new RuntimeException("刪除股票資訊時發生資料庫錯誤，ID: " + id, e);
        }
    }

    //依據id更新資料
    public boolean UpdateStockInfoService(StockInfoDto stockInfoDto) {

        try (Connection conn = ConnUtil.getConn();) {
            try {
                conn.setAutoCommit(false);
                StockInfoEntity stockInfoEntity = ConvertDtoToEntity(stockInfoDto);
                int row = stockInfoDao.UpdateStockInfo(conn, stockInfoEntity);
                conn.commit();
                return row > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("修改單筆資料失敗，ID: " + stockInfoDto.getStockId(), e);
        }
    }
}
