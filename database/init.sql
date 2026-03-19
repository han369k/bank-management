CREATE TABLE [ADMINISTRATOR] (
    [admin_id] int PRIMARY KEY IDENTITY(1, 1),
    [username] nvarchar(255),
    [password_hash] nvarchar(255),
    [name] nvarchar(255),
    [email] nvarchar(255),
    [phone_number] nvarchar(255),
    [role] nvarchar(255),
    [status] nvarchar(255),
    [created_at] datetime,
    [updated_at] datetime
    )
    GO

CREATE TABLE [ADMIN_LOG] (
    [log_id] int PRIMARY KEY IDENTITY(1, 1),
    [admin_id] int,
    [action_type] nvarchar(255),
    [target_table] nvarchar(255),
    [target_record_id] nvarchar(255),
    [action_details] text,
    [action_timestamp] datetime,
    [ip_address] nvarchar(255)
    )
    GO

CREATE TABLE [CUSTOMER] (
    [customer_id] nvarchar(255) PRIMARY KEY,
    [id_number] nvarchar(255),
    [name] nvarchar(255),
    [date_of_birth] date,
    [nationality] nvarchar(255),
    [address] nvarchar,
    [phone] nvarchar(255),
    [email] nvarchar(255),
    [income] decimal(15,2),
    [credit_score] int,
    [created_at] datetime,
    [updated_at] datetime,
    [status] nvarchar(255)
    )
    GO

CREATE TABLE [KYC_RECORD] (
    [kyc_id] int PRIMARY KEY IDENTITY(1, 1),
    [customer_id] nvarchar(255),
    [document_type] nvarchar(255),
    [document_number] nvarchar(255),
    [expiry_date] date,
    [document_image_url] nvarchar(255),
    [verification_status] nvarchar(255),
    [verified_by_admin] int,
    [submission_date] datetime,
    [verification_date] datetime,
    [comments] nvarchar
    )
    GO

CREATE TABLE [ACCOUNT] (
    [account_number] nvarchar(255) PRIMARY KEY,
    [customer_id] nvarchar(255),
    [type] nvarchar(255),
    [currency] nvarchar(255),
    [balance] decimal(15,2),
    [status] nvarchar(255),
    [create_at] datetime,
    [change_at] datetime
    )
    GO

CREATE TABLE [TRANS_LOG] (
    [trans_log_id] bigint PRIMARY KEY IDENTITY(1, 1),
    [reference_id] nvarchar(255),
    [amount] decimal(15,2),
    [type] nvarchar(255),
    [operation_account] nvarchar(255),
    [other_account] nvarchar(255),
    [balance] decimal(15,2),
    [transaction_time] datetime,
    [note] nvarchar
    )
    GO

CREATE TABLE [LOAN_APPLICATION] (
    [application_id] nvarchar(255) PRIMARY KEY,
    [customer_id] nvarchar(255),
    [apply_amount] decimal(15,2),
    [apply_period] int,
    [approved_amount] decimal(15,2),
    [approved_rate] decimal(5,2),
    [approved_period] int,
    [status] nvarchar(255),
    [reviewer_id] int,
    [review_time] datetime
    )
    GO

CREATE TABLE [REPAYMENT_RECORD] (
    [repayment_id] bigint PRIMARY KEY IDENTITY(1, 1),
    [application_id] nvarchar(255),
    [account_number] nvarchar(255),
    [period] int,
    [principal_paid] decimal(15,2),
    [interest_paid] decimal(15,2),
    [penalty_paid] decimal(15,2),
    [payment_date] datetime
    )
    GO

CREATE TABLE [CardTypes] (
    [card_type_id] int PRIMARY KEY IDENTITY(1, 1),
    [card_type_name] nvarchar(50) NOT NULL,
    [brand] nvarchar(20) NOT NULL,
    [annual_fee] decimal(10,2),
    [cashback_rate] decimal(5,2)
    )
    GO

CREATE TABLE [Merchants] (
    [merchant_id] int PRIMARY KEY IDENTITY(1, 1),
    [merchant_name] nvarchar(100) NOT NULL,
    [merchant_category] nvarchar(50)
    )
    GO

