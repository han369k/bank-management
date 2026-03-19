# 🏦 爪哇銀行管理系統 (Java Bank Management System)

這是一個基於 Java EE (Servlet) 配合 jQuery 開發的銀行內部管理後台系統。提供銀行行員進行顧客管理、帳戶監控、貸款審核、信用卡與基金業務之核心功能。

## 🛠️ 技術棧 (Tech Stack)

* **前端 (Frontend)**: HTML5, CSS3, JavaScript (jQuery)
* **後端 (Backend)**: Java 17, Jakarta EE (Servlet / JSP), Tomcat 10
* **資料庫 (Database)**: MS SQL Server
* **建置工具 (Build Tool)**: Maven

## 📂 系統架構 (Architecture)

本專案採用經典的 MVC 四層架構進行職責分離：
* `Controller Layer`: 負責接收 API 請求與回傳 JSON (`Servlet`)。
* `Service Layer`: 處理核心商業邏輯與狀態判斷。
* `DAO Layer`: 負責與 MS SQL Server 進行資料存取 (`JDBC`)。
* `VO / DTO`: 封裝傳遞資料的純物件容器。

## 🚀 本地端啟動指南 (Getting Started)

為了讓專案順利運行，請開發人員嚴格遵守以下啟動步驟：

### 1. 環境準備
* 確保已安裝 **JDK 17**。
* 確保已安裝 **Tomcat 10.1+**。
* 確保已安裝 **MS SQL Server**。

### 2. 資料庫初始化
1. 進入 SQL Server，建立一個新的資料庫名為 `bank_db` (可自訂)。
2. 執行本專案根目錄下的 `/database/init.sql`。
3. 該腳本將自動建立 15 張關聯表。

### 3. 設定資料庫連線密碼
1. 在 `src/main/resources/` 目錄下建立 `config.properties` 檔案。
2. 填寫你的本機資料庫連線資訊：
   ```properties
   db.url=jdbc:sqlserver://localhost:1433;databaseName=bank_db;encrypt=false
   db.user=你的帳號 (如: sa)
   db.password=你的密碼
   ```
   *(⚠️ 注意：`config.properties` 已加入 `.gitignore`，請勿將自己的密碼推上遠端！)*

### 4. 啟動專案
* 在 IntelliJ IDEA 或 Eclipse 中載入 Maven 專案 (`pom.xml`)。
* 將專案部署至 Tomcat 10 伺服器並啟動。
* 預設入口網站為：`http://localhost:8080/bank-management/`

## 🔗 相關開發文件

* [規範書]: https://www.notion.so/327ebfea4fd180b3a0d5e4412387ee91?source=copy_link

## 👥 開發團隊 (Team Members)

* **Huang Hank**: 專案統籌 / 架構規劃
* **以琳**: 系統權限模組、KYC 與顧客管理
* **漢億**: 存款與帳務監控模組
* **泓翔**: 授信放款業務模組
* **王昶**: 信用卡業務模組
* **世帆**: 財富管理模組
