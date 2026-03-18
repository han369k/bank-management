CREATE TABLE [ADMINISTRATOR] (
  [admin_id] int PRIMARY KEY,
  [username] varchar(50),
  [password_hash] varchar(255),
  [name] varchar(50),
  [email] varchar(100),
  [phone_number] varchar(20),
  [role] varchar(20),
  [status] varchar(10),
  [created_at] datetime2,
  [updated_at] datetime2
)
GO

CREATE TABLE [ADMIN_LOG] (
  [log_id] int PRIMARY KEY,
  [admin_id] int,
  [action_type] varchar(20),
  [target_table] varchar(50),
  [target_record_id] varchar(50),
  [action_details] text,
  [action_timestamp] datetime2,
  [ip_address] varchar(50)
)
GO

CREATE TABLE [CUSTOMER] (
  [customer_id] varchar(20) PRIMARY KEY,
  [id_number] varchar(20),
  [name] nvarchar(50),
  [date_of_birth] date,
  [nationality] varchar(50),
  [address] nvarchar(255),
  [phone] varchar(20),
  [email] varchar(100),
  [password_hash] varchar(255),
  [failed_login_attempts] int,
  [income] decimal(19,4),
  [credit_score] int,
  [created_at] datetime2,
  [updated_at] datetime2,
  [status] varchar(20)
)
GO

CREATE TABLE [KYC_RECORD] (
  [kyc_id] int PRIMARY KEY,
  [customer_id] varchar(20),
  [document_type] varchar(20),
  [document_number] varchar(50),
  [expiry_date] date,
  [document_image_url] varchar(255),
  [verification_status] varchar(20),
  [verified_by_admin] int,
  [submission_date] datetime2,
  [verification_date] datetime2,
  [comments] nvarchar(255)
)
GO

CREATE TABLE [ACCOUNT] (
  [account_number] varchar(20) PRIMARY KEY,
  [customer_id] varchar(20),
  [type] varchar(20),
  [currency] varchar(3),
  [balance] decimal(19,4),
  [status] varchar(10),
  [create_at] datetime2,
  [change_at] datetime2
)
GO

CREATE TABLE [TRANS_LOG] (
  [trans_log_id] bigint PRIMARY KEY,
  [reference_id] varchar(50),
  [amount] decimal(19,4),
  [type] varchar(20),
  [operation_account] varchar(20),
  [other_account] varchar(20),
  [balance] decimal(19,4),
  [transaction_time] datetime2,
  [note] nvarchar(255)
)
GO

CREATE TABLE [LOAN_APPLICATION] (
  [application_id] varchar(20) PRIMARY KEY,
  [customer_id] varchar(20),
  [apply_amount] decimal(19,4),
  [apply_period] int,
  [deduct_account_number] varchar(20),
  [approved_amount] decimal(19,4),
  [approved_rate] decimal(5,4),
  [approved_period] int,
  [status] varchar(20),
  [reviewer_id] int,
  [review_time] datetime2
)
GO

CREATE TABLE [REPAYMENT_RECORD] (
  [repayment_id] bigint PRIMARY KEY,
  [application_id] varchar(20),
  [account_number] varchar(20),
  [period] int,
  [principal_paid] decimal(19,4),
  [interest_paid] decimal(19,4),
  [penalty_paid] decimal(19,4),
  [payment_date] datetime2
)
GO

CREATE TABLE [CARD_TYPE] (
  [card_type_id] int PRIMARY KEY,
  [card_type_name] varchar(50),
  [brand] varchar(20),
  [annual_fee] decimal(19,4),
  [cashback_rate] decimal(5,4),
  [credit_limit] decimal(19,4),
  [created_at] datetime2,
  [status] tinyint
)
GO

CREATE TABLE [CREDIT_CARD] (
  [card_id] int PRIMARY KEY,
  [customer_id] varchar(20),
  [card_type_id] int,
  [card_number] varchar(255),
  [expiry_date] char(4),
  [credit_limit] decimal(19,4),
  [current_balance] decimal(19,4),
  [created_at] datetime2,
  [status] tinyint
)
GO

CREATE TABLE [CREDIT_CARD_BILL] (
  [bill_id] int PRIMARY KEY,
  [card_id] int,
  [billing_month] char(7),
  [total_amount] decimal(19,4),
  [minimum_payment] decimal(19,4),
  [due_date] date,
  [bill_status] tinyint,
  [created_at] datetime2
)
GO

CREATE TABLE [CREDIT_CARD_TRANSACTION] (
  [txn_id] int PRIMARY KEY,
  [card_id] int,
  [bill_id] int,
  [merchant_name] nvarchar(100),
  [txn_amount] decimal(19,4),
  [txn_type] varchar(20),
  [txn_status] tinyint,
  [auth_code] varchar(50),
  [txn_date] datetime2,
  [description] nvarchar(255)
)
GO

CREATE TABLE [STOCK_MASTER] (
  [stock_symbol] varchar(10) PRIMARY KEY,
  [company_name] varchar(100),
  [market_type] varchar(20),
  [current_price] decimal(10,2),
  [opening_price] decimal(10,2),
  [highest_price] decimal(10,2),
  [lowest_price] decimal(10,2),
  [trade_volume] bigint,
  [fee_rate] decimal(5,4),
  [status] varchar(20),
  [created_at] datetime2,
  [updated_at] datetime2
)
GO