CREATE TABLE [CreditCards] (
    [card_id] int PRIMARY KEY IDENTITY(1, 1),
    [customer_id] int NOT NULL,
    [card_type_id] int NOT NULL,
    [card_number] varchar(16) UNIQUE NOT NULL,
    [expiry_date] date NOT NULL,
    [credit_limit] decimal(15,2),
    [current_balance] decimal(15,2) DEFAULT (0),
    [create_date] datetime,
    [status] int DEFAULT (1)
    )
    GO

CREATE TABLE [CreditCardTransactions] (
    [txn_id] int PRIMARY KEY IDENTITY(1, 1),
    [card_id] int NOT NULL,
    [merchant_id] int NOT NULL,
    [txn_amount] decimal(15,2) NOT NULL,
    [txn_type] nvarchar(20),
    [txn_date] datetime,
    [description] nvarchar(200)
    )
    GO

CREATE TABLE [CreditCardBills] (
    [bill_id] int PRIMARY KEY IDENTITY(1, 1),
    [card_id] int NOT NULL,
    [billing_month] varchar(7),
    [bill_date] date,
    [due_date] date,
    [total_amount] decimal(15,2),
    [minimum_payment] decimal(15,2),
    [paid_amount] decimal(15,2),
    [bill_status] nvarchar(20)
    )
    GO

CREATE TABLE [FUND_MASTER] (
    [fund_id] nvarchar(255) PRIMARY KEY,
    [issuer_name] nvarchar,
    [fund_name] nvarchar,
    [currency] nvarchar(255),
    [latest_nav] decimal(15,4),
    [risk_level] nvarchar(255),
    [fee_rate] decimal(5,2),
    [min_purchase_amount] decimal(15,2),
    [fund_status] nvarchar(255),
    [created_at] datetime,
    [updated_at] datetime
    )
    GO

CREATE TABLE [CUSTOMER_FUND_HOLDING] (
    [holding_id] bigint PRIMARY KEY IDENTITY(1, 1),
    [customer_id] nvarchar(255),
    [account_number] nvarchar(255),
    [fund_id] nvarchar(255),
    [total_units] decimal(15,4),
    [average_cost] decimal(15,4),
    [updated_at] datetime
    )
    GO

CREATE TABLE [FUND_TRANSACTION_RECORD] (
    [transaction_id] bigint PRIMARY KEY IDENTITY(1, 1),
    [customer_id] nvarchar(255),
    [account_number] nvarchar(255),
    [fund_id] nvarchar(255),
    [transaction_type] nvarchar(255),
    [transaction_amount] decimal(15,2),
    [transaction_units] decimal(15,4),
    [deal_nav] decimal(15,4),
    [fee_amount] decimal(15,2),
    [transaction_status] nvarchar(255),
    [created_at] datetime,
    [completed_at] datetime
    )
    GO

    EXEC sp_addextendedproperty
    @name = N'Table_Description',
    @value = '系統管理員/行員表',
    @level0type = N'Schema', @level0name = 'dbo',
    @level1type = N'Table',  @level1name = 'ADMINISTRATOR';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '行員編號 (主鍵/自動遞增)',
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
@value = '登入密碼 (雜湊值)',
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
@value = '電子郵件',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'ADMINISTRATOR',
@level2type = N'Column', @level2name = 'email';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '聯絡電話',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'ADMINISTRATOR',
@level2type = N'Column', @level2name = 'phone_number';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '角色權限 (如:一般行員/經理)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'ADMINISTRATOR',
@level2type = N'Column', @level2name = 'role';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '帳號狀態 (正常/停權/離職)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'ADMINISTRATOR',
@level2type = N'Column', @level2name = 'status';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '建立時間',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'ADMINISTRATOR',
@level2type = N'Column', @level2name = 'created_at';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '最後更新時間',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'ADMINISTRATOR',
@level2type = N'Column', @level2name = 'updated_at';
GO

