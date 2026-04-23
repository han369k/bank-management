SET NOCOUNT ON;
GO

-- 關閉 FK 設定以利測試資料匯入 --
EXEC sp_MSforeachtable 'ALTER TABLE ? NOCHECK CONSTRAINT ALL';

PRINT N'=========================================';
PRINT N'    Demo 專用測試資料生成 (修正版)    ';
PRINT N'=========================================';

-- 1. 建立管理員帳號
IF NOT EXISTS (SELECT 1 FROM [ADMINISTRATOR] WHERE [admin_id] = 1)
BEGIN
    INSERT INTO [ADMINISTRATOR] ([admin_id], [username], [password_hash], [name], [role], [status], [created_at])
    VALUES (1, 'demo_admin', 'e10adc3949ba59abbe56e057f20f883e', N'預設審核員', 'MANAGER', 'ACTIVE', GETDATE());
END

IF NOT EXISTS (SELECT 1 FROM [ADMINISTRATOR] WHERE [admin_id] = 2)
BEGIN
    INSERT INTO [ADMINISTRATOR] ([admin_id], [username], [password_hash], [name], [role], [status], [created_at])
    VALUES (2, 'admin123', 'test123', N'測試管理員', 'MANAGER', 'ACTIVE', GETDATE());
END
GO -- 這裡分開批次是正確的，接下來宣告變數

PRINT N'開始產生 100 筆測試資料...';

DECLARE @i INT = 1;
DECLARE @custId INT;
DECLARE @accNum VARCHAR(12), @loanId VARCHAR(20);
DECLARE @surnames NVARCHAR(100) = N'陳林黃張李王吳劉蔡楊許鄭謝洪郭邱曾廖賴徐周彭柯蘇盧詹莊侯游';
DECLARE @chars NVARCHAR(200) = N'家豪雅婷宗翰怡君柏翰佳穎冠霖詩涵哲宇雅雯志偉婷婷建文淑芬明哲佩珊宇翔宜蓁柏宇欣瑜浩宇心怡承恩郁婷宥廷婉婷辰睿靜雯俊傑冠宇建國美麗';
DECLARE @custName NVARCHAR(50), @randApplyType NVARCHAR(20), @safeTimeStr VARCHAR(14), @rnd INT;
DECLARE @mockTime DATETIME2, @mockDOB DATE;
DECLARE @initBalance DECIMAL(19,4), @applyAmount BIGINT;

