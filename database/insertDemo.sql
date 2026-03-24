SET NOCOUNT ON;
-- 請確保已執行 USE [你的資料庫名];
GO

PRINT N'=========================================';
PRINT N'   Demo 專用 - 腳本';
PRINT N'=========================================';

-- 1. 信用卡別、商家、股票 (略，與你提供的一致)
-- ... (請保留你原本腳本的 1, 2, 3 部分) ...

PRINT N'4. 開始產生 100 筆測試資料...';

DECLARE @i INT = 1;
DECLARE @custId VARCHAR(20), @accNum VARCHAR(12), @loanId VARCHAR(20), @cardNum VARCHAR(16);
DECLARE @surnames NVARCHAR(100) = N'陳林黃張李王吳劉蔡楊許鄭謝洪郭邱曾廖賴徐周彭柯蘇盧詹莊侯游';
DECLARE @chars NVARCHAR(200) = N'家豪雅婷宗翰怡君柏翰佳穎冠霖詩涵哲宇雅雯志偉婷婷建文淑芬明哲佩珊宇翔宜蓁柏宇欣瑜浩宇心怡承恩郁婷宥廷婉婷辰睿靜雯俊傑冠宇建國美麗';
DECLARE @adminName NVARCHAR(50), @custName NVARCHAR(50);
DECLARE @randCardTypeId INT, @randMerchantId INT, @randStockSymbol VARCHAR(10), @randPhone VARCHAR(20), @randApplyType NVARCHAR(20);
DECLARE @safeTimeStr VARCHAR(14), @rnd INT; 

WHILE @i <= 100
BEGIN
    SET @adminName = SUBSTRING(@surnames, (ABS(CHECKSUM(NEWID())) % LEN(@surnames)) + 1, 1) + SUBSTRING(@chars, (ABS(CHECKSUM(NEWID())) % LEN(@chars)) + 1, 1) + SUBSTRING(@chars, (ABS(CHECKSUM(NEWID())) % LEN(@chars)) + 1, 1);
    SET @custName = SUBSTRING(@surnames, (ABS(CHECKSUM(NEWID())) % LEN(@surnames)) + 1, 1) + SUBSTRING(@chars, (ABS(CHECKSUM(NEWID())) % LEN(@chars)) + 1, 1) + SUBSTRING(@chars, (ABS(CHECKSUM(NEWID())) % LEN(@chars)) + 1, 1);
    SET @safeTimeStr = CONVERT(VARCHAR(8), GETDATE(), 112) + REPLACE(CONVERT(VARCHAR(8), GETDATE(), 108), ':', '');
    
    SET @custId = 'C' + RIGHT('00000' + CAST(@i AS VARCHAR), 5);
    SET @accNum = '808' + RIGHT('000000000' + CAST(@i AS VARCHAR), 9);
    SET @loanId = 'LA' + @safeTimeStr + RIGHT('0000' + CAST(@i AS VARCHAR), 4);
    SET @cardNum = (CASE WHEN @i % 2 = 0 THEN '4500' ELSE '5400' END) + '12345678' + RIGHT('0000' + CAST(@i AS VARCHAR), 4);
    SET @randPhone = '09' + RIGHT('00000000' + CAST((ABS(CHECKSUM(NEWID())) % 100000000) AS VARCHAR), 8);
    
    -- 隨機種類
    SET @rnd = ABS(CHECKSUM(NEWID())) % 7;
    SET @randApplyType = CASE @rnd WHEN 0 THEN 'PERSONAL' WHEN 1 THEN 'CAR' WHEN 2 THEN 'MOTOR' WHEN 3 THEN 'STUDENT' WHEN 4 THEN 'BUSINESS' WHEN 5 THEN 'HOUSE' ELSE 'LAND' END;

    -- 1. stock_info
    INSERT INTO [stock_info] ([stock_id], [stock_name], [status])
    VALUES (1000 + @i, N'新興潛力股 ' + CAST((1000 + @i) AS NVARCHAR), CASE WHEN @i % 5 = 0 THEN 0 ELSE 1 END);

    -- 2. CUSTOMER
    INSERT INTO [CUSTOMER] ([customer_id], [id_number], [name], [phone], [email], [income], [credit_score], [status], [created_at])
    VALUES (@custId, 'A' + RIGHT('123456789' + CAST(@i AS VARCHAR), 9), @custName, @randPhone, 'user' + CAST(@i AS VARCHAR) + '@gmail.com', ROUND(RAND()*1000000+500000, 0), ROUND(RAND()*300+500, 0), 'ACTIVE', GETDATE());

    -- 3. ACCOUNT
    INSERT INTO [ACCOUNT] ([account_number], [customer_id], [type], [currency], [balance], [status], [create_at])
    VALUES (@accNum, @custId, IIF(@i%2=0, 'SAVINGS', 'CHECKING'), 'TWD', ROUND(RAND()*800000+10000, 2), 'ACTIVE', GETDATE());

    -- 4. TRANS_LOG (✅ 修正：移除 trans_log_id，讓 IDENTITY 自動跳號)
    INSERT INTO [TRANS_LOG] ([amount], [type], [operation_account], [balance], [transaction_time], [note])
    VALUES (ROUND(RAND()*10000, 2), IIF(@i%2=0, 'DEPOSIT', 'WITHDRAW'), @accNum, ROUND(RAND()*500000, 2), GETDATE(), N'開戶初始資金');

    -- 5. LOAN_APPLICATION (✅ 修正：前 20 筆設為 PENDING，方便 Demo 審核功能)
    INSERT INTO [LOAN_APPLICATION] ([application_id], [customer_id], [apply_type], [apply_amount], [apply_period], [rate], [status], [reviewer_id], [create_time])
    VALUES (@loanId, @custId, @randApplyType, ROUND(RAND()*2000000+100000, 0), 36, 0.05, IIF(@i <= 20, 'PENDING', 'APPROVED'), IIF(@i <= 20, NULL, 1), GETDATE());

    -- 6. CREDIT_CARD (略...)
    -- 7. STOCK_HOLDING (略...)

    SET @i = @i + 1;
END

PRINT N'✅ 測試資料生成完畢！';
GO