EXEC sp_addextendedproperty
@name = N'Table_Description',
@value = '系統稽核與操作日誌',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'ADMIN_LOG';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '日誌流水號 (主鍵/自動遞增)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'ADMIN_LOG',
@level2type = N'Column', @level2name = 'log_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '操作的行員編號',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'ADMIN_LOG',
@level2type = N'Column', @level2name = 'admin_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '操作類型 (新增/修改/刪除)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'ADMIN_LOG',
@level2type = N'Column', @level2name = 'action_type';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '被異動的資料表名稱',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'ADMIN_LOG',
@level2type = N'Column', @level2name = 'target_table';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '被異動的資料主鍵ID',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'ADMIN_LOG',
@level2type = N'Column', @level2name = 'target_record_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '操作詳細內容',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'ADMIN_LOG',
@level2type = N'Column', @level2name = 'action_details';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '操作發生時間',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'ADMIN_LOG',
@level2type = N'Column', @level2name = 'action_timestamp';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '操作時的 IP 位址',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'ADMIN_LOG',
@level2type = N'Column', @level2name = 'ip_address';
GO

EXEC sp_addextendedproperty
@name = N'Table_Description',
@value = '顧客基本資料表 (CIF)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CUSTOMER';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '顧客代號 (主鍵/自行編碼)',
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
@value = '出生年月日',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CUSTOMER',
@level2type = N'Column', @level2name = 'date_of_birth';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '國籍',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CUSTOMER',
@level2type = N'Column', @level2name = 'nationality';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '通訊地址',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CUSTOMER',
@level2type = N'Column', @level2name = 'address';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '聯絡電話',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CUSTOMER',
@level2type = N'Column', @level2name = 'phone';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '電子郵件',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CUSTOMER',
@level2type = N'Column', @level2name = 'email';
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
@value = '內部信用評分',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CUSTOMER',
@level2type = N'Column', @level2name = 'credit_score';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '建檔時間',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CUSTOMER',
@level2type = N'Column', @level2name = 'created_at';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '最後修改時間',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CUSTOMER',
@level2type = N'Column', @level2name = 'updated_at';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '顧客狀態 (正常/凍結/待審核)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CUSTOMER',
@level2type = N'Column', @level2name = 'status';
GO

EXEC sp_addextendedproperty
@name = N'Table_Description',
@value = '顧客 KYC 審查紀錄',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'KYC_RECORD';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = 'KYC紀錄流水號 (主鍵/自動遞增)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'KYC_RECORD',
@level2type = N'Column', @level2name = 'kyc_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '關聯顧客代號',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'KYC_RECORD',
@level2type = N'Column', @level2name = 'customer_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '身分驗證文件類型',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'KYC_RECORD',
@level2type = N'Column', @level2name = 'document_type';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '身分驗證文件號碼',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'KYC_RECORD',
@level2type = N'Column', @level2name = 'document_number';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '文件到期日',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'KYC_RECORD',
@level2type = N'Column', @level2name = 'expiry_date';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '證件影像檔案路徑',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'KYC_RECORD',
@level2type = N'Column', @level2name = 'document_image_url';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '審核狀態 (待審核/通過/退件)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'KYC_RECORD',
@level2type = N'Column', @level2name = 'verification_status';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '負責審核的行員編號',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'KYC_RECORD',
@level2type = N'Column', @level2name = 'verified_by_admin';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '資料提交時間',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'KYC_RECORD',
@level2type = N'Column', @level2name = 'submission_date';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '主管審核時間',
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
@name = N'Table_Description',
@value = '活存/外幣帳戶表',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'ACCOUNT';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '銀行帳號 (主鍵/自行編碼)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'ACCOUNT',
@level2type = N'Column', @level2name = 'account_number';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '所屬顧客代號',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'ACCOUNT',
@level2type = N'Column', @level2name = 'customer_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '帳戶類型 (活期/定期)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'ACCOUNT',
@level2type = N'Column', @level2name = 'type';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '幣別 (如: TWD, USD)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'ACCOUNT',
@level2type = N'Column', @level2name = 'currency';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '帳戶目前餘額',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'ACCOUNT',
@level2type = N'Column', @level2name = 'balance';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '帳戶狀態 (正常/警示/凍結)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'ACCOUNT',
@level2type = N'Column', @level2name = 'status';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '開戶時間',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'ACCOUNT',
@level2type = N'Column', @level2name = 'create_at';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '狀態最後變更時間',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'ACCOUNT',
@level2type = N'Column', @level2name = 'change_at';
GO

