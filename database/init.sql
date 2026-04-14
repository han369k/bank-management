-- 行員/管理員資料表
CREATE TABLE [ADMINISTRATOR] (
                                 [admin_id] INT PRIMARY KEY,             -- 行員編號 (PK)
                                 [username] NVARCHAR(50),                -- 登入帳號
                                 [password_hash] VARCHAR(255),           -- 登入密碼(Hash)
                                 [name] NVARCHAR(50),                    -- 行員姓名
                                 [email] VARCHAR(100),                   -- 聯絡信箱
                                 [phone_number] VARCHAR(20),             -- 聯絡電話
                                 [role] VARCHAR(20),                     -- 角色：MANAGER / CLERK
                                 [status] VARCHAR(10),                   -- 狀態：ACTIVE / SUSPENDED
                                 [created_at] DATETIME2,                 -- 建立時間
                                 [updated_at] DATETIME2                  -- 更新時間
)
GO

-- 行員操作日誌表
CREATE TABLE [ADMIN_LOG] (
                             [log_id] INT PRIMARY KEY,               -- 日誌流水號 (PK)
                             [admin_id] INT,                         -- 操作行員 (FK)
                             [action_type] VARCHAR(20),              -- 動作類型 (如: LOGIN, UPDATE)
                             [target_table] VARCHAR(50),             -- 異動資料表
                             [target_record_id] VARCHAR(50),         -- 異動資料ID
                             [action_details] NVARCHAR(MAX),         -- 詳細內容
                             [action_timestamp] DATETIME2,           -- 操作時間
                             [ip_address] VARCHAR(50)                -- 操作IP
)
GO

-- 顧客資料表
CREATE TABLE [CUSTOMER] (
                            [customer_id] INT PRIMARY KEY,          -- 顧客系統代號 (PK)
                            [id_number] VARCHAR(20),                -- 身分證字號
                            [name] NVARCHAR(50),                    -- 顧客姓名
                            [date_of_birth] DATE,                   -- 生日
                            [nationality] VARCHAR(50),              -- 國籍
                            [address] NVARCHAR(255),                -- 聯絡地址
                            [phone] VARCHAR(20),                    -- 聯絡電話
                            [email] VARCHAR(100),                   -- 聯絡信箱
                            [password_hash] VARCHAR(255),           -- 網銀登入密碼
                            [failed_login_attempts] INT,            -- 登入失敗次數
                            [income] DECIMAL(19,4),                 -- 年收入
                            [credit_score] INT,                     -- 信用評分
                            [created_at] DATETIME2,                 -- 建立時間
                            [updated_at] DATETIME2,                 -- 更新時間
                            [status] VARCHAR(20)                    -- 狀態：PENDING / ACTIVE / FROZEN
)
GO

-- KYC 顧客身分審查紀錄表
CREATE TABLE [KYC_RECORD] (
                              [kyc_id] INT PRIMARY KEY,               -- 審查案件編號 (PK)
                              [customer_id] INT,                  -- 顧客代號 (FK)
                              [document_type] VARCHAR(20),            -- 證件類型 (如: ID_CARD)
                              [document_number] VARCHAR(50),          -- 證件號碼
                              [expiry_date] DATE,                     -- 證件到期日
                              [document_image_url] VARCHAR(255),      -- 證件影像路徑
                              [verification_status] VARCHAR(20),      -- 審查狀態：PENDING / APPROVED
                              [verified_by_admin] INT,                -- 審查行員代號 (FK)
                              [submission_date] DATETIME2,            -- 送件時間
                              [verification_date] DATETIME2,          -- 審核時間
                              [comments] NVARCHAR(255)                -- 審核備註/退件原因
)
GO

-- 銀行帳戶資料表
CREATE TABLE [ACCOUNT] (
                           [account_number] VARCHAR(12) PRIMARY KEY, -- 銀行帳號 (PK)
                           [customer_id] INT,                  -- 顧客代號 (FK)
                           [type] VARCHAR(20),                     -- 帳戶類型 (活存/定存)
                           [currency] VARCHAR(3),                  -- 幣別 (如: TWD)
                           [balance] DECIMAL(19,4),                -- 帳戶餘額
                           [status] VARCHAR(10),                   -- 狀態：ACTIVE / FROZEN
                           [create_at] DATETIME2,                  -- 開戶時間
                           [change_at] DATETIME2                   -- 最後異動時間
)
GO