CREATE TABLE [CUSTOMER_STOCK_HOLDING] (
  [holding_id] bigint PRIMARY KEY,
  [customer_id] varchar(20),
  [account_number] varchar(20),
  [stock_symbol] varchar(10),
  [total_shares] int,
  [average_cost] decimal(10,2),
  [updated_at] datetime2
)
GO

CREATE TABLE [STOCK_TRANSACTION_RECORD] (
  [transaction_id] bigint PRIMARY KEY,
  [customer_id] varchar(20),
  [account_number] varchar(20),
  [stock_symbol] varchar(10),
  [transaction_type] varchar(10),
  [deal_price] decimal(10,2),
  [shares] int,
  [fee_amount] decimal(10,2),
  [transaction_status] varchar(20),
  [transaction_time] datetime2
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
@value = '扣款帳號 (FK)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'LOAN_APPLICATION',
@level2type = N'Column', @level2name = 'deduct_account_number';
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
@value = '卡種代號 (PK)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CARD_TYPE',
@level2type = N'Column', @level2name = 'card_type_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '卡片名稱 (如: 爪哇卡)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CARD_TYPE',
@level2type = N'Column', @level2name = 'card_type_name';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '發卡組織 (VISA/Master)',
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
@value = '現金回饋率',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CARD_TYPE',
@level2type = N'Column', @level2name = 'cashback_rate';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '預設額度',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CARD_TYPE',
@level2type = N'Column', @level2name = 'credit_limit';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '狀態：1 上架 / 0 下架',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CARD_TYPE',
@level2type = N'Column', @level2name = 'status';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '實體卡流水號 (PK)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CREDIT_CARD',
@level2type = N'Column', @level2name = 'card_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '持卡人 (FK)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CREDIT_CARD',
@level2type = N'Column', @level2name = 'customer_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '卡種代號 (FK)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CREDIT_CARD',
@level2type = N'Column', @level2name = 'card_type_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '信用卡號 (需加密)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CREDIT_CARD',
@level2type = N'Column', @level2name = 'card_number';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '到期年月 (MMYY)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CREDIT_CARD',
@level2type = N'Column', @level2name = 'expiry_date';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '個人核准額度',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CREDIT_CARD',
@level2type = N'Column', @level2name = 'credit_limit';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '目前已刷金額',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CREDIT_CARD',
@level2type = N'Column', @level2name = 'current_balance';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '狀態：1 正常 / 2 停卡',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CREDIT_CARD',
@level2type = N'Column', @level2name = 'status';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '帳單代號 (PK)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CREDIT_CARD_BILL',
@level2type = N'Column', @level2name = 'bill_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '卡片代號 (FK)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CREDIT_CARD_BILL',
@level2type = N'Column', @level2name = 'card_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '帳單月份 (YYYY-MM)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CREDIT_CARD_BILL',
@level2type = N'Column', @level2name = 'billing_month';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '本期應繳總額',
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
@value = '繳款截止日',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CREDIT_CARD_BILL',
@level2type = N'Column', @level2name = 'due_date';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '狀態：0 未繳 / 1 已繳清',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CREDIT_CARD_BILL',
@level2type = N'Column', @level2name = 'bill_status';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '刷卡授權代號 (PK)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CREDIT_CARD_TRANSACTION',
@level2type = N'Column', @level2name = 'txn_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '卡片代號 (FK)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CREDIT_CARD_TRANSACTION',
@level2type = N'Column', @level2name = 'card_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '對應帳單代號 (FK)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CREDIT_CARD_TRANSACTION',
@level2type = N'Column', @level2name = 'bill_id';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '商店名稱',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CREDIT_CARD_TRANSACTION',
@level2type = N'Column', @level2name = 'merchant_name';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '消費金額',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CREDIT_CARD_TRANSACTION',
@level2type = N'Column', @level2name = 'txn_amount';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '交易類型 (消費/退刷)',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CREDIT_CARD_TRANSACTION',
@level2type = N'Column', @level2name = 'txn_type';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '狀態：1 授權成功',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CREDIT_CARD_TRANSACTION',
@level2type = N'Column', @level2name = 'txn_status';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '授權碼',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CREDIT_CARD_TRANSACTION',
@level2type = N'Column', @level2name = 'auth_code';
GO

EXEC sp_addextendedproperty
@name = N'Column_Description',
@value = '消費時間',
@level0type = N'Schema', @level0name = 'dbo',
@level1type = N'Table',  @level1name = 'CREDIT_CARD_TRANSACTION',
@level2type = N'Column', @level2name = 'txn_date';
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

ALTER TABLE [LOAN_APPLICATION] ADD FOREIGN KEY ([deduct_account_number]) REFERENCES [ACCOUNT] ([account_number])
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

ALTER TABLE [CREDIT_CARD_TRANSACTION] ADD FOREIGN KEY ([bill_id]) REFERENCES [CREDIT_CARD_BILL] ([bill_id])
GO

ALTER TABLE [CUSTOMER_STOCK_HOLDING] ADD FOREIGN KEY ([stock_symbol]) REFERENCES [STOCK_MASTER] ([stock_symbol])
GO

ALTER TABLE [STOCK_TRANSACTION_RECORD] ADD FOREIGN KEY ([stock_symbol]) REFERENCES [STOCK_MASTER] ([stock_symbol])
GO
