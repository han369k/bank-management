package com.ispan.bankmanagement.stock.service;

import com.ispan.bankmanagement.stock.dao.InfoDao;
import com.ispan.bankmanagement.stock.dto.StockInfoDto;
import com.ispan.bankmanagement.stock.entity.StockInfoEntity;
import com.ispan.bankmanagement.util.ConnUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static com.ispan.bankmanagement.util.ConnUtil.closeResource;

public class StockInfoService {
    private final InfoDao stockInfoDao = new InfoDao();

    public static StockInfoEntity ConvertToEntity(StockInfoDto stockInfoDto) {
        StockInfoEntity stockInfoEntity = new StockInfoEntity();
        stockInfoEntity.setStockId(stockInfoDto.getStockId());
        stockInfoEntity.setStockName(stockInfoDto.getStockName());
        return stockInfoEntity;
    }

    public static StockInfoDto ConvertToDto(StockInfoEntity stockInfoEntity) {
        StockInfoDto stockInfoDto = new StockInfoDto();
        stockInfoDto.setStockId(stockInfoEntity.getStockId());
        stockInfoDto.setStockName(stockInfoEntity.getStockName());

        return stockInfoDto;
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
                StockInfoEntity stockInfoEntity = ConvertToEntity(stockInfoDto);
                int row = stockInfoDao.InsertInfo(conn, stockInfoEntity);
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

            //完成整份清單的轉換 (Mapping) 將daoList (Stream)，全部轉成 (Map) dto，最後包成一個 List。」
            return daoList.stream()
                    .map(StockInfoService::ConvertToDto)
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
            return ConvertToDto(stockInfoEntity);
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
                StockInfoEntity stockInfoEntity = ConvertToEntity(stockInfoDto);
                int row = stockInfoDao.UpdateInfo(conn, stockInfoEntity);
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
