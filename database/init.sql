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
                           [status] VARCHAR(20),                   -- 狀態：ACTIVE / FROZEN 等
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
                             [operation_account] VARCHAR(12),        -- 修正點：對齊主鍵 12 碼 (FK)
                             [other_account] VARCHAR(12),            -- 修正點：對手帳號亦對齊 12 碼
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
                                    [account_number] VARCHAR(12),           -- 修正點：對齊主鍵 12 碼 (FK)
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
                             [card_type_name] NVARCHAR(50) NOT NULL,       -- 卡別名稱
                             [brand] NVARCHAR(20) NOT NULL,                -- 卡片品牌
                             [annual_fee] DECIMAL(10,2),                   -- 年費
                             [cashback_rate] DECIMAL(5,2),                 -- 回饋比例 (%)
                             [card_image_url] VARCHAR(255)                 -- 卡片圖片URL
)
GO

-- 商家資料表
CREATE TABLE [MERCHANT] (
                            [merchant_id] INT IDENTITY(1,1) PRIMARY KEY,  -- 商家ID (PK)
                            [merchant_name] NVARCHAR(100) NOT NULL,       -- 商家名稱
                            [merchant_category] NVARCHAR(50)              -- 商家類型
)
GO

-- 信用卡申請主表
CREATE TABLE [CARD_APPLICATION] (
                                    [application_id] INT IDENTITY(1,1) PRIMARY KEY,
                                    [customer_id] INT NOT NULL,
                                    [apply_date] DATETIME DEFAULT GETDATE(),
                                    [status] NVARCHAR(20) DEFAULT 'PENDING',
                                    [remark] NVARCHAR(200),
                                    FOREIGN KEY (customer_id) REFERENCES [CUSTOMER](customer_id)
)
GO

-- 信用卡申請明細
CREATE TABLE [CARD_APPLICATION_ITEM] (
                                         [item_id] INT IDENTITY(1,1) PRIMARY KEY,
                                         [application_id] INT NOT NULL,
                                         [card_type_id] INT NOT NULL,
                                         [result] NVARCHAR(20) DEFAULT 'PENDING',
                                         [approved_limit] DECIMAL(15,2),
                                         [annual_fee] DECIMAL(10,2),
                                         [create_card_flag] BIT DEFAULT 0,
                                         [remark] NVARCHAR(200),
                                         FOREIGN KEY (application_id) REFERENCES CARD_APPLICATION(application_id),
                                         FOREIGN KEY (card_type_id) REFERENCES CARD_TYPE(card_type_id)
)
GO

-- 信用卡主表
CREATE TABLE [CREDIT_CARD] (
                               [card_id] INT IDENTITY(1,1) PRIMARY KEY,
                               [customer_id] INT NOT NULL,
                               [card_type_id] INT NOT NULL,
                               [application_item_id] INT NULL,
                               [card_number] VARCHAR(16) UNIQUE NOT NULL,
                               [expiry_date] DATE NOT NULL,
                               [credit_limit] DECIMAL(15,2),
                               [current_balance] DECIMAL(15,2) DEFAULT 0,
                               [create_date] DATETIME DEFAULT GETDATE(),
                               [status] NVARCHAR(20) CHECK (status IN ('ACTIVE','BLOCKED')),
                               FOREIGN KEY (card_type_id) REFERENCES [CARD_TYPE](card_type_id)
)
GO

-- 信用卡交易紀錄表
CREATE TABLE [CREDIT_CARD_TRANSACTION] (
                                           [txn_id] INT IDENTITY(1,1) PRIMARY KEY,
                                           [card_id] INT NOT NULL,
                                           [merchant_id] INT NOT NULL,
                                           [ref_txn_id] INT NULL,
                                           [txn_amount] DECIMAL(15,2) NOT NULL,
                                           [txn_type] NVARCHAR(20),
                                           [txn_date] DATETIME DEFAULT GETDATE(),
                                           [description] NVARCHAR(200),
                                           FOREIGN KEY (card_id) REFERENCES [CREDIT_CARD](card_id),
                                           FOREIGN KEY (merchant_id) REFERENCES [MERCHANT](merchant_id)
)
GO

