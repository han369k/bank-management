# 🏦 爪哇銀行管理系統 (Java Bank Management System)

這是一個銀行內部管理後台系統，提供銀行行員進行顧客管理、帳戶監控、貸款審核、信用卡與股票業務之核心功能。
本專案目前經歷了架構現代化的演進，同時維護「傳統 Java EE」與「現代 Spring Boot」兩種技術版本。

## 🌿 分支策略與版本說明 (Branch Strategy)

本數據庫採用雙軌制進行開發與維護，請開發人員依據任務切換至對應的 Branch：

* **`main` 分支 (穩定版)**：保持最初的傳統架構，使用 **Java EE (Servlet + JSP) / Java 17**。
* **`dev-1` 分支 (現代化開發版)**：全新重構的版本，全面升級至 **Spring Boot 4.0.5 + Java 21 (Temurin)**，此為未來主要開發分支。

---

## 🛠️ 技術棧 (Tech Stack)

### `dev-1` 分支 (Spring Boot 架構)
* **後端 (Backend)**: Java 21 (Eclipse Temurin), Spring Boot 4.0.5
* **資料存取 (Data Access)**: Spring Data JPA, Hibernate
* **安全驗證 (Security)**: Spring Security
* **建置工具 (Build Tool)**: Maven
* **伺服器 (Server)**: 內建 Tomcat (Embedded)

### `main` 分支 (傳統 Servlet 架構)   
* **後端 (Backend)**: Java 17, Jakarta EE (Servlet / JSP)
* **前端 (Frontend)**: HTML5, CSS3, JavaScript (jQuery)
* **資料存取 (Data Access)**: 傳統 JDBC (DAO Pattern)
* **建置工具 (Build Tool)**: Maven
* **伺服器 (Server)**: 需外掛 Tomcat 10+

> **共用基礎設施**：資料庫皆採用 **MS SQL Server**。

---

## 📂 系統架構 (Architecture)

依據切換的分支不同，專案的底層目錄與架構有所差異：

* **`dev-1` (Spring Boot)**：採用標準分層架構，包含 `Controller` (處理 API 請求)、`Service` (核心業務邏輯)、`Repository` (JPA 介面)、`Entity` (資料庫映射) 以及 `DTO` (資料傳輸物件)。
* **`main` (Java EE)**：採用經典 MVC 模式，包含 `Servlet` (接收請求)、`Service` (商業邏輯)、`DAO` (JDBC 存取) 以及 `VO/DTO`。

---

## 🚀 本地端啟動指南 (Getting Started)

為了讓專案順利運行，請開發人員嚴格遵守以下啟動步驟：

### 1. 資料庫初始化 (雙分支共通)
1. 進入 MS SQL Server，建立一個新的資料庫名為 `bank_db` (可自訂)。
2. 執行本專案根目錄下的 `/database/init.sql`。
3. 該腳本將自動建立 15 張關聯表。

### 2. 環境設定與啟動 

#### ➡️ 如果你使用 `dev-1` 分支 (Spring Boot 4 + Java 21)
1. 確保已安裝 **JDK 21**。
2. 進入 `src/main/resources/` 目錄。
3. 在 `application.properties` (或 `application-local.properties`) 中設定資料庫：
   ```properties
   spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=bank_db;encrypt=false
   spring.datasource.username=你的帳號 (如: sa)
   spring.datasource.password=你的密碼
   spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
   ```
4. 找到 `BankmanagementApplication.java` 並直接執行 `main` 方法，系統將運行於 `http://localhost:8080/`。

#### ➡️ 如果你使用 `main` 分支 (Servlet + Java 17)
1. 確保已安裝 **JDK 17** 與 **Tomcat 10.1+**。
2. 在 `src/main/resources/` 目錄下建立 `config.properties` 檔案：
   ```properties
   jdbc.driver=com.microsoft.sqlserver.jdbc.SQLServerDriver
   jdbc.url=jdbc:sqlserver://localhost:1433;databaseName=bank_db;encrypt=false;trustServerCertificate=true
   jdbc.user=你的帳號
   jdbc.password=你的密碼
   ```
3. 將專案部署至 Tomcat 10 伺服器並啟動。預設入口網站為：`http://localhost:8080/bank_management/`

> *(⚠️ 注意：含有密碼的 properties 檔案已被加入 `.gitignore`，請勿將自己的密碼推上遠端！)*

---

## 🔗 相關開發文件

* [開發規範書 & Notion 進度表]: https://www.notion.so/327ebfea4fd180b3a0d5e4412387ee91?source=copy_link

## 👥 開發團隊 (Team Members)

* **Huang Hank**: 專案統籌 / 架構規劃 (Spring Boot 重構)
* **以琳**: 系統權限模組、KYC 與顧客管理
* **漢億**: 存款與帳務監控模組
* **泓翔**: 授信放款業務模組
* **王昶**: 信用卡業務模組
* **世帆**: 股票業務模組