WHILE @i <= 100
BEGIN
    -- 產生純數字的顧客 ID
    SET @custId = 10000 + @i; 

    -- 防重複執行保護
    IF NOT EXISTS (SELECT 1 FROM [CUSTOMER] WHERE [customer_id] = @custId)
    BEGIN
        -- 變數準備
        SET @mockTime = DATEADD(MINUTE, - (ABS(CHECKSUM(NEWID())) % 1440), DATEADD(DAY, - (100 - @i), GETDATE()));
        SET @safeTimeStr = CONVERT(VARCHAR(8), @mockTime, 112) + REPLACE(CONVERT(VARCHAR(8), @mockTime, 108), ':', '');
        
        SET @accNum = '808' + RIGHT('000000000' + CAST(@i AS VARCHAR), 9);
        SET @loanId = 'LA' + @safeTimeStr + RIGHT('0000' + CAST(@i AS VARCHAR), 4);
        SET @mockDOB = DATEADD(DAY, - (ABS(CHECKSUM(NEWID())) % 10950 + 7300), GETDATE());
        SET @custName = SUBSTRING(@surnames, (ABS(CHECKSUM(NEWID())) % LEN(@surnames)) + 1, 1) + 
                        SUBSTRING(@chars, (ABS(CHECKSUM(NEWID())) % LEN(@chars)) + 1, 1) + 
                        SUBSTRING(@chars, (ABS(CHECKSUM(NEWID())) % LEN(@chars)) + 1, 1);
        SET @initBalance = (ABS(CHECKSUM(NEWID())) % 800000) + 10000;
        SET @applyAmount = (ABS(CHECKSUM(NEWID())) % 2000001) + 100000;
        SET @rnd = ABS(CHECKSUM(NEWID())) % 7;
        SET @randApplyType = CASE @rnd WHEN 0 THEN 'PERSONAL' WHEN 1 THEN 'CAR' WHEN 2 THEN 'MOTOR' WHEN 3 THEN 'STUDENT' WHEN 4 THEN 'BUSINESS' WHEN 5 THEN 'HOUSE' ELSE 'LAND' END;

        -- 2. 插入 CUSTOMER (確保外鍵關聯存在)
        -- 注意：請確認你的 CUSTOMER 表欄位名稱，如為 birthday 則修改 [dob]
        INSERT INTO [CUSTOMER] ([customer_id], [name], [date_of_birth], [created_at])
        VALUES (@custId, @custName, @mockDOB, @mockTime);

        -- 3. 插入 ACCOUNT 
        INSERT INTO [ACCOUNT] ([account_number], [customer_id], [type], [currency], [balance], [status], [create_at])
        VALUES (
            @accNum, 
            @custId, 
            CASE WHEN @i % 2 = 0 THEN 'SAVINGS' ELSE 'CHECKING' END, 
            'TWD', 
            @initBalance,      
            'ACTIVE', 
            @mockTime
        );

        -- 4. 插入 TRANS_LOG 
        INSERT INTO [TRANS_LOG] ([reference_id], [amount], [type], [operation_account], [balance], [transaction_time], [note])
        VALUES (
            CAST(NEWID() AS VARCHAR(50)),
            @initBalance,        
            'DEPOSIT',           
            @accNum, 
            @initBalance,        
            @mockTime, 
            N'開戶初始資金'
        );

        -- 5. 插入 LOAN_APPLICATION
        INSERT INTO [LOAN_APPLICATION] (
            [application_id], [customer_id], [apply_type], [apply_amount], [apply_period], [rate],
            [approved_amount], [approved_period], [approved_rate],
            [status], [reviewer_id], [create_time], [review_time]
        )
        VALUES (
            @loanId,
            @custId,
            @randApplyType,
            @applyAmount,
            36,
            0.05,
            CASE WHEN @i <= 20 THEN NULL ELSE @applyAmount END,
            CASE WHEN @i <= 20 THEN NULL ELSE 36 END,
            CASE WHEN @i <= 20 THEN NULL ELSE 0.05 END,
            CASE WHEN @i <= 20 THEN 'PENDING' ELSE 'APPROVED' END,
            CASE WHEN @i <= 20 THEN NULL ELSE 1 END,
            @mockTime,
            CASE WHEN @i <= 20 THEN NULL ELSE DATEADD(HOUR, (ABS(CHECKSUM(NEWID())) % 4) + 1, @mockTime) END
        );
    END -- End IF NOT EXISTS

    SET @i = @i + 1;
END

-- 2. CARD_TYPE
INSERT INTO CARD_TYPE (card_type_name, brand, annual_fee, cashback_rate, card_image_url) VALUES
('Cashback Card', 'VISA', 1000, 1.5, 'img/cashback1.png'),
('Travel Card', 'Master', 2000, 2.0, 'img/travel1.png'),
('Department Store Card', 'JCB', 1500, 1.2, 'img/store1.png'),
('Gas Card', 'VISA', 800, 3.0, 'img/gas1.png'),
('Student Card', 'Master', 0, 0.5, 'img/student1.png'),
('Business Card', 'VISA', 5000, 2.5, 'img/business1.png'),
('Airline Card', 'JCB', 3000, 2.2, 'img/airline1.png'),
('Shopping Card', 'Master', 1200, 1.8, 'img/shop1.png'),
('Dining Card', 'VISA', 900, 2.0, 'img/food1.png'),
('Black Card', 'Master', 10000, 3.5, 'img/black1.png');

-- 3. MERCHANT
INSERT INTO MERCHANT (merchant_name, merchant_category) VALUES
('7-11', 'Retail'),
('FamilyMart', 'Retail'),
('Starbucks', 'Food'),
('McDonalds', 'Food'),
('THSR', 'Transport'),
('Uber', 'Transport'),
('Shopee', 'Retail'),
('Netflix', 'Entertainment'),
('Eslite Bookstore', 'Retail'),
('Carrefour', 'Retail');

-- 4. CARD_APPLICATION
INSERT INTO CARD_APPLICATION (customer_id, status, remark) VALUES
(1, 'PENDING', NULL),
(2, 'APPROVED', 'Good credit'),
(3, 'REJECTED', 'Low income'),
(4, 'PENDING', NULL),
(5, 'APPROVED', NULL),
(6, 'PENDING', NULL),
(7, 'REJECTED', 'Bad credit history'),
(8, 'APPROVED', NULL),
(9, 'PENDING', NULL),
(10, 'APPROVED', NULL);