-- 帳戶交易流水帳表
CREATE TABLE [TRANS_LOG] (
                             [trans_log_id] INT IDENTITY(1,1) PRIMARY KEY, -- 交易流水號 (PK)
                             [reference_id] VARCHAR(50),             -- 外部參考編號
                             [amount] DECIMAL(19,4),                 -- 交易金額
                             [type] VARCHAR(20),                     -- 類型 (存款/提款/轉帳)
                             [operation_account] VARCHAR(20),        -- 操作帳號 (FK)
                             [other_account] VARCHAR(20),            -- 對手帳號
                             [balance] DECIMAL(19,4),                -- 交易後餘額
                             [transaction_time] DATETIME2,           -- 交易時間
                             [note] NVARCHAR(255)                    -- 備註/摘要
)
GO

-- 貸款申請表
CREATE TABLE [LOAN_APPLICATION] (
                                    [application_id] VARCHAR(20) PRIMARY KEY, -- 申請案號 (PK)
                                    [customer_id] INT,                  -- 顧客代號 (FK)
                                    [apply_type] VARCHAR(20),               -- 貸款類型
                                    [apply_amount] BIGINT,                  -- 申請金額
                                    [apply_period] INT,                     -- 申請期數(月)
                                    [rate] DECIMAL(4,2),                    -- 申請利率
                                    [create_time] DATETIME2(2),             -- 申請時間
                                    [approved_amount] BIGINT,               -- 核准金額
                                    [approved_period] INT,                  -- 核准期數
                                    [approved_rate] DECIMAL(4,2),           -- 核准利率
                                    [status] VARCHAR(20),                   -- 狀態：PENDING / APPROVED / REJECTED
                                    [reviewer_id] INT,                      -- 審核行員代號 (FK)
                                    [review_time] DATETIME2(2)              -- 審核時間
)
GO

-- 貸款還款紀錄表
CREATE TABLE [REPAYMENT_RECORD] (
                                    [repayment_id] BIGINT PRIMARY KEY,      -- 還款流水號 (PK)
                                    [application_id] VARCHAR(20),           -- 對應貸款案號 (FK)
                                    [account_number] VARCHAR(20),           -- 扣款帳號 (FK)
                                    [period] INT,                           -- 期數 (第幾期)
                                    [principal_paid] DECIMAL(19,4),         -- 已還本金
                                    [interest_paid] DECIMAL(19,4),          -- 已繳利息
                                    [penalty_paid] DECIMAL(19,4),           -- 已繳違約金
                                    [payment_date] DATETIME2                -- 實際還款時間
)
GO

-- 卡別資料表
CREATE TABLE [CARD_TYPE] (
                             [card_type_id] INT IDENTITY(1,1) PRIMARY KEY, -- 卡別ID (PK)
                             [card_type_name] NVARCHAR(50) NOT NULL,       -- 卡別名稱 (如: 現金回饋卡、旅遊卡)
                             [brand] NVARCHAR(20) NOT NULL,                -- 卡片品牌 (VISA / Master / JCB)
                             [annual_fee] DECIMAL(10,2),                   -- 年費
                             [cashback_rate] DECIMAL(5,2),                 -- 回饋比例 (%)
                             [card_image_url] VARCHAR(255)                 -- 卡片圖片URL
)
GO

-- 商家資料表
CREATE TABLE [MERCHANT] (
                            [merchant_id] INT IDENTITY(1,1) PRIMARY KEY,  -- 商家ID (PK)
                            [merchant_name] NVARCHAR(100) NOT NULL,       -- 商家名稱
                            [merchant_category] NVARCHAR(50)              -- 商家類型 (餐飲 / 交通 / 購物 / 娛樂)
)
GO

-- 信用卡申請主表
CREATE TABLE [CARD_APPLICATION] (
                                    [application_id] INT IDENTITY(1,1) PRIMARY KEY, -- 申請單ID (PK)
                                    [customer_id] INT NOT NULL,                     -- 客戶ID (對應 CUSTOMER)
                                    [apply_date] DATETIME DEFAULT GETDATE(),        -- 申請時間
                                    [status] NVARCHAR(20) DEFAULT 'PENDING',        -- 申請狀態 (PENDING / APPROVED / REJECTED / PARTIAL)
                                    [remark] NVARCHAR(200),                         -- 備註 (補件說明 / 人工審核內容)
                                    FOREIGN KEY (customer_id) REFERENCES [CUSTOMER](customer_id)
)
GO

