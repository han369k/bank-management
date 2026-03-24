/**
 * customer.js — 顧客管理模組的 JavaScript 驗證邏輯
 *
 * 所有 id 都用 cust- 開頭，不會跟其他模組衝突。
 *
 * 這支 JS 在表單送出前，先檢查使用者有沒有填對格式：
 * - 身分證字號：必須 10 碼、第一碼英文字母、後9碼數字
 * - 姓名：不可空白
 * - 顧客代號：不可空白
 * - 月收入：不可為負數
 */

// ===== 工具函式：顯示錯誤訊息 =====
function custShowError(elementId, message) {
    var el = document.getElementById(elementId);
    if (el) {
        el.textContent = message;
        el.style.display = 'block';
    }
}

// ===== 工具函式：隱藏錯誤訊息 =====
function custHideError(elementId) {
    var el = document.getElementById(elementId);
    if (el) {
        el.style.display = 'none';
    }
}

// ===== 工具函式：驗證身分證字號格式 =====
// 台灣身分證：第1碼為英文字母（A-Z），後9碼全為數字，共10碼
function custValidateIdNumber(idNumber) {
    var pattern = /^[A-Za-z][0-9]{9}$/;
    return pattern.test(idNumber);
}

// ===== 【查詢表單】送出前驗證 =====
document.getElementById('cust-search-form').addEventListener('submit', function(event) {
    custHideError('cust-search-error');

    var idInput = document.getElementById('cust-search-id').value.trim();

    if (idInput === '') {
        custShowError('cust-search-error', '⚠️ 請輸入身分證字號！');
        event.preventDefault(); // 阻止表單送出
        return;
    }

    if (!custValidateIdNumber(idInput)) {
        custShowError('cust-search-error', '⚠️ 身分證字號格式錯誤！必須是1碼英文字母 + 9碼數字，共10碼。');
        event.preventDefault();
        return;
    }

    // 格式正確，讓英文變大寫後送出
    document.getElementById('cust-search-id').value = idInput.toUpperCase();
});

// ===== 【新增表單】送出前驗證 =====
document.getElementById('cust-insert-form').addEventListener('submit', function(event) {
    custHideError('cust-insert-error');

    var customerId = document.getElementById('cust-new-id').value.trim();
    var idNumber   = document.getElementById('cust-new-idno').value.trim();
    var name       = document.getElementById('cust-new-name').value.trim();
    var income     = document.getElementById('cust-new-income').value;
    var creditScore = document.getElementById('cust-new-credit').value;

    // 必填項目檢查
    if (customerId === '') {
        custShowError('cust-insert-error', '⚠️ 顧客代號不可空白！');
        event.preventDefault();
        return;
    }

    if (idNumber === '') {
        custShowError('cust-insert-error', '⚠️ 身分證字號不可空白！');
        event.preventDefault();
        return;
    }

    if (!custValidateIdNumber(idNumber)) {
        custShowError('cust-insert-error', '⚠️ 身分證字號格式錯誤！必須是1碼英文字母 + 9碼數字，共10碼。');
        event.preventDefault();
        return;
    }

    if (name === '') {
        custShowError('cust-insert-error', '⚠️ 姓名不可空白！');
        event.preventDefault();
        return;
    }

    // 月收入不可為負數
    if (income !== '' && parseFloat(income) < 0) {
        custShowError('cust-insert-error', '⚠️ 月收入不可為負數！');
        event.preventDefault();
        return;
    }

    // 信用分數範圍檢查
    if (creditScore !== '') {
        var score = parseInt(creditScore);
        if (isNaN(score) || score < 300 || score > 850) {
            custShowError('cust-insert-error', '⚠️ 信用分數必須介於 300 ~ 850 之間！');
            event.preventDefault();
            return;
        }
    }

    // 全部通過，把身分證轉大寫
    document.getElementById('cust-new-idno').value = idNumber.toUpperCase();
});

// ===== 【修改表單】送出前驗證 =====
document.getElementById('cust-update-form').addEventListener('submit', function(event) {
    custHideError('cust-update-error');

    var customerId = document.getElementById('cust-upd-id').value.trim();

    if (customerId === '') {
        custShowError('cust-update-error', '⚠️ 請輸入顧客代號！例如：C2026001');
        event.preventDefault();
        return;
    }
});

// ===== 【刪除表單】送出前確認彈窗 =====
document.getElementById('cust-delete-form').addEventListener('submit', function(event) {
    custHideError('cust-delete-error');

    var customerId = document.getElementById('cust-del-id').value.trim();

    if (customerId === '') {
        custShowError('cust-delete-error', '⚠️ 請輸入要刪除的顧客代號！');
        event.preventDefault();
        return;
    }

    // 刪除前要再三確認！
    var confirmed = confirm('⚠️ 確定要刪除顧客「' + customerId + '」嗎？\n此操作無法復原！');
    if (!confirmed) {
        event.preventDefault(); // 使用者按取消，就不送出
    }
});