-- 5. CARD_APPLICATION_ITEM
INSERT INTO CARD_APPLICATION_ITEM 
(application_id, card_type_id, result, approved_limit, annual_fee, create_card_flag, remark) VALUES
(1, 1, 'PENDING', NULL, NULL, 0, NULL),
(2, 2, 'APPROVED', 100000, 2000, 1, NULL),
(3, 3, 'REJECTED', NULL, NULL, 0, 'Credit issue'),
(4, 4, 'PENDING', NULL, NULL, 0, NULL),
(5, 5, 'APPROVED', 50000, 0, 1, NULL),
(6, 6, 'PENDING', NULL, NULL, 0, NULL),
(7, 7, 'REJECTED', NULL, NULL, 0, 'Score too low'),
(8, 8, 'APPROVED', 120000, 1200, 1, NULL),
(9, 9, 'PENDING', NULL, NULL, 0, NULL),
(10, 10, 'APPROVED', 500000, 10000, 1, NULL);

-- 6. CREDIT_CARD
INSERT INTO CREDIT_CARD 
(customer_id, card_type_id, application_item_id, card_number, expiry_date, credit_limit, current_balance, status)
VALUES
(2, 2, 2, '4000000000000001', '2028-12-31', 100000, 2000, 'ACTIVE'),
(5, 5, 5, '4000000000000002', '2027-06-30', 50000, 1000, 'ACTIVE'),
(8, 8, 8, '4000000000000003', '2029-01-31', 120000, 5000, 'ACTIVE'),
(10, 10, 10, '4000000000000004', '2030-12-31', 500000, 20000, 'ACTIVE'),
(2, 1, NULL, '4000000000000005', '2027-05-31', 80000, 3000, 'ACTIVE'),
(5, 4, NULL, '4000000000000006', '2026-11-30', 60000, 4000, 'BLOCKED'),
(8, 6, NULL, '4000000000000007', '2028-09-30', 150000, 7000, 'ACTIVE'),
(10, 7, NULL, '4000000000000008', '2029-03-31', 200000, 10000, 'ACTIVE'),
(2, 3, NULL, '4000000000000009', '2027-08-31', 90000, 2000, 'ACTIVE'),
(5, 9, NULL, '4000000000000010', '2026-12-31', 70000, 1500, 'ACTIVE');

-- 7. CREDIT_CARD_TRANSACTION
INSERT INTO CREDIT_CARD_TRANSACTION 
(card_id, merchant_id, txn_amount, txn_type, description) VALUES
(1, 1, 150, 'PURCHASE', 'Convenience store'),
(1, 3, 200, 'PURCHASE', 'Coffee'),
(2, 4, 300, 'PURCHASE', 'Fast food'),
(3, 5, 1200, 'PURCHASE', 'Train ticket'),
(4, 7, 2500, 'PURCHASE', 'Online shopping'),
(5, 8, 390, 'PURCHASE', 'Subscription'),
(6, 6, 500, 'PURCHASE', 'Ride'),
(7, 2, 180, 'PURCHASE', 'Retail'),
(8, 9, 600, 'PURCHASE', 'Books'),
(9, 10, 2200, 'PURCHASE', 'Supermarket');

-- 8. CREDIT_CARD_BILL
INSERT INTO CREDIT_CARD_BILL
(card_id, billing_month, bill_date, due_date, total_amount, minimum_payment, paid_amount, bill_status)
VALUES
(1, '2026-03', '2026-03-25', '2026-04-10', 2000, 200, 2000, 'PAID'),
(2, '2026-03', '2026-03-25', '2026-04-10', 1000, 100, 500, 'PARTIAL'),
(3, '2026-03', '2026-03-25', '2026-04-10', 5000, 500, 0, 'UNPAID'),
(4, '2026-03', '2026-03-25', '2026-04-10', 20000, 2000, 20000, 'PAID'),
(5, '2026-03', '2026-03-25', '2026-04-10', 3000, 300, 1000, 'PARTIAL'),
(6, '2026-03', '2026-03-25', '2026-04-10', 4000, 400, 0, 'UNPAID'),
(7, '2026-03', '2026-03-25', '2026-04-10', 7000, 700, 7000, 'PAID'),
(8, '2026-03', '2026-03-25', '2026-04-10', 10000, 1000, 5000, 'PARTIAL'),
(9, '2026-03', '2026-03-25', '2026-04-10', 2000, 200, 0, 'UNPAID'),
(10, '2026-03', '2026-03-25', '2026-04-10', 1500, 150, 1500, 'PAID');


-- 恢復 FK 設定 --
EXEC sp_MSforeachtable 'ALTER TABLE ? CHECK CONSTRAINT ALL';

PRINT N'=========================================';
PRINT N'? 測試資料生成完畢！';
PRINT N'=========================================';
GO