-- 信用卡申請明細（可一次申請多張卡）
CREATE TABLE [CARD_APPLICATION_ITEM] (
                                         [item_id] INT IDENTITY(1,1) PRIMARY KEY,       -- 明細ID (PK)
                                         [application_id] INT NOT NULL,                 -- 申請單ID (對應主表)
                                         [card_type_id] INT NOT NULL,                   -- 卡別ID (對應 CARD_TYPE)
                                         [result] NVARCHAR(20) DEFAULT 'PENDING',       -- 審核結果 (PENDING / APPROVED / REJECTED)
                                         [approved_limit] DECIMAL(15,2),                -- 核准額度
                                         [annual_fee] DECIMAL(10,2),                    -- 核准後年費 (可能不同原卡別)
                                         [create_card_flag] BIT DEFAULT 0,              -- 是否已建立信用卡 (0=否 / 1=是)
                                         [remark] NVARCHAR(200),                        -- 備註 (拒絕原因等)
                                         FOREIGN KEY (application_id) REFERENCES CARD_APPLICATION(application_id),
                                         FOREIGN KEY (card_type_id) REFERENCES CARD_TYPE(card_type_id)
)
GO

-- 信用卡主表
CREATE TABLE [CREDIT_CARD] (
                               [card_id] INT IDENTITY(1,1) PRIMARY KEY,       -- 信用卡ID (PK)
                               [customer_id] INT NOT NULL,                    -- 客戶ID (持卡人)
                               [card_type_id] INT NOT NULL,                   -- 卡別ID
                               [application_item_id] INT NULL,                -- 對應申請明細 (從哪個申請來)
                               [card_number] VARCHAR(16) UNIQUE NOT NULL,     -- 信用卡卡號 (唯一)
                               [expiry_date] DATE NOT NULL,                   -- 卡片到期日
                               [credit_limit] DECIMAL(15,2),                  -- 信用額度
                               [current_balance] DECIMAL(15,2) DEFAULT 0,     -- 目前已使用金額
                               [create_date] DATETIME DEFAULT GETDATE(),      -- 開卡日期
                               [status] NVARCHAR(20) CHECK (status IN ('ACTIVE','BLOCKED')), -- 卡片狀態 (1=正常 0=停卡)
                               FOREIGN KEY (card_type_id) REFERENCES [CARD_TYPE](card_type_id)
)
GO

-- 信用卡交易紀錄表
CREATE TABLE [CREDIT_CARD_TRANSACTION] (
                                           [txn_id] INT IDENTITY(1,1) PRIMARY KEY,        -- 交易ID (PK)
                                           [card_id] INT NOT NULL,                        -- 信用卡ID
                                           [merchant_id] INT NOT NULL,                    -- 商家ID (對應 MERCHANT)
                                           [ref_txn_id] INT NULL,                         -- 關聯交易 (例如退款對應原交易)
                                           [txn_amount] DECIMAL(15,2) NOT NULL,           -- 交易金額
                                           [txn_type] NVARCHAR(20),                       -- 交易類型 (PURCHASE / REFUND / PAYMENT)
                                           [txn_date] DATETIME DEFAULT GETDATE(),         -- 交易時間
                                           [description] NVARCHAR(200),                   -- 備註 (交易說明)
                                           FOREIGN KEY (card_id) REFERENCES [CREDIT_CARD](card_id),
                                           FOREIGN KEY (merchant_id) REFERENCES [MERCHANT](merchant_id)
)
GO

-- 信用卡帳單表
CREATE TABLE [CREDIT_CARD_BILL] (
                                    [bill_id] INT IDENTITY(1,1) PRIMARY KEY,       -- 帳單ID (PK)
                                    [card_id] INT NOT NULL,                        -- 信用卡ID
                                    [billing_month] VARCHAR(7),                    -- 帳單月份 (例如: 2026-03)
                                    [bill_date] DATE,                              -- 帳單產生日期
                                    [due_date] DATE,                               -- 繳款截止日
                                    [total_amount] DECIMAL(15,2),                  -- 帳單總金額
                                    [minimum_payment] DECIMAL(15,2),               -- 最低應繳金額
                                    [paid_amount] DECIMAL(15,2),                   -- 已繳金額
                                    [bill_status] NVARCHAR(20),                    -- 帳單狀態 (UNPAID / PARTIAL / PAID)
                                    FOREIGN KEY (card_id) REFERENCES [CREDIT_CARD](card_id)
)
GO