-- 信用卡帳單表
CREATE TABLE [CREDIT_CARD_BILL] (
                                    [bill_id] INT IDENTITY(1,1) PRIMARY KEY,
                                    [card_id] INT NOT NULL,
                                    [billing_month] VARCHAR(7),
                                    [bill_date] DATE,
                                    [due_date] DATE,
                                    [total_amount] DECIMAL(15,2),
                                    [minimum_payment] DECIMAL(15,2),
                                    [paid_amount] DECIMAL(15,2),
                                    [bill_status] NVARCHAR(20),
                                    FOREIGN KEY (card_id) REFERENCES [CREDIT_CARD](card_id)
)
GO

-- 股票主檔資料表
CREATE TABLE [STOCK_MASTER] (
                                [stock_symbol] VARCHAR(10) PRIMARY KEY,
                                [company_name] VARCHAR(100),
                                [market_type] VARCHAR(20),
                                [current_price] DECIMAL(10,2),
                                [opening_price] DECIMAL(10,2),
                                [highest_price] DECIMAL(10,2),
                                [lowest_price] DECIMAL(10,2),
                                [trade_volume] BIGINT,
                                [fee_rate] DECIMAL(5,4),
                                [status] VARCHAR(20),
                                [created_at] DATETIME2,
                                [updated_at] DATETIME2
)
GO

-- 顧客股票庫存表
CREATE TABLE [CUSTOMER_STOCK_HOLDING] (
                                          [holding_id] BIGINT PRIMARY KEY,
                                          [customer_id] INT,
                                          [account_number] VARCHAR(12), -- 修正點：對齊主鍵 12 碼 (FK)
                                          [stock_symbol] VARCHAR(10),
                                          [total_shares] INT,
                                          [average_cost] DECIMAL(10,2),
                                          [updated_at] DATETIME2
)
GO

-- 股票交易明細表
CREATE TABLE [STOCK_TRANSACTION_RECORD] (
                                            [transaction_id] BIGINT PRIMARY KEY,
                                            [customer_id] INT,
                                            [account_number] VARCHAR(12), -- 修正點：對齊主鍵 12 碼 (FK)
                                            [stock_symbol] VARCHAR(10),
                                            [transaction_type] VARCHAR(10),
                                            [deal_price] DECIMAL(10,2),
                                            [shares] INT,
                                            [fee_amount] DECIMAL(10,2),
                                            [transaction_status] VARCHAR(20),
                                            [transaction_time] DATETIME2
)
GO

-- 股票基本資訊表
CREATE TABLE [stock_info] (
                              [stock_id] INT PRIMARY KEY,
                              [stock_name] NVARCHAR(MAX) NULL,
                              [status] BIT NULL
)
GO

-----------------------------------------------------------
-- 以下為 Extended Properties 補完區 (保證完整補回)
-----------------------------------------------------------

EXEC sp_addextendedproperty @name = N'Column_Description', @value = '行員編號 (PK)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'ADMINISTRATOR', @level2type = N'Column', @level2name = 'admin_id';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '登入帳號', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'ADMINISTRATOR', @level2type = N'Column', @level2name = 'username';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '登入密碼(Hash)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'ADMINISTRATOR', @level2type = N'Column', @level2name = 'password_hash';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '行員姓名', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'ADMINISTRATOR', @level2type = N'Column', @level2name = 'name';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '角色：MANAGER / CLERK', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'ADMINISTRATOR', @level2type = N'Column', @level2name = 'role';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '狀態：ACTIVE / SUSPENDED', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'ADMINISTRATOR', @level2type = N'Column', @level2name = 'status';

EXEC sp_addextendedproperty @name = N'Column_Description', @value = '日誌流水號 (PK)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'ADMIN_LOG', @level2type = N'Column', @level2name = 'log_id';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '操作行員 (FK)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'ADMIN_LOG', @level2type = N'Column', @level2name = 'admin_id';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '動作類型 (如: LOGIN, UPDATE)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'ADMIN_LOG', @level2type = N'Column', @level2name = 'action_type';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '異動資料表', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'ADMIN_LOG', @level2type = N'Column', @level2name = 'target_table';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '異動資料ID', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'ADMIN_LOG', @level2type = N'Column', @level2name = 'target_record_id';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '詳細內容', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'ADMIN_LOG', @level2type = N'Column', @level2name = 'action_details';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '操作時間', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'ADMIN_LOG', @level2type = N'Column', @level2name = 'action_timestamp';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '操作IP', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'ADMIN_LOG', @level2type = N'Column', @level2name = 'ip_address';

