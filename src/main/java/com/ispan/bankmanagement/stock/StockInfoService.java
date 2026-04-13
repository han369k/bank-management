package com.ispan.bankmanagement.stock;

import com.ispan.bankmanagement.stock.dto.StockInfoDto;
import com.ispan.bankmanagement.common.util.ConnUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static com.ispan.bankmanagement.common.util.ConnUtil.closeResource;

public class StockInfoService {
    private final InfoDao stockInfoDao = new InfoDao();

    public static StockInfoEntity ConvertToEntity(StockInfoDto stockInfoDto) {
        StockInfoEntity stockInfoEntity = new StockInfoEntity();
        stockInfoEntity.setStockId(stockInfoDto.getStockId());
        stockInfoEntity.setStockName(stockInfoDto.getStockName());
        stockInfoEntity.setStatus(Boolean.parseBoolean(stockInfoDto.getStatus()));
        return stockInfoEntity;
    }

    public static StockInfoDto ConvertToDto(StockInfoEntity stockInfoEntity) {
        StockInfoDto stockInfoDto = new StockInfoDto();
        stockInfoDto.setStockId(stockInfoEntity.getStockId());
        stockInfoDto.setStockName(stockInfoEntity.getStockName());
        
        // 將 Boolean 安全地轉回 String，若為 null 則維持 null 或給預設值
        if (stockInfoEntity.getStatus() != null) {
            stockInfoDto.setStatus(String.valueOf(stockInfoEntity.getStatus()));
        }

        return stockInfoDto;
    }

    public static StockInfoVO ConvertToVO(StockInfoEntity stockInfoEntity) {
        StockInfoVO stockInfoVO = new StockInfoVO();
        stockInfoVO.setStockId(stockInfoEntity.getStockId());
        stockInfoVO.setStockName(stockInfoEntity.getStockName());
        // 增加 null 防呆，避免拆箱(Unboxing)時發生 NullPointerException
        stockInfoVO.setStatus(Boolean.TRUE.equals(stockInfoEntity.getStatus()) ? "true" : "false");
        return stockInfoVO;
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
    public List<StockInfoVO> GetAllStocksInfoService() {
        try (Connection conn = ConnUtil.getConn();) {
            //拿到資料庫原始資料
            List<StockInfoEntity> daoList = stockInfoDao.GetAll(conn);

            //完成整份清單的轉換 (Mapping) 將daoList (Stream)，全部轉成 (Map) dto，最後包成一個 List。」
            return daoList.stream()
                    .map(StockInfoService::ConvertToVO)
                    .toList();
        } catch (SQLException e) {
            e.printStackTrace(); // 這行能讓你在 IDE 的 Console 看到詳細報錯原因
            throw new RuntimeException("資料庫查詢出錯: " + e.getMessage());
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
            e.printStackTrace();
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