-- 股票主檔資料表
CREATE TABLE [STOCK_MASTER] (
                                [stock_symbol] VARCHAR(10) PRIMARY KEY,        -- 股票代碼 (PK)，如 2330
                                [company_name] VARCHAR(100),                   -- 公司名稱
                                [market_type] VARCHAR(20),                     -- 上市或上櫃
                                [current_price] DECIMAL(10,2),                 -- 最新股價/收盤價
                                [opening_price] DECIMAL(10,2),                 -- 開盤價
                                [highest_price] DECIMAL(10,2),                 -- 最高價
                                [lowest_price] DECIMAL(10,2),                  -- 最低價
                                [trade_volume] BIGINT,                         -- 成交股數
                                [fee_rate] DECIMAL(5,4),                       -- 手續費率
                                [status] VARCHAR(20),                          -- 狀態：ACTIVE
                                [created_at] DATETIME2,                        -- 建立時間
                                [updated_at] DATETIME2                         -- 報價更新時間
)
GO

-- 顧客股票庫存表
CREATE TABLE [CUSTOMER_STOCK_HOLDING] (
                                          [holding_id] BIGINT PRIMARY KEY,               -- 庫存流水號 (PK)
                                          [customer_id] INT,                     -- 顧客代號 (FK)
                                          [account_number] VARCHAR(20),                  -- 扣款/入帳的活存帳號 (FK)
                                          [stock_symbol] VARCHAR(10),                    -- 股票代碼 (FK)
                                          [total_shares] INT,                            -- 總持有股數
                                          [average_cost] DECIMAL(10,2),                  -- 平均成本價 (用來算損益)
                                          [updated_at] DATETIME2                         -- 最後更新時間
)
GO

-- 股票交易明細表
CREATE TABLE [STOCK_TRANSACTION_RECORD] (
                                            [transaction_id] BIGINT PRIMARY KEY,           -- 交易流水號 (PK)
                                            [customer_id] INT,                         -- 顧客代號 (FK)
                                            [account_number] VARCHAR(20),                  -- 活存帳號 (FK)
                                            [stock_symbol] VARCHAR(10),                    -- 股票代碼 (FK)
                                            [transaction_type] VARCHAR(10),                -- BUY 或 SELL
                                            [deal_price] DECIMAL(10,2),                    -- 實際成交價
                                            [shares] INT,                                  -- 交易股數
                                            [fee_amount] DECIMAL(10,2),                    -- 手續費與證交稅總額
                                            [transaction_status] VARCHAR(20),              -- 狀態：COMPLETED
                                            [transaction_time] DATETIME2                   -- 成交時間
)
GO

-- 股票基本資訊表 (簡化版)
CREATE TABLE [stock_info] (
                              [stock_id] INT PRIMARY KEY,                    -- 股票ID (PK)
                              [stock_name] NVARCHAR(MAX) NULL,               -- 股票名稱
                              [status] BIT NULL                              -- 狀態 (1=正常 0=下市)
)
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '行員編號 (PK)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'ADMINISTRATOR',
     @level2type = N'Column', @level2name = 'admin_id';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '登入帳號',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'ADMINISTRATOR',
     @level2type = N'Column', @level2name = 'username';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '登入密碼(Hash)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'ADMINISTRATOR',
     @level2type = N'Column', @level2name = 'password_hash';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '行員姓名',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'ADMINISTRATOR',
     @level2type = N'Column', @level2name = 'name';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '角色：MANAGER / CLERK',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'ADMINISTRATOR',
     @level2type = N'Column', @level2name = 'role';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '狀態：ACTIVE / SUSPENDED',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'ADMINISTRATOR',
     @level2type = N'Column', @level2name = 'status';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '日誌流水號 (PK)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'ADMIN_LOG',
     @level2type = N'Column', @level2name = 'log_id';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '操作行員 (FK)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'ADMIN_LOG',
     @level2type = N'Column', @level2name = 'admin_id';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '動作類型 (如: LOGIN, UPDATE)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'ADMIN_LOG',
     @level2type = N'Column', @level2name = 'action_type';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '異動資料表',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'ADMIN_LOG',
     @level2type = N'Column', @level2name = 'target_table';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '異動資料ID',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'ADMIN_LOG',
     @level2type = N'Column', @level2name = 'target_record_id';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '詳細內容',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'ADMIN_LOG',
     @level2type = N'Column', @level2name = 'action_details';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '操作時間',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'ADMIN_LOG',
     @level2type = N'Column', @level2name = 'action_timestamp';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '操作IP',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'ADMIN_LOG',
     @level2type = N'Column', @level2name = 'ip_address';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '顧客系統代號 (PK)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CUSTOMER',
     @level2type = N'Column', @level2name = 'customer_id';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '身分證字號',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CUSTOMER',
     @level2type = N'Column', @level2name = 'id_number';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '顧客姓名',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CUSTOMER',
     @level2type = N'Column', @level2name = 'name';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '網銀登入密碼',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CUSTOMER',
     @level2type = N'Column', @level2name = 'password_hash';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '登入失敗次數',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CUSTOMER',
     @level2type = N'Column', @level2name = 'failed_login_attempts';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '年收入',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CUSTOMER',
     @level2type = N'Column', @level2name = 'income';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '信用評分',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CUSTOMER',
     @level2type = N'Column', @level2name = 'credit_score';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '狀態：PENDING / ACTIVE / FROZEN',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CUSTOMER',
     @level2type = N'Column', @level2name = 'status';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '審查案件編號 (PK)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'KYC_RECORD',
     @level2type = N'Column', @level2name = 'kyc_id';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '顧客代號 (FK)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'KYC_RECORD',
     @level2type = N'Column', @level2name = 'customer_id';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '證件類型 (如: ID_CARD)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'KYC_RECORD',
     @level2type = N'Column', @level2name = 'document_type';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '證件號碼',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'KYC_RECORD',
     @level2type = N'Column', @level2name = 'document_number';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '證件到期日',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'KYC_RECORD',
     @level2type = N'Column', @level2name = 'expiry_date';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '證件影像路徑',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'KYC_RECORD',
     @level2type = N'Column', @level2name = 'document_image_url';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '審查狀態：PENDING / APPROVED',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'KYC_RECORD',
     @level2type = N'Column', @level2name = 'verification_status';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '審查行員代號 (FK)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'KYC_RECORD',
     @level2type = N'Column', @level2name = 'verified_by_admin';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '送件時間',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'KYC_RECORD',
     @level2type = N'Column', @level2name = 'submission_date';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '審核時間',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'KYC_RECORD',
     @level2type = N'Column', @level2name = 'verification_date';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '審核備註/退件原因',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'KYC_RECORD',
     @level2type = N'Column', @level2name = 'comments';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '銀行帳號 (PK)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'ACCOUNT',
     @level2type = N'Column', @level2name = 'account_number';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '顧客代號 (FK)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'ACCOUNT',
     @level2type = N'Column', @level2name = 'customer_id';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '帳戶類型 (活存/定存)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'ACCOUNT',
     @level2type = N'Column', @level2name = 'type';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '幣別 (如: TWD)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'ACCOUNT',
     @level2type = N'Column', @level2name = 'currency';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '帳戶餘額',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'ACCOUNT',
     @level2type = N'Column', @level2name = 'balance';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '狀態：ACTIVE / FROZEN',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'ACCOUNT',
     @level2type = N'Column', @level2name = 'status';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '交易流水號 (PK)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'TRANS_LOG',
     @level2type = N'Column', @level2name = 'trans_log_id';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '外部參考編號',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'TRANS_LOG',
     @level2type = N'Column', @level2name = 'reference_id';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '交易金額',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'TRANS_LOG',
     @level2type = N'Column', @level2name = 'amount';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '類型 (存款/提款/轉帳)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'TRANS_LOG',
     @level2type = N'Column', @level2name = 'type';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '操作帳號 (FK)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'TRANS_LOG',
     @level2type = N'Column', @level2name = 'operation_account';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '對手帳號',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'TRANS_LOG',
     @level2type = N'Column', @level2name = 'other_account';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '交易後餘額',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'TRANS_LOG',
     @level2type = N'Column', @level2name = 'balance';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '交易時間',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'TRANS_LOG',
     @level2type = N'Column', @level2name = 'transaction_time';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '備註/摘要',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'TRANS_LOG',
     @level2type = N'Column', @level2name = 'note';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '申請案號 (PK)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'LOAN_APPLICATION',
     @level2type = N'Column', @level2name = 'application_id';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '顧客代號 (FK)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'LOAN_APPLICATION',
     @level2type = N'Column', @level2name = 'customer_id';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '申請金額',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'LOAN_APPLICATION',
     @level2type = N'Column', @level2name = 'apply_amount';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '申請期數(月)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'LOAN_APPLICATION',
     @level2type = N'Column', @level2name = 'apply_period';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '核准金額',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'LOAN_APPLICATION',
     @level2type = N'Column', @level2name = 'approved_amount';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '核准利率',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'LOAN_APPLICATION',
     @level2type = N'Column', @level2name = 'approved_rate';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '核准期數',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'LOAN_APPLICATION',
     @level2type = N'Column', @level2name = 'approved_period';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '狀態：PENDING / APPROVED / REJECTED',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'LOAN_APPLICATION',
     @level2type = N'Column', @level2name = 'status';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '審核行員代號 (FK)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'LOAN_APPLICATION',
     @level2type = N'Column', @level2name = 'reviewer_id';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '審核時間',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'LOAN_APPLICATION',
     @level2type = N'Column', @level2name = 'review_time';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '還款流水號 (PK)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'REPAYMENT_RECORD',
     @level2type = N'Column', @level2name = 'repayment_id';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '對應貸款案號 (FK)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'REPAYMENT_RECORD',
     @level2type = N'Column', @level2name = 'application_id';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '扣款帳號 (FK)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'REPAYMENT_RECORD',
     @level2type = N'Column', @level2name = 'account_number';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '期數 (第幾期)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'REPAYMENT_RECORD',
     @level2type = N'Column', @level2name = 'period';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '已還本金',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'REPAYMENT_RECORD',
     @level2type = N'Column', @level2name = 'principal_paid';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '已繳利息',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'REPAYMENT_RECORD',
     @level2type = N'Column', @level2name = 'interest_paid';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '已繳違約金',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'REPAYMENT_RECORD',
     @level2type = N'Column', @level2name = 'penalty_paid';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '實際還款時間',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'REPAYMENT_RECORD',
     @level2type = N'Column', @level2name = 'payment_date';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '卡別ID(主鍵)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CARD_TYPE',
     @level2type = N'Column', @level2name = 'card_type_id';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '卡別名稱 (例如: 現金回饋卡、旅遊卡)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CARD_TYPE',
     @level2type = N'Column', @level2name = 'card_type_name';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '卡片品牌 (VISA / Master / JCB)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CARD_TYPE',
     @level2type = N'Column', @level2name = 'brand';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '年費',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CARD_TYPE',
     @level2type = N'Column', @level2name = 'annual_fee';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '回饋比例 (%)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CARD_TYPE',
     @level2type = N'Column', @level2name = 'cashback_rate';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '商家ID(主鍵)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'MERCHANT',
     @level2type = N'Column', @level2name = 'merchant_id';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '商家名稱',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'MERCHANT',
     @level2type = N'Column', @level2name = 'merchant_name';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '商家類型 (餐飲 / 交通 / 購物)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'MERCHANT',
     @level2type = N'Column', @level2name = 'merchant_category';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '信用卡ID(主鍵)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CREDIT_CARD',
     @level2type = N'Column', @level2name = 'card_id';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '客戶ID (對應 CUSTOMER)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CREDIT_CARD',
     @level2type = N'Column', @level2name = 'customer_id';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '卡別ID (對應 CARD_TYPE)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CREDIT_CARD',
     @level2type = N'Column', @level2name = 'card_type_id';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '信用卡卡號',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CREDIT_CARD',
     @level2type = N'Column', @level2name = 'card_number';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '卡片到期日',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CREDIT_CARD',
     @level2type = N'Column', @level2name = 'expiry_date';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '信用額度',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CREDIT_CARD',
     @level2type = N'Column', @level2name = 'credit_limit';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '目前已使用金額',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CREDIT_CARD',
     @level2type = N'Column', @level2name = 'current_balance';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '開卡日期',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CREDIT_CARD',
     @level2type = N'Column', @level2name = 'create_date';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '卡片狀態 (1=正常 0=停卡)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CREDIT_CARD',
     @level2type = N'Column', @level2name = 'status';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '交易ID(主鍵)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CREDIT_CARD_TRANSACTION',
     @level2type = N'Column', @level2name = 'txn_id';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '信用卡ID',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CREDIT_CARD_TRANSACTION',
     @level2type = N'Column', @level2name = 'card_id';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '商家ID (對應 MERCHANT)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CREDIT_CARD_TRANSACTION',
     @level2type = N'Column', @level2name = 'merchant_id';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '交易金額',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CREDIT_CARD_TRANSACTION',
     @level2type = N'Column', @level2name = 'txn_amount';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '交易類型 (PURCHASE / REFUND / PAYMENT)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CREDIT_CARD_TRANSACTION',
     @level2type = N'Column', @level2name = 'txn_type';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '交易時間',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CREDIT_CARD_TRANSACTION',
     @level2type = N'Column', @level2name = 'txn_date';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '備註',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CREDIT_CARD_TRANSACTION',
     @level2type = N'Column', @level2name = 'description';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '帳單ID(主鍵)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CREDIT_CARD_BILL',
     @level2type = N'Column', @level2name = 'bill_id';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '信用卡ID',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CREDIT_CARD_BILL',
     @level2type = N'Column', @level2name = 'card_id';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '帳單月份 (例如: 2026-03)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CREDIT_CARD_BILL',
     @level2type = N'Column', @level2name = 'billing_month';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '帳單產生日期',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CREDIT_CARD_BILL',
     @level2type = N'Column', @level2name = 'bill_date';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '繳款截止日',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CREDIT_CARD_BILL',
     @level2type = N'Column', @level2name = 'due_date';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '帳單總金額',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CREDIT_CARD_BILL',
     @level2type = N'Column', @level2name = 'total_amount';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '最低應繳金額',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CREDIT_CARD_BILL',
     @level2type = N'Column', @level2name = 'minimum_payment';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '已繳金額',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CREDIT_CARD_BILL',
     @level2type = N'Column', @level2name = 'paid_amount';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '帳單狀態 (UNPAID / PARTIAL / PAID)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CREDIT_CARD_BILL',
     @level2type = N'Column', @level2name = 'bill_status';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '股票代碼 (PK)，如 2330',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'STOCK_MASTER',
     @level2type = N'Column', @level2name = 'stock_symbol';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '公司名稱',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'STOCK_MASTER',
     @level2type = N'Column', @level2name = 'company_name';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '上市或上櫃',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'STOCK_MASTER',
     @level2type = N'Column', @level2name = 'market_type';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '最新股價/收盤價',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'STOCK_MASTER',
     @level2type = N'Column', @level2name = 'current_price';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '開盤價',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'STOCK_MASTER',
     @level2type = N'Column', @level2name = 'opening_price';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '最高價',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'STOCK_MASTER',
     @level2type = N'Column', @level2name = 'highest_price';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '最低價',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'STOCK_MASTER',
     @level2type = N'Column', @level2name = 'lowest_price';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '成交股數',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'STOCK_MASTER',
     @level2type = N'Column', @level2name = 'trade_volume';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '手續費率',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'STOCK_MASTER',
     @level2type = N'Column', @level2name = 'fee_rate';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '狀態：ACTIVE',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'STOCK_MASTER',
     @level2type = N'Column', @level2name = 'status';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '報價更新時間',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'STOCK_MASTER',
     @level2type = N'Column', @level2name = 'updated_at';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '庫存流水號 (PK)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CUSTOMER_STOCK_HOLDING',
     @level2type = N'Column', @level2name = 'holding_id';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '顧客代號 (FK)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CUSTOMER_STOCK_HOLDING',
     @level2type = N'Column', @level2name = 'customer_id';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '扣款/入帳的活存帳號 (FK)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CUSTOMER_STOCK_HOLDING',
     @level2type = N'Column', @level2name = 'account_number';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '股票代碼 (FK)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CUSTOMER_STOCK_HOLDING',
     @level2type = N'Column', @level2name = 'stock_symbol';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '總持有股數',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CUSTOMER_STOCK_HOLDING',
     @level2type = N'Column', @level2name = 'total_shares';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '平均成本價 (用來算損益)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'CUSTOMER_STOCK_HOLDING',
     @level2type = N'Column', @level2name = 'average_cost';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '交易流水號 (PK)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'STOCK_TRANSACTION_RECORD',
     @level2type = N'Column', @level2name = 'transaction_id';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '顧客代號 (FK)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'STOCK_TRANSACTION_RECORD',
     @level2type = N'Column', @level2name = 'customer_id';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '活存帳號 (FK)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'STOCK_TRANSACTION_RECORD',
     @level2type = N'Column', @level2name = 'account_number';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '股票代碼 (FK)',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'STOCK_TRANSACTION_RECORD',
     @level2type = N'Column', @level2name = 'stock_symbol';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = 'BUY 或 SELL',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'STOCK_TRANSACTION_RECORD',
     @level2type = N'Column', @level2name = 'transaction_type';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '實際成交價',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'STOCK_TRANSACTION_RECORD',
     @level2type = N'Column', @level2name = 'deal_price';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '交易股數',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'STOCK_TRANSACTION_RECORD',
     @level2type = N'Column', @level2name = 'shares';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '手續費與證交稅總額',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'STOCK_TRANSACTION_RECORD',
     @level2type = N'Column', @level2name = 'fee_amount';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '狀態：COMPLETED',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'STOCK_TRANSACTION_RECORD',
     @level2type = N'Column', @level2name = 'transaction_status';
GO

EXEC sp_addextendedproperty
     @name = N'Column_Description',
     @value = '成交時間',
     @level0type = N'Schema', @level0name = 'dbo',
     @level1type = N'Table',  @level1name = 'STOCK_TRANSACTION_RECORD',
     @level2type = N'Column', @level2name = 'transaction_time';
GO

ALTER TABLE [ADMIN_LOG] ADD FOREIGN KEY ([admin_id]) REFERENCES [ADMINISTRATOR] ([admin_id])
GO

ALTER TABLE [KYC_RECORD] ADD FOREIGN KEY ([verified_by_admin]) REFERENCES [ADMINISTRATOR] ([admin_id])
GO

ALTER TABLE [LOAN_APPLICATION] ADD FOREIGN KEY ([reviewer_id]) REFERENCES [ADMINISTRATOR] ([admin_id])
GO

ALTER TABLE [KYC_RECORD] ADD FOREIGN KEY ([customer_id]) REFERENCES [CUSTOMER] ([customer_id])
GO

ALTER TABLE [ACCOUNT] ADD FOREIGN KEY ([customer_id]) REFERENCES [CUSTOMER] ([customer_id])
GO

ALTER TABLE [LOAN_APPLICATION] ADD FOREIGN KEY ([customer_id]) REFERENCES [CUSTOMER] ([customer_id])
GO

ALTER TABLE [CREDIT_CARD] ADD FOREIGN KEY ([customer_id]) REFERENCES [CUSTOMER] ([customer_id])
GO

ALTER TABLE [CUSTOMER_STOCK_HOLDING] ADD FOREIGN KEY ([customer_id]) REFERENCES [CUSTOMER] ([customer_id])
GO

ALTER TABLE [STOCK_TRANSACTION_RECORD] ADD FOREIGN KEY ([customer_id]) REFERENCES [CUSTOMER] ([customer_id])
GO

ALTER TABLE [TRANS_LOG] ADD FOREIGN KEY ([operation_account]) REFERENCES [ACCOUNT] ([account_number])
GO

ALTER TABLE [REPAYMENT_RECORD] ADD FOREIGN KEY ([account_number]) REFERENCES [ACCOUNT] ([account_number])
GO

ALTER TABLE [CUSTOMER_STOCK_HOLDING] ADD FOREIGN KEY ([account_number]) REFERENCES [ACCOUNT] ([account_number])
GO

ALTER TABLE [STOCK_TRANSACTION_RECORD] ADD FOREIGN KEY ([account_number]) REFERENCES [ACCOUNT] ([account_number])
GO

ALTER TABLE [REPAYMENT_RECORD] ADD FOREIGN KEY ([application_id]) REFERENCES [LOAN_APPLICATION] ([application_id])
GO

ALTER TABLE [CREDIT_CARD] ADD FOREIGN KEY ([card_type_id]) REFERENCES [CARD_TYPE] ([card_type_id])
GO

ALTER TABLE [CREDIT_CARD_BILL] ADD FOREIGN KEY ([card_id]) REFERENCES [CREDIT_CARD] ([card_id])
GO

ALTER TABLE [CREDIT_CARD_TRANSACTION] ADD FOREIGN KEY ([card_id]) REFERENCES [CREDIT_CARD] ([card_id])
GO

ALTER TABLE [CREDIT_CARD_TRANSACTION] ADD FOREIGN KEY ([merchant_id]) REFERENCES [MERCHANT] ([merchant_id])
GO

ALTER TABLE [CUSTOMER_STOCK_HOLDING] ADD FOREIGN KEY ([stock_symbol]) REFERENCES [STOCK_MASTER] ([stock_symbol])
GO

ALTER TABLE [STOCK_TRANSACTION_RECORD] ADD FOREIGN KEY ([stock_symbol]) REFERENCES [STOCK_MASTER] ([stock_symbol])
GO
