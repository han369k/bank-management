package com.ispan.bankmanagement.util;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class ConnUtil {

    // 用來記錄錯誤的log
    private static final Logger logger = LoggerFactory.getLogger(ConnUtil.class);

    // DataSource 連線池
    private static final HikariDataSource dataSource;

    // 驗證設定檔
    private static void validate(Properties prop) {
        // 放入設定檔內容 對應欄位
        String[] requiredKeys = {"jdbc.driver" , "jdbc.url" , "jdbc.user" , "jdbc.password"};
        // 逐一檢查
        for(String key : requiredKeys ){
            // 這邊做兩件事
            // 1. 設定檔有沒有寫這個屬性？
            // 2. 有這個屬性，但有沒有填？ -> "jdbc.user=" 沒寫 報錯
            // 防呆 trim 避免前有有空格導致格式不合法
            if(prop.getProperty(key) == null || prop.getProperty(key).trim().isEmpty()){
                // 明確指出是哪裡出問題
                throw new RuntimeException("db.properties 內容不合法: " + key );
            }
        }
    }

    // Hikari 連線池設定
    static {
        // 將 Properties 改為區域變數，避免常駐在記憶體中
        Properties prop = new Properties();

        try (InputStream inputStream = ConnUtil.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            // 確認檔案路徑是否存在
            if(inputStream == null){
                throw new RuntimeException("db.properties 檔案不存在，請確認檔案是否在src/main/resources 下");
            }

            // 讀取+檢查
            prop.load(inputStream);
            validate(prop);

            // 以下 HikariCP
            HikariConfig config = new HikariConfig();// Hikari 物件

            // 導入設定檔
            config.setDriverClassName(prop.getProperty("jdbc.driver"));
            config.setJdbcUrl(prop.getProperty("jdbc.url"));
            config.setUsername(prop.getProperty("jdbc.user"));
            config.setPassword(prop.getProperty("jdbc.password"));

            config.setMaximumPoolSize(10);// 最大10條
            config.setMinimumIdle(2);// 最小空閒數
            config.setConnectionTimeout(30000);// 等待連線超時(單位毫秒)

            // 連線池設定
            // 把設定資料傳入dataSource 讓HikariCP 來使用
            dataSource = new HikariDataSource(config);
            logger.info("連線池初始化成功");

            // 這段是把程式註冊一個標記 如果程式關閉的時候 準備一個thread去運行關閉作業(防崩潰)
            Runtime.getRuntime().addShutdownHook(new Thread(ConnUtil::shutdown));

        } catch (IOException e) {
            logger.error("讀取 db.properties 失敗，請確認檔案格式或權限。", e);
            throw new RuntimeException("設定檔讀取失敗",e);
        } catch (IllegalArgumentException e ){
            logger.error("HikariCP 設定參數有誤！請檢查 jdbc.url 或 driver 名稱。", e);
            throw new RuntimeException("設定檔內容不合法",e);
        } catch (Exception e){
            logger.error("DBUtil 初始化發生預期外的錯誤。", e);
            throw new RuntimeException("連線池初始化失敗",e);
        }
    }

    // 建立連線池
    public static Connection getConn() throws SQLException {
        return dataSource.getConnection();
    }

    // 歸還連線池
    // 給不使用try with resource的方法使用
    // 在寫code的時候可以考慮使用 Try-With-Resources 語法，這樣就不需要呼叫這個方法。
    public static void closeResource(Connection conn, Statement stat, ResultSet rs){

        // 這個物件的用處 以下三個關閉的方法 如果有任何一個方法沒有關閉成功 他會先傳出第一個例外的訊息
        // 因為如果有兩個以上的例外 會造成第一個例外被吃掉
        SQLException firstException = null ;

        if(rs != null){
            try {
                rs.close();
            } catch (SQLException e) {
                logger.error("ResultSet 關閉失敗", e);
                firstException = e;
            }
        }
        if(stat != null) {
            try {
                stat.close();
            } catch (SQLException e) {
                logger.error("Statement 關閉失敗", e);
                if (firstException == null) firstException = e;
            }
        }
        if(conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error("Connection 關閉失敗", e);
                if (firstException == null) firstException = e;
            }
        }

        logger.debug("資源已釋放完畢");

        if(firstException != null){
            logger.error("關閉資源過程中發生錯誤,請檢查詳細堆疊", new RuntimeException(firstException));
            throw new RuntimeException("關閉資源過程中發生錯誤", firstException);
        }
    }

    // polymorphism
    public static void closeResource(Connection conn,Statement stat){
        // 原本的方法要三個param 用這個多型方法 可以適用其他非查詢的方法使用
        ConnUtil.closeResource(conn,stat,null); // 呼叫上面的方法 帶入null
    }

    public static void shutdown() {
        // 如果 dataSource裡面還有連線資訊 && dataSource尚未關閉
        if ( dataSource != null  && !dataSource.isClosed() ){
            dataSource.close();
            // logger.info("資料庫連線池已關閉");
        }
    }

    public static void main(String[] args) {
        try {
            Connection conn = ConnUtil.getConn();
            System.out.println(!conn.isClosed());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}