EXEC sp_addextendedproperty @name = N'Column_Description', @value = '顧客系統代號 (PK)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CUSTOMER', @level2type = N'Column', @level2name = 'customer_id';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '身分證字號', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CUSTOMER', @level2type = N'Column', @level2name = 'id_number';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '顧客姓名', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CUSTOMER', @level2type = N'Column', @level2name = 'name';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '網銀登入密碼', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CUSTOMER', @level2type = N'Column', @level2name = 'password_hash';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '登入失敗次數', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CUSTOMER', @level2type = N'Column', @level2name = 'failed_login_attempts';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '年收入', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CUSTOMER', @level2type = N'Column', @level2name = 'income';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '信用評分', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CUSTOMER', @level2type = N'Column', @level2name = 'credit_score';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '狀態：PENDING / ACTIVE / FROZEN', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CUSTOMER', @level2type = N'Column', @level2name = 'status';

EXEC sp_addextendedproperty @name = N'Column_Description', @value = '審查案件編號 (PK)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'KYC_RECORD', @level2type = N'Column', @level2name = 'kyc_id';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '顧客代號 (FK)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'KYC_RECORD', @level2type = N'Column', @level2name = 'customer_id';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '證件類型 (如: ID_CARD)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'KYC_RECORD', @level2type = N'Column', @level2name = 'document_type';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '證件號碼', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'KYC_RECORD', @level2type = N'Column', @level2name = 'document_number';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '證件到期日', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'KYC_RECORD', @level2type = N'Column', @level2name = 'expiry_date';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '證件影像路徑', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'KYC_RECORD', @level2type = N'Column', @level2name = 'document_image_url';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '審查狀態：PENDING / APPROVED', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'KYC_RECORD', @level2type = N'Column', @level2name = 'verification_status';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '審查行員代號 (FK)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'KYC_RECORD', @level2type = N'Column', @level2name = 'verified_by_admin';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '送件時間', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'KYC_RECORD', @level2type = N'Column', @level2name = 'submission_date';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '審核時間', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'KYC_RECORD', @level2type = N'Column', @level2name = 'verification_date';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '審核備註/退件原因', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'KYC_RECORD', @level2type = N'Column', @level2name = 'comments';

EXEC sp_addextendedproperty @name = N'Column_Description', @value = '銀行帳號 (PK)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'ACCOUNT', @level2type = N'Column', @level2name = 'account_number';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '顧客代號 (FK)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'ACCOUNT', @level2type = N'Column', @level2name = 'customer_id';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '帳戶類型 (活存/定存)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'ACCOUNT', @level2type = N'Column', @level2name = 'type';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '幣別 (如: TWD)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'ACCOUNT', @level2type = N'Column', @level2name = 'currency';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '帳戶餘額', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'ACCOUNT', @level2type = N'Column', @level2name = 'balance';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '狀態：ACTIVE / FROZEN', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'ACCOUNT', @level2type = N'Column', @level2name = 'status';

EXEC sp_addextendedproperty @name = N'Column_Description', @value = '交易流水號 (PK)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'TRANS_LOG', @level2type = N'Column', @level2name = 'trans_log_id';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '外部參考編號', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'TRANS_LOG', @level2type = N'Column', @level2name = 'reference_id';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '交易金額', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'TRANS_LOG', @level2type = N'Column', @level2name = 'amount';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '類型 (存款/提款/轉帳)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'TRANS_LOG', @level2type = N'Column', @level2name = 'type';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '操作帳號 (FK)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'TRANS_LOG', @level2type = N'Column', @level2name = 'operation_account';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '對手帳號', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'TRANS_LOG', @level2type = N'Column', @level2name = 'other_account';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '交易後餘額', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'TRANS_LOG', @level2type = N'Column', @level2name = 'balance';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '交易時間', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'TRANS_LOG', @level2type = N'Column', @level2name = 'transaction_time';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '備註/摘要', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'TRANS_LOG', @level2type = N'Column', @level2name = 'note';

