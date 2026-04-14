SET NOCOUNT ON;
GO

-- 關閉FK設定使用 --
EXEC sp_MSforeachtable 'ALTER TABLE ? NOCHECK CONSTRAINT ALL';

PRINT N'=========================================';
PRINT N'   Demo 專用    ';
PRINT N'=========================================';

-- 0. 建立預設審核員 (若無則建，避免 LOAN_APPLICATION FK 報錯)
IF NOT EXISTS (SELECT 1 FROM [ADMINISTRATOR] WHERE [admin_id] = 1)
BEGIN
    INSERT INTO [ADMINISTRATOR] ([admin_id], [username], [password_hash], [name], [role], [status], [created_at])
    VALUES (1, 'demo_admin', 'e10adc3949ba59abbe56e057f20f883e', N'預設審核員', 'MANAGER', 'ACTIVE', GETDATE());
END
GO

PRINT N'開始產生 100 筆測試資料 (具備重複執行防護)...';

DECLARE @i INT = 1;
DECLARE @custId VARCHAR(20), @accNum VARCHAR(12), @loanId VARCHAR(20), @cardNum VARCHAR(16);
DECLARE @surnames NVARCHAR(100) = N'陳林黃張李王吳劉蔡楊許鄭謝洪郭邱曾廖賴徐周彭柯蘇盧詹莊侯游';
DECLARE @chars NVARCHAR(200) = N'家豪雅婷宗翰怡君柏翰佳穎冠霖詩涵哲宇雅雯志偉婷婷建文淑芬明哲佩珊宇翔宜蓁柏宇欣瑜浩宇心怡承恩郁婷宥廷婉婷辰睿靜雯俊傑冠宇建國美麗';
DECLARE @custName NVARCHAR(50), @randApplyType NVARCHAR(20), @safeTimeStr VARCHAR(14), @rnd INT; 
DECLARE @mockTime DATETIME2, @mockDOB DATE;
DECLARE @initBalance DECIMAL(19,4), @applyAmount BIGINT;
DECLARE @defaultPwdHash VARCHAR(255) = 'e10adc3949ba59abbe56e057f20f883e'; 

WHILE @i <= 100
BEGIN
    -- 產生安全的 PK 字串與假時間
    SET @custId = 'C' + RIGHT('00000' + CAST(@i AS VARCHAR), 5);
    SET @accNum = '808' + RIGHT('000000000' + CAST(@i AS VARCHAR), 9);
    
    -- 時間往前推移，避免超過 GETDATE() 產生未來時間的邏輯 Bug
    SET @mockTime = DATEADD(MINUTE, - (ABS(CHECKSUM(NEWID())) % 1440), DATEADD(DAY, - (100 - @i), GETDATE()));
    SET @safeTimeStr = CONVERT(VARCHAR(8), @mockTime, 112) + REPLACE(CONVERT(VARCHAR(8), @mockTime, 108), ':', '');
    SET @loanId = 'LA' + @safeTimeStr + RIGHT('0000' + CAST(@i AS VARCHAR), 4); 

    -- 防呆：如果這筆顧客已經存在，跳過此次迴圈，防止 PK Violation 崩潰
    IF NOT EXISTS (SELECT 1 FROM [CUSTOMER] WHERE [customer_id] = @custId)
    BEGIN
        -- 變數準備
        SET @mockDOB = DATEADD(DAY, - (ABS(CHECKSUM(NEWID())) % 10950 + 7300), GETDATE());
        SET @custName = SUBSTRING(@surnames, (ABS(CHECKSUM(NEWID())) % LEN(@surnames)) + 1, 1) + SUBSTRING(@chars, (ABS(CHECKSUM(NEWID())) % LEN(@chars)) + 1, 1) + SUBSTRING(@chars, (ABS(CHECKSUM(NEWID())) % LEN(@chars)) + 1, 1);
        SET @initBalance = (ABS(CHECKSUM(NEWID())) % 800000) + 10000;
        SET @applyAmount = (ABS(CHECKSUM(NEWID())) % 2000001) + 100000;
        SET @rnd = ABS(CHECKSUM(NEWID())) % 7;
        SET @randApplyType = CASE @rnd WHEN 0 THEN 'PERSONAL' WHEN 1 THEN 'CAR' WHEN 2 THEN 'MOTOR' WHEN 3 THEN 'STUDENT' WHEN 4 THEN 'BUSINESS' WHEN 5 THEN 'HOUSE' ELSE 'LAND' END;

        -- 1. stock_info
        IF NOT EXISTS (SELECT 1 FROM [stock_info] WHERE [stock_id] = 1000 + @i)
        BEGIN
            INSERT INTO [stock_info] ([stock_id], [stock_name], [status])
            VALUES (1000 + @i, N'新興潛力股 ' + CAST((1000 + @i) AS NVARCHAR), CASE WHEN @i % 5 = 0 THEN 0 ELSE 1 END);
        END

        -- 2. CUSTOMER
        INSERT INTO [CUSTOMER] ([customer_id], [id_number], [name], [date_of_birth], [phone], [email], [password_hash], [failed_login_attempts], [income], [credit_score], [status], [created_at])
        VALUES (
            @custId, 
            'A' + RIGHT('123456789' + CAST(@i AS VARCHAR), 9), 
            @custName, 
            @mockDOB,
            '09' + RIGHT('00000000' + CAST((ABS(CHECKSUM(NEWID())) % 100000000) AS VARCHAR), 8), 
            'user' + RIGHT('000' + CAST(@i AS VARCHAR), 3) + '@gmail.com', 
            @defaultPwdHash,
            0,
            (ABS(CHECKSUM(NEWID())) % 1000001) + 500000,   
            (ABS(CHECKSUM(NEWID())) % 301) + 500,          
            'ACTIVE', 
            @mockTime
        );

        -- 3. ACCOUNT 
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

        -- 4. TRANS_LOG 
        INSERT INTO [TRANS_LOG] ([amount], [type], [operation_account], [balance], [transaction_time], [note])
        VALUES (
            @initBalance,        
            'DEPOSIT',           
            @accNum, 
            @initBalance,        
            @mockTime, 
            N'開戶初始資金'
        );

        -- 5. LOAN_APPLICATION 
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
            CASE WHEN @i <= 20 THEN CAST(NULL AS BIGINT) ELSE @applyAmount END, 
            CASE WHEN @i <= 20 THEN CAST(NULL AS INT) ELSE 36 END,           
            CASE WHEN @i <= 20 THEN CAST(NULL AS DECIMAL(4,2)) ELSE 0.05 END,         
            CASE WHEN @i <= 20 THEN 'PENDING' ELSE 'APPROVED' END, 
            CASE WHEN @i <= 20 THEN CAST(NULL AS INT) ELSE 1 END, 
            @mockTime,
            CASE WHEN @i <= 20 THEN CAST(NULL AS DATETIME2(2)) ELSE DATEADD(HOUR, (ABS(CHECKSUM(NEWID())) % 4) + 1, @mockTime) END
        );
    END -- End IF NOT EXISTS

    SET @i = @i + 1;
END

PRINT N'✅ 測試資料生成完畢！';
GO