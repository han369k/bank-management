package com.ispan.bankmanagement.account.service;


import com.ispan.bankmanagement.account.dao.AccountDAO;
import com.ispan.bankmanagement.account.entity.AccountEntity;
import com.ispan.bankmanagement.util.ConnUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class AccountService {
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    AccountDAO accountDAO = new AccountDAO();

    // 新增帳戶
    public void createAcc(AccountEntity accountEntity) {
        try (Connection conn = ConnUtil.getConn()) {

            // 檢查帳戶資料
            if (accountEntity == null || accountEntity.getAccount() == null || accountEntity.getAccount().trim().isEmpty()) {
                logger.warn("新增失敗，提供的資料格式不正確。");
                throw new IllegalArgumentException("帳戶資料與帳號不可為空。");
            }
            // 檢查帳號是否已存在
            if (accountDAO.findByAccount(conn, accountEntity.getAccount()) != null) {
                // 這是一個業務邏輯錯誤，不是資料庫錯誤
                logger.warn("嘗試新增已存在的帳戶: {}", accountEntity.getAccount());
                throw new IllegalArgumentException("帳號 " + accountEntity.getAccount() + " 已存在，無法重複新增。");
            }

            // 限制帳號長度為12碼
            if (accountEntity.getAccount().trim().length() != 12) {
                throw new IllegalArgumentException("帳號格式不合法，長度應為12碼");
            }

            accountDAO.insert(conn, accountEntity);
            logger.info("帳戶 {} 建立成功。", accountEntity.getAccount());
        } catch (SQLException e) {
            throw new RuntimeException("連線失敗，請稍後再試。",e);
        }
    }

    // 用帳號查
    public AccountEntity searchAccountByAccount(String account) {
        try (Connection conn = ConnUtil.getConn()) {
            AccountEntity acc = accountDAO.findByAccount(conn, account);

            // DAO回傳null 代表檢查不到
            if ( acc == null ) {
                logger.warn("查詢的帳戶 {} 不存在。", account);
                throw new RuntimeException("查無此帳戶: " + account);
            }

            logger.info("成功查詢到帳戶: {}", account);
            return acc;

        } catch (SQLException e) {
            logger.error("服務層在取得資料庫連線時發生錯誤", e);
            throw new RuntimeException("連線失敗，請稍後再試。", e);
        }
    }

    // todo: 需要改寫DAO (join custormer)
    //public AccountEntity searchAccountByCustormerName(){}

    // 查全部
    public List<AccountEntity> searchAllAccount(AccountEntity accountEntity) {
        try (Connection conn = ConnUtil.getConn()) {
            List<AccountEntity> list = accountDAO.query(conn, accountEntity);

            logger.info("帳戶條件查詢完成，共找到 {} 筆資料。", list.size());
            return list;
        } catch (SQLException e) {
            logger.error("服務層在取得資料庫連線時發生錯誤", e);
            throw new RuntimeException("連線失敗，請稍後再試。", e);
        }
    }

    // 狀態更新
    // todo: insert adminLog
    public void updateStatus( String account, String status) {
        try (Connection conn = ConnUtil.getConn()) {
            // 防呆 查帳號是否存在
            AccountEntity accountEntity = accountDAO.findByAccount(conn, account);

            if (accountEntity == null || accountEntity.getAccount() == null || accountEntity.getAccount().trim().isEmpty()) {
                logger.warn("編輯失敗， {} 不存在。", account);
                throw new RuntimeException("編輯失敗，帳號不存在。");
            } else{
                if(accountDAO.updateStatus(conn,account,status)){
                    logger.info("狀態更新成功, account: {}, 新狀態: {}", account, status);
                }else{
                    throw new RuntimeException("狀態更新失敗");
                };
            }
        } catch (SQLException e) {
            logger.error("服務層在取得資料庫連線時發生錯誤", e);
            throw new RuntimeException(e);
        }
    }
}