EXEC sp_addextendedproperty @name = N'Column_Description', @value = '申請案號 (PK)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'LOAN_APPLICATION', @level2type = N'Column', @level2name = 'application_id';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '顧客代號 (FK)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'LOAN_APPLICATION', @level2type = N'Column', @level2name = 'customer_id';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '申請金額', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'LOAN_APPLICATION', @level2type = N'Column', @level2name = 'apply_amount';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '申請期數(月)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'LOAN_APPLICATION', @level2type = N'Column', @level2name = 'apply_period';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '核准金額', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'LOAN_APPLICATION', @level2type = N'Column', @level2name = 'approved_amount';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '核准利率', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'LOAN_APPLICATION', @level2type = N'Column', @level2name = 'approved_rate';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '核准期數', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'LOAN_APPLICATION', @level2type = N'Column', @level2name = 'approved_period';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '狀態：PENDING / APPROVED / REJECTED', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'LOAN_APPLICATION', @level2type = N'Column', @level2name = 'status';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '審核行員代號 (FK)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'LOAN_APPLICATION', @level2type = N'Column', @level2name = 'reviewer_id';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '審核時間', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'LOAN_APPLICATION', @level2type = N'Column', @level2name = 'review_time';

EXEC sp_addextendedproperty @name = N'Column_Description', @value = '還款流水號 (PK)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'REPAYMENT_RECORD', @level2type = N'Column', @level2name = 'repayment_id';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '對應貸款案號 (FK)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'REPAYMENT_RECORD', @level2type = N'Column', @level2name = 'application_id';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '扣款帳號 (FK)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'REPAYMENT_RECORD', @level2type = N'Column', @level2name = 'account_number';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '期數 (第幾期)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'REPAYMENT_RECORD', @level2type = N'Column', @level2name = 'period';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '已還本金', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'REPAYMENT_RECORD', @level2type = N'Column', @level2name = 'principal_paid';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '已繳利息', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'REPAYMENT_RECORD', @level2type = N'Column', @level2name = 'interest_paid';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '已繳違約金', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'REPAYMENT_RECORD', @level2type = N'Column', @level2name = 'penalty_paid';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '實際還款時間', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'REPAYMENT_RECORD', @level2type = N'Column', @level2name = 'payment_date';

EXEC sp_addextendedproperty @name = N'Column_Description', @value = '卡別ID(主鍵)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CARD_TYPE', @level2type = N'Column', @level2name = 'card_type_id';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '卡別名稱', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CARD_TYPE', @level2type = N'Column', @level2name = 'card_type_name';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '卡片品牌', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CARD_TYPE', @level2type = N'Column', @level2name = 'brand';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '年費', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CARD_TYPE', @level2type = N'Column', @level2name = 'annual_fee';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '回饋比例 (%)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CARD_TYPE', @level2type = N'Column', @level2name = 'cashback_rate';

EXEC sp_addextendedproperty @name = N'Column_Description', @value = '商家ID(主鍵)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'MERCHANT', @level2type = N'Column', @level2name = 'merchant_id';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '商家名稱', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'MERCHANT', @level2type = N'Column', @level2name = 'merchant_name';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '商家類型', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'MERCHANT', @level2type = N'Column', @level2name = 'merchant_category';

EXEC sp_addextendedproperty @name = N'Column_Description', @value = '信用卡ID(主鍵)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CREDIT_CARD', @level2type = N'Column', @level2name = 'card_id';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '客戶ID', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CREDIT_CARD', @level2type = N'Column', @level2name = 'customer_id';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '卡別ID', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CREDIT_CARD', @level2type = N'Column', @level2name = 'card_type_id';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '信用卡卡號', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CREDIT_CARD', @level2type = N'Column', @level2name = 'card_number';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '卡片到期日', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CREDIT_CARD', @level2type = N'Column', @level2name = 'expiry_date';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '信用額度', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CREDIT_CARD', @level2type = N'Column', @level2name = 'credit_limit';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '目前已使用金額', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CREDIT_CARD', @level2type = N'Column', @level2name = 'current_balance';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '開卡日期', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CREDIT_CARD', @level2type = N'Column', @level2name = 'create_date';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '卡片狀態 (1=正常 0=停卡)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CREDIT_CARD', @level2type = N'Column', @level2name = 'status';

EXEC sp_addextendedproperty @name = N'Column_Description', @value = '交易ID(主鍵)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CREDIT_CARD_TRANSACTION', @level2type = N'Column', @level2name = 'txn_id';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '信用卡ID', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CREDIT_CARD_TRANSACTION', @level2type = N'Column', @level2name = 'card_id';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '商家ID', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CREDIT_CARD_TRANSACTION', @level2type = N'Column', @level2name = 'merchant_id';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '交易金額', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CREDIT_CARD_TRANSACTION', @level2type = N'Column', @level2name = 'txn_amount';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '交易類型', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CREDIT_CARD_TRANSACTION', @level2type = N'Column', @level2name = 'txn_type';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '交易時間', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CREDIT_CARD_TRANSACTION', @level2type = N'Column', @level2name = 'txn_date';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '備註', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CREDIT_CARD_TRANSACTION', @level2type = N'Column', @level2name = 'description';

