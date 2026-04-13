package com.ispan.bankmanagement.common.util;

import java.sql.Connection;

public class TestDbConn {

    public static void main(String[] args) {
        System.out.println("=== 爪哇銀行：資料庫連線測試開始 ===");
        
        // 1. 直接呼叫組長寫好的 getConn() 方法
        // 組長的 getConn() 會回傳一個 java.sql.Connection 物件
        try (Connection conn = ConnUtil.getConn()) {
            
            if (conn != null && !conn.isClosed()) {
                System.out.println("------------------------------------");
                System.out.println("✅ [成功] 恭喜！連線已接通。");
                System.out.println("📍 資料庫名稱：" + conn.getMetaData().getDatabaseProductName());
                System.out.println("👤 使用者帳號：" + conn.getMetaData().getUserName());
                System.out.println("------------------------------------");
            }
            
        } catch (Exception e) {
            System.err.println("------------------------------------");
            System.err.println("❌ [失敗] 無法連線至資料庫！");
            System.err.println("💡 錯誤訊息：" + e.getMessage());
            System.err.println("------------------------------------");
            e.printStackTrace();
        }
        
        System.out.println("=== 測試結束 ===");
    }
}