EXEC sp_addextendedproperty
@name = N'Table_Description',
@value = '帳戶資金進出明細',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'TRANS_LOG';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '交易流水號 (主鍵/自動遞增)',
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
@value = '交易類型 (存款/提款/轉帳/扣款)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'TRANS_LOG',
@level2type = N'Column', @level2name = 'type';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '發生交易的本行帳號',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'TRANS_LOG',
@level2type = N'Column', @level2name = 'operation_account';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '對方帳號 (轉帳時)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'TRANS_LOG',
@level2type = N'Column', @level2name = 'other_account';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '交易後的帳戶餘額',
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
@value = '交易備註/摘要',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'TRANS_LOG',
@level2type = N'Column', @level2name = 'note';
GO

EXEC sp_addextendedproperty
@name = N'Table_Description',
@value = '貸款申請與合約表',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'LOAN_APPLICATION';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '貸款案號 (主鍵/自行編碼)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'LOAN_APPLICATION',
@level2type = N'Column', @level2name = 'application_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '申請顧客代號',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'LOAN_APPLICATION',
@level2type = N'Column', @level2name = 'customer_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '客戶申請金額',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'LOAN_APPLICATION',
@level2type = N'Column', @level2name = 'apply_amount';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '客戶申請期數(月)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'LOAN_APPLICATION',
@level2type = N'Column', @level2name = 'apply_period';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '銀行核准金額',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'LOAN_APPLICATION',
@level2type = N'Column', @level2name = 'approved_amount';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '銀行核准利率(%)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'LOAN_APPLICATION',
@level2type = N'Column', @level2name = 'approved_rate';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '銀行核准期數(月)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'LOAN_APPLICATION',
@level2type = N'Column', @level2name = 'approved_period';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '案件狀態 (審核中/已撥款/結案)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'LOAN_APPLICATION',
@level2type = N'Column', @level2name = 'status';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '負責審查的行員編號',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'LOAN_APPLICATION',
@level2type = N'Column', @level2name = 'reviewer_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '審查完成時間',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'LOAN_APPLICATION',
@level2type = N'Column', @level2name = 'review_time';
GO

EXEC sp_addextendedproperty
@name = N'Table_Description',
@value = '貸款每期還款紀錄',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'REPAYMENT_RECORD';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '還款流水號 (主鍵/自動遞增)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'REPAYMENT_RECORD',
@level2type = N'Column', @level2name = 'repayment_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '關聯的貸款案號',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'REPAYMENT_RECORD',
@level2type = N'Column', @level2name = 'application_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '實際扣款的帳號',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'REPAYMENT_RECORD',
@level2type = N'Column', @level2name = 'account_number';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '本次還款期數 (第 N 期)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'REPAYMENT_RECORD',
@level2type = N'Column', @level2name = 'period';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '本次償還本金',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'REPAYMENT_RECORD',
@level2type = N'Column', @level2name = 'principal_paid';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '本次償還利息',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'REPAYMENT_RECORD',
@level2type = N'Column', @level2name = 'interest_paid';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '本次繳交違約金/滯納金',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'REPAYMENT_RECORD',
@level2type = N'Column', @level2name = 'penalty_paid';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '實際扣款/繳款時間',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'REPAYMENT_RECORD',
@level2type = N'Column', @level2name = 'payment_date';
GO

