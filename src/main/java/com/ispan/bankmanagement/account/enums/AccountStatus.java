package com.ispan.bankmanagement.account.enums;

// todo: 要加表把這個拆開來設計
public enum AccountStatus {
    INACTIVE,           // 待啟用
    ACTIVE,             // 啟用
    FROZEN,             // 凍結、警示
    CLOSED,             // 結清
    DORMANT,            // 靜止戶

    DERIVED_CONTROL,    // 衍伸管制：擁有其他帳戶遭到警示
    LIMITED,            // 限額：配合封控偵測異常
    HOLDING,            // 圈存：簽帳卡交易、法院命令扣押
    ADMONITION,         // 告誡管制：曾遭受洗錢防制法裁處的客戶，
                        // 必須限制其非實體交易，ATM限額至10000內。
    SUSPENDED           // 暫停服務：密碼輸入錯誤次數過多，鎖實體卡交易
}