EXEC sp_addextendedproperty @name = N'Column_Description', @value = '帳單ID(主鍵)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CREDIT_CARD_BILL', @level2type = N'Column', @level2name = 'bill_id';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '信用卡ID', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CREDIT_CARD_BILL', @level2type = N'Column', @level2name = 'card_id';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '帳單月份 (例如: 2026-03)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CREDIT_CARD_BILL', @level2type = N'Column', @level2name = 'billing_month';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '帳單產生日期', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CREDIT_CARD_BILL', @level2type = N'Column', @level2name = 'bill_date';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '繳款截止日', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CREDIT_CARD_BILL', @level2type = N'Column', @level2name = 'due_date';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '帳單總金額', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CREDIT_CARD_BILL', @level2type = N'Column', @level2name = 'total_amount';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '最低應繳金額', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CREDIT_CARD_BILL', @level2type = N'Column', @level2name = 'minimum_payment';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '已繳金額', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CREDIT_CARD_BILL', @level2type = N'Column', @level2name = 'paid_amount';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '帳單狀態', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CREDIT_CARD_BILL', @level2type = N'Column', @level2name = 'bill_status';

EXEC sp_addextendedproperty @name = N'Column_Description', @value = '股票代碼 (PK)，如 2330', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'STOCK_MASTER', @level2type = N'Column', @level2name = 'stock_symbol';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '公司名稱', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'STOCK_MASTER', @level2type = N'Column', @level2name = 'company_name';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '上市或上櫃', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'STOCK_MASTER', @level2type = N'Column', @level2name = 'market_type';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '最新股價/收盤價', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'STOCK_MASTER', @level2type = N'Column', @level2name = 'current_price';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '開盤價', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'STOCK_MASTER', @level2type = N'Column', @level2name = 'opening_price';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '最高價', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'STOCK_MASTER', @level2type = N'Column', @level2name = 'highest_price';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '最低價', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'STOCK_MASTER', @level2type = N'Column', @level2name = 'lowest_price';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '成交股數', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'STOCK_MASTER', @level2type = N'Column', @level2name = 'trade_volume';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '手續費率', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'STOCK_MASTER', @level2type = N'Column', @level2name = 'fee_rate';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '狀態：ACTIVE', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'STOCK_MASTER', @level2type = N'Column', @level2name = 'status';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '報價更新時間', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'STOCK_MASTER', @level2type = N'Column', @level2name = 'updated_at';

EXEC sp_addextendedproperty @name = N'Column_Description', @value = '庫存流水號 (PK)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CUSTOMER_STOCK_HOLDING', @level2type = N'Column', @level2name = 'holding_id';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '顧客代號 (FK)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CUSTOMER_STOCK_HOLDING', @level2type = N'Column', @level2name = 'customer_id';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '扣款/入帳的活存帳號 (FK)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CUSTOMER_STOCK_HOLDING', @level2type = N'Column', @level2name = 'account_number';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '股票代碼 (FK)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CUSTOMER_STOCK_HOLDING', @level2type = N'Column', @level2name = 'stock_symbol';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '總持有股數', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CUSTOMER_STOCK_HOLDING', @level2type = N'Column', @level2name = 'total_shares';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '平均成本價', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'CUSTOMER_STOCK_HOLDING', @level2type = N'Column', @level2name = 'average_cost';