EXEC sp_addextendedproperty
@name = N'Table_Description',
@value = '銀行發行的信用卡產品目錄',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CardTypes';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '卡種編號 (主鍵/自動遞增)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CardTypes',
@level2type = N'Column', @level2name = 'card_type_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '卡片名稱 (如: 現金回饋卡、旅遊卡)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CardTypes',
@level2type = N'Column', @level2name = 'card_type_name';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '發卡組織 (VISA/JCB/Mastercard)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CardTypes',
@level2type = N'Column', @level2name = 'brand';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '預設年費',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CardTypes',
@level2type = N'Column', @level2name = 'annual_fee';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '現金回饋率(%)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CardTypes',
@level2type = N'Column', @level2name = 'cashback_rate';
GO

EXEC sp_addextendedproperty
@name = N'Table_Description',
@value = '刷卡特約商店/商家資訊',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'Merchants';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '商家編號 (主鍵/自動遞增)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'Merchants',
@level2type = N'Column', @level2name = 'merchant_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '請款商店名稱',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'Merchants',
@level2type = N'Column', @level2name = 'merchant_name';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '商家類型 (餐飲/交通/購物等)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'Merchants',
@level2type = N'Column', @level2name = 'merchant_category';
GO

EXEC sp_addextendedproperty
@name = N'Table_Description',
@value = '顧客持有的實體信用卡',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CreditCards';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '信用卡內部流水號 (主鍵/自動遞增)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CreditCards',
@level2type = N'Column', @level2name = 'card_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '持卡人顧客代號',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CreditCards',
@level2type = N'Column', @level2name = 'customer_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '關聯的卡種編號',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CreditCards',
@level2type = N'Column', @level2name = 'card_type_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '實體信用卡卡號 (16碼)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CreditCards',
@level2type = N'Column', @level2name = 'card_number';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '卡片有效期限',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CreditCards',
@level2type = N'Column', @level2name = 'expiry_date';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '該卡片專屬信用額度',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CreditCards',
@level2type = N'Column', @level2name = 'credit_limit';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '目前已刷未繳金額',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CreditCards',
@level2type = N'Column', @level2name = 'current_balance';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '開卡日期/發卡時間',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CreditCards',
@level2type = N'Column', @level2name = 'create_date';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '卡片狀態 (1:正常, 0:停卡/掛失)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CreditCards',
@level2type = N'Column', @level2name = 'status';
GO

EXEC sp_addextendedproperty
@name = N'Table_Description',
@value = '信用卡單筆刷卡明細',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CreditCardTransactions';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '刷卡流水號 (主鍵/自動遞增)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CreditCardTransactions',
@level2type = N'Column', @level2name = 'txn_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '關聯的實體信用卡',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CreditCardTransactions',
@level2type = N'Column', @level2name = 'card_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '請款商家編號',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CreditCardTransactions',
@level2type = N'Column', @level2name = 'merchant_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '單筆刷卡金額',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CreditCardTransactions',
@level2type = N'Column', @level2name = 'txn_amount';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '交易類型 (PURCHASE/REFUND/PAYMENT)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CreditCardTransactions',
@level2type = N'Column', @level2name = 'txn_type';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '實際刷卡時間',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CreditCardTransactions',
@level2type = N'Column', @level2name = 'txn_date';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '消費備註/說明',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CreditCardTransactions',
@level2type = N'Column', @level2name = 'description';
GO

EXEC sp_addextendedproperty
@name = N'Table_Description',
@value = '信用卡每月帳單',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CreditCardBills';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '帳單流水號 (主鍵/自動遞增)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CreditCardBills',
@level2type = N'Column', @level2name = 'bill_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '關聯的實體信用卡',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CreditCardBills',
@level2type = N'Column', @level2name = 'card_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '帳單月份 (如: 2026-03)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CreditCardBills',
@level2type = N'Column', @level2name = 'billing_month';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '帳單產生日期',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CreditCardBills',
@level2type = N'Column', @level2name = 'bill_date';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '最後繳款截止日',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CreditCardBills',
@level2type = N'Column', @level2name = 'due_date';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '本期應繳總金額',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CreditCardBills',
@level2type = N'Column', @level2name = 'total_amount';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '本期最低應繳金額',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CreditCardBills',
@level2type = N'Column', @level2name = 'minimum_payment';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '已繳納金額',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CreditCardBills',
@level2type = N'Column', @level2name = 'paid_amount';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '帳單狀態 (UNPAID/PARTIAL/PAID)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CreditCardBills',
@level2type = N'Column', @level2name = 'bill_status';
GO

