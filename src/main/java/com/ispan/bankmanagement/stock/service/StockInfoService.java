package com.ispan.bankmanagement.stock.service;

import com.ispan.bankmanagement.stock.dao.StockInfoDao;
import com.ispan.bankmanagement.stock.dto.StockInfoDto;
import com.ispan.bankmanagement.stock.entity.StockInfoEntity;
import com.ispan.bankmanagement.util.ConnUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static com.ispan.bankmanagement.stock.entity.StockInfoEntity.ConvertDtoToEntity;
import static com.ispan.bankmanagement.util.ConnUtil.closeResource;

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
    public void InsertStockInfoService(StockInfoDto stockInfoDto) {
        Connection conn = null;
        try {
            conn = ConnUtil.getConn();
            conn.setAutoCommit(false);//關閉自動提交
            StockInfoEntity stockInfoEntity = ConvertDtoToEntity(stockInfoDto);
            stockInfoDao.InsertStockInfo(conn, stockInfoEntity);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                throw new RuntimeException("新增單筆失敗", e);
            }
        } finally {
            closeResource(conn);
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
            return StockInfoDto.ConvertEntityToDto(stockInfoEntity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //依據id刪除股票資訊
    public void DeleteStockInfoService(int id) {
        try (Connection conn = ConnUtil.getConn();) {
            stockInfoDao.DeleteById(conn, id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //依據id更新資料
    public void UpdateStockInfoService(StockInfoDto stockInfoDto) {
        Connection conn = null;
        try {
            conn = ConnUtil.getConn();
            conn.setAutoCommit(false);
            StockInfoEntity stockInfoEntity = ConvertDtoToEntity(stockInfoDto);
            stockInfoDao.UpdateStockInfo(conn, stockInfoEntity);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                throw new RuntimeException("修改單筆失敗", e);
            }
        } finally {
            closeResource(conn);
        }
    }
}