EXEC sp_addextendedproperty @name = N'Column_Description', @value = '交易流水號 (PK)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'STOCK_TRANSACTION_RECORD', @level2type = N'Column', @level2name = 'transaction_id';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '顧客代號 (FK)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'STOCK_TRANSACTION_RECORD', @level2type = N'Column', @level2name = 'customer_id';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '活存帳號 (FK)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'STOCK_TRANSACTION_RECORD', @level2type = N'Column', @level2name = 'account_number';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '股票代碼 (FK)', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'STOCK_TRANSACTION_RECORD', @level2type = N'Column', @level2name = 'stock_symbol';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = 'BUY 或 SELL', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'STOCK_TRANSACTION_RECORD', @level2type = N'Column', @level2name = 'transaction_type';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '實際成交價', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'STOCK_TRANSACTION_RECORD', @level2type = N'Column', @level2name = 'deal_price';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '交易股數', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'STOCK_TRANSACTION_RECORD', @level2type = N'Column', @level2name = 'shares';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '手續費與證交稅總額', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'STOCK_TRANSACTION_RECORD', @level2type = N'Column', @level2name = 'fee_amount';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '狀態：COMPLETED', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'STOCK_TRANSACTION_RECORD', @level2type = N'Column', @level2name = 'transaction_status';
EXEC sp_addextendedproperty @name = N'Column_Description', @value = '成交時間', @level0type = N'Schema', @level0name = 'dbo', @level1type = N'Table', @level1name = 'STOCK_TRANSACTION_RECORD', @level2type = N'Column', @level2name = 'transaction_time';
GO

-- 外鍵連結
ALTER TABLE [ADMIN_LOG] ADD FOREIGN KEY ([admin_id]) REFERENCES [ADMINISTRATOR] ([admin_id])
ALTER TABLE [KYC_RECORD] ADD FOREIGN KEY ([verified_by_admin]) REFERENCES [ADMINISTRATOR] ([admin_id])
ALTER TABLE [LOAN_APPLICATION] ADD FOREIGN KEY ([reviewer_id]) REFERENCES [ADMINISTRATOR] ([admin_id])
ALTER TABLE [KYC_RECORD] ADD FOREIGN KEY ([customer_id]) REFERENCES [CUSTOMER] ([customer_id])
ALTER TABLE [ACCOUNT] ADD FOREIGN KEY ([customer_id]) REFERENCES [CUSTOMER] ([customer_id])
ALTER TABLE [LOAN_APPLICATION] ADD FOREIGN KEY ([customer_id]) REFERENCES [CUSTOMER] ([customer_id])
ALTER TABLE [CREDIT_CARD] ADD FOREIGN KEY ([customer_id]) REFERENCES [CUSTOMER] ([customer_id])
ALTER TABLE [CUSTOMER_STOCK_HOLDING] ADD FOREIGN KEY ([customer_id]) REFERENCES [CUSTOMER] ([customer_id])
ALTER TABLE [STOCK_TRANSACTION_RECORD] ADD FOREIGN KEY ([customer_id]) REFERENCES [CUSTOMER] ([customer_id])

-- 核心修正：operation_account 必須為 VARCHAR(12) 才能 REFERENCES ACCOUNT(account_number)
ALTER TABLE [TRANS_LOG] ADD FOREIGN KEY ([operation_account]) REFERENCES [ACCOUNT] ([account_number])
ALTER TABLE [REPAYMENT_RECORD] ADD FOREIGN KEY ([account_number]) REFERENCES [ACCOUNT] ([account_number])
ALTER TABLE [CUSTOMER_STOCK_HOLDING] ADD FOREIGN KEY ([account_number]) REFERENCES [ACCOUNT] ([account_number])
ALTER TABLE [STOCK_TRANSACTION_RECORD] ADD FOREIGN KEY ([account_number]) REFERENCES [ACCOUNT] ([account_number])

ALTER TABLE [REPAYMENT_RECORD] ADD FOREIGN KEY ([application_id]) REFERENCES [LOAN_APPLICATION] ([application_id])
ALTER TABLE [CREDIT_CARD] ADD FOREIGN KEY ([card_type_id]) REFERENCES [CARD_TYPE] ([card_type_id])
ALTER TABLE [CREDIT_CARD_BILL] ADD FOREIGN KEY ([card_id]) REFERENCES [CREDIT_CARD] ([card_id])
ALTER TABLE [CREDIT_CARD_TRANSACTION] ADD FOREIGN KEY ([card_id]) REFERENCES [CREDIT_CARD] ([card_id])
ALTER TABLE [CREDIT_CARD_TRANSACTION] ADD FOREIGN KEY ([merchant_id]) REFERENCES [MERCHANT] ([merchant_id])
ALTER TABLE [CUSTOMER_STOCK_HOLDING] ADD FOREIGN KEY ([stock_symbol]) REFERENCES [STOCK_MASTER] ([stock_symbol])
ALTER TABLE [STOCK_TRANSACTION_RECORD] ADD FOREIGN KEY ([stock_symbol]) REFERENCES [STOCK_MASTER] ([stock_symbol])
GO