EXEC sp_addextendedproperty
@name = N'Table_Description',
@value = '基金商品目錄',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'FUND_MASTER';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '基金代碼 (主鍵/自行編碼)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'FUND_MASTER',
@level2type = N'Column', @level2name = 'fund_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '發行機構/投信名稱',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'FUND_MASTER',
@level2type = N'Column', @level2name = 'issuer_name';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '基金商品名稱',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'FUND_MASTER',
@level2type = N'Column', @level2name = 'fund_name';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '計價幣別',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'FUND_MASTER',
@level2type = N'Column', @level2name = 'currency';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '最新單位淨值',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'FUND_MASTER',
@level2type = N'Column', @level2name = 'latest_nav';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '風險等級 (RR1~RR5)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'FUND_MASTER',
@level2type = N'Column', @level2name = 'risk_level';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '申購手續費率(%)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'FUND_MASTER',
@level2type = N'Column', @level2name = 'fee_rate';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '單筆最低申購門檻',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'FUND_MASTER',
@level2type = N'Column', @level2name = 'min_purchase_amount';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '商品狀態 (上架/下架)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'FUND_MASTER',
@level2type = N'Column', @level2name = 'fund_status';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '產品上架時間',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'FUND_MASTER',
@level2type = N'Column', @level2name = 'created_at';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '淨值最後更新時間',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'FUND_MASTER',
@level2type = N'Column', @level2name = 'updated_at';
GO

EXEC sp_addextendedproperty
@name = N'Table_Description',
@value = '顧客持有的基金庫存',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CUSTOMER_FUND_HOLDING';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '庫存流水號 (主鍵/自動遞增)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CUSTOMER_FUND_HOLDING',
@level2type = N'Column', @level2name = 'holding_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '投資人顧客代號',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CUSTOMER_FUND_HOLDING',
@level2type = N'Column', @level2name = 'customer_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '贖回時預設匯入的帳號',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CUSTOMER_FUND_HOLDING',
@level2type = N'Column', @level2name = 'account_number';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '持有的基金代碼',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CUSTOMER_FUND_HOLDING',
@level2type = N'Column', @level2name = 'fund_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '持有總單位數',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CUSTOMER_FUND_HOLDING',
@level2type = N'Column', @level2name = 'total_units';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '平均申購成本',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CUSTOMER_FUND_HOLDING',
@level2type = N'Column', @level2name = 'average_cost';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '庫存最後更新時間',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CUSTOMER_FUND_HOLDING',
@level2type = N'Column', @level2name = 'updated_at';
GO

EXEC sp_addextendedproperty
@name = N'Table_Description',
@value = '基金申購與贖回交易明細',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'FUND_TRANSACTION_RECORD';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '基金交易流水號 (主鍵/自動遞增)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'FUND_TRANSACTION_RECORD',
@level2type = N'Column', @level2name = 'transaction_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '投資人顧客代號',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'FUND_TRANSACTION_RECORD',
@level2type = N'Column', @level2name = 'customer_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '實際扣款或匯入的帳號',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'FUND_TRANSACTION_RECORD',
@level2type = N'Column', @level2name = 'account_number';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '交易的基金代碼',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'FUND_TRANSACTION_RECORD',
@level2type = N'Column', @level2name = 'fund_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '交易動作 (申購/贖回)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'FUND_TRANSACTION_RECORD',
@level2type = N'Column', @level2name = 'transaction_type';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '交易總金額',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'FUND_TRANSACTION_RECORD',
@level2type = N'Column', @level2name = 'transaction_amount';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '成交單位數',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'FUND_TRANSACTION_RECORD',
@level2type = N'Column', @level2name = 'transaction_units';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '成交當下淨值',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'FUND_TRANSACTION_RECORD',
@level2type = N'Column', @level2name = 'deal_nav';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '內扣或外加手續費',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'FUND_TRANSACTION_RECORD',
@level2type = N'Column', @level2name = 'fee_amount';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '交易狀態 (處理中/已完成/失敗)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'FUND_TRANSACTION_RECORD',
@level2type = N'Column', @level2name = 'transaction_status';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '委託下單時間',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'FUND_TRANSACTION_RECORD',
@level2type = N'Column', @level2name = 'created_at';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '交易完成/結算時間',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'FUND_TRANSACTION_RECORD',
@level2type = N'Column', @level2name = 'completed_at';
GO

ALTER TABLE [ADMIN_LOG] ADD FOREIGN KEY ([admin_id]) REFERENCES [ADMINISTRATOR] ([admin_id])
    GO

ALTER TABLE [KYC_RECORD] ADD FOREIGN KEY ([customer_id]) REFERENCES [CUSTOMER] ([customer_id])
    GO

ALTER TABLE [KYC_RECORD] ADD FOREIGN KEY ([verified_by_admin]) REFERENCES [ADMINISTRATOR] ([admin_id])
    GO

ALTER TABLE [ACCOUNT] ADD FOREIGN KEY ([customer_id]) REFERENCES [CUSTOMER] ([customer_id])
    GO

ALTER TABLE [TRANS_LOG] ADD FOREIGN KEY ([operation_account]) REFERENCES [ACCOUNT] ([account_number])
    GO

ALTER TABLE [LOAN_APPLICATION] ADD FOREIGN KEY ([customer_id]) REFERENCES [CUSTOMER] ([customer_id])
    GO

ALTER TABLE [LOAN_APPLICATION] ADD FOREIGN KEY ([reviewer_id]) REFERENCES [ADMINISTRATOR] ([admin_id])
    GO

ALTER TABLE [REPAYMENT_RECORD] ADD FOREIGN KEY ([application_id]) REFERENCES [LOAN_APPLICATION] ([application_id])
    GO

ALTER TABLE [REPAYMENT_RECORD] ADD FOREIGN KEY ([account_number]) REFERENCES [ACCOUNT] ([account_number])
    GO

ALTER TABLE [CreditCards] ADD FOREIGN KEY ([customer_id]) REFERENCES [CUSTOMER] ([customer_id])
    GO

ALTER TABLE [CreditCards] ADD FOREIGN KEY ([card_type_id]) REFERENCES [CardTypes] ([card_type_id])
    GO

ALTER TABLE [CreditCardTransactions] ADD FOREIGN KEY ([card_id]) REFERENCES [CreditCards] ([card_id])
    GO

ALTER TABLE [CreditCardTransactions] ADD FOREIGN KEY ([merchant_id]) REFERENCES [Merchants] ([merchant_id])
    GO

ALTER TABLE [CreditCardBills] ADD FOREIGN KEY ([card_id]) REFERENCES [CreditCards] ([card_id])
    GO

ALTER TABLE [CUSTOMER_FUND_HOLDING] ADD FOREIGN KEY ([customer_id]) REFERENCES [CUSTOMER] ([customer_id])
    GO

ALTER TABLE [CUSTOMER_FUND_HOLDING] ADD FOREIGN KEY ([account_number]) REFERENCES [ACCOUNT] ([account_number])
    GO

ALTER TABLE [CUSTOMER_FUND_HOLDING] ADD FOREIGN KEY ([fund_id]) REFERENCES [FUND_MASTER] ([fund_id])
    GO

ALTER TABLE [FUND_TRANSACTION_RECORD] ADD FOREIGN KEY ([customer_id]) REFERENCES [CUSTOMER] ([customer_id])
    GO

ALTER TABLE [FUND_TRANSACTION_RECORD] ADD FOREIGN KEY ([account_number]) REFERENCES [ACCOUNT] ([account_number])
    GO

ALTER TABLE [FUND_TRANSACTION_RECORD] ADD FOREIGN KEY ([fund_id]) REFERENCES [FUND_MASTER] ([fund_id])
    GO
