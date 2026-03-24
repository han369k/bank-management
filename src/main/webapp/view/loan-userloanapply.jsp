<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-TW">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>申請貸款 - Bank Management</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
  <link rel="stylesheet" href="../css/style.css">
  <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
  <style>
    .page-wrapper { max-width: 550px; margin: 40px auto; padding: 0 16px; }
    .card-custom { border: 1px solid #eee; border-radius: 8px; box-shadow: 0 1px 4px rgba(0,0,0,.05); }
    .readonly-field { background-color: #f8f9fa !important; color: #555; font-weight: 500; cursor: not-allowed; }
  </style>
</head>
<body>

<nav class="navbar navbar-expand-lg">
  <div class="container-fluid px-4">
    <a class="navbar-brand" href="../index.html"><i class="bi bi-bank me-2"></i>Bank Management</a>
    <div class="navbar-nav ms-3">
      <a class="nav-link" href="account-list.html">帳戶</a>
      <a class="nav-link active" href="loan-userloanapply.jsp">申請貸款</a>
      <a class="nav-link" href="translog-search.html">交易紀錄</a>
    </div>
  </div>
</nav>

<div class="page-wrapper">
  <div class="d-flex justify-content-between align-items-center mb-4">
    <div>
      <h2 class="page-title mb-1"><i class="bi bi-cash-stack me-2"></i>申請貸款</h2>
      <p class="text-muted small mb-0">請填寫您的貸款需求與預期方案</p>
    </div>
  </div>

  <div class="card card-custom p-2">
    <div class="card-body">
      <form id="loanForm" class="row g-3">
        <div class="col-12">
          <label class="form-label text-muted small fw-bold">客戶 ID <span class="text-danger">*</span></label>
          <input name="customerId" id="customerId" class="form-control" placeholder="請輸入客戶身分識別碼 (例: C00001)" required>
        </div>

        <div class="col-md-6">
          <label class="form-label text-muted small fw-bold">貸款方案 <span class="text-danger">*</span></label>
          <select name="applyType" id="type" class="form-select" required>
            <option value="">請選擇貸款種類</option>
            <option value="PERSONAL">信用貸款</option>
            <option value="CAR">汽車車貸</option>
            <option value="MOTOR">機車車貸</option>
            <option value="STUDENT">就學貸款</option>
            <option value="BUSINESS">創業貸款</option>
            <option value="HOUSE">房屋貸款</option>
            <option value="LAND">土地貸款</option>
          </select>
        </div>

        <div class="col-md-6">
          <label class="form-label text-muted small fw-bold">期數 <span class="text-danger">*</span></label>
          <select name="applyPeriod" id="term" class="form-select" required>
            <option value="">請先選擇貸款種類</option>
          </select>
        </div>

        <div class="col-12">
          <label class="form-label text-muted small fw-bold">貸款金額 ($) <span class="text-danger">*</span></label>
          <input name="applyAmount" id="amount" type="number" class="form-control" min="1000" placeholder="最低 1,000 元" required>
        </div>

        <div class="col-12 mt-4">
          <div class="p-3 bg-light border rounded">
            <p class="text-muted small fw-bold mb-2"><i class="bi bi-calculator me-1"></i>系統試算結果</p>
            <div class="row g-2">
              <div class="col-md-4">
                <label class="form-label text-muted" style="font-size: 11px;">試算利率</label>
                <input id="rate" class="form-control form-control-sm readonly-field text-center" readonly>
              </div>
              <div class="col-md-4">
                <label class="form-label text-muted" style="font-size: 11px;">估計總還款額</label>
                <input id="total" class="form-control form-control-sm readonly-field text-end" readonly>
              </div>
              <div class="col-md-4">
                <label class="form-label text-muted" style="font-size: 11px;">每期應繳金額</label>
                <input id="avg" class="form-control form-control-sm readonly-field text-end text-danger fw-bold" readonly>
              </div>
            </div>
          </div>
        </div>

        <div class="col-12 mt-4 d-flex align-items-center gap-2">
          <button type="button" class="btn btn-outline-info me-auto" onclick="fillDemoData()">
            <i class="bi bi-magic me-1"></i>一鍵帶入
          </button>
          <button type="button" class="btn btn-light" onclick="window.location.reload()">重新填寫</button>
          <button type="submit" class="btn btn-primary px-4"><i class="bi bi-send me-1"></i>送出申請</button>
        </div>
      </form>
    </div>
  </div>
</div>

<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script>
  // ==========================================
  // 1. 一鍵帶入假資料 (Demo 魔法按鈕)
  // ==========================================
  function fillDemoData() {
    // 隨機產生客戶 ID (例如 C00001 ~ C00100 之間)
    const randCustId = 'C' + Math.floor(Math.random() * 100 + 1).toString().padStart(5, '0');
    $('#customerId').val(randCustId);

    // 隨機選擇貸款種類 (排除比較特殊的，選這三個最常見的)
    const loanTypes = ['PERSONAL', 'CAR', 'HOUSE'];
    const randomType = loanTypes[Math.floor(Math.random() * loanTypes.length)];

    // 設定貸款種類，並「主動觸發 change 事件」，讓期數的下拉選單生出來！
    $('#type').val(randomType).trigger('change');

    // 取得剛生出來的期數選單中，所有合法的期數 (排除第一個空選項)
    const availableTerms = $('#term option').map(function() { return $(this).val(); }).get().filter(v => v !== "");
    const randomTerm = availableTerms[Math.floor(Math.random() * availableTerms.length)];
    $('#term').val(randomTerm);

    // 隨機產生金額 (例如 30萬 ~ 500萬)
    const randomAmount = Math.floor(Math.random() * 470 + 30) * 10000;

    // 設定金額，並「主動觸發 input 事件」，讓下方的利率與金額開始試算！
    $('#amount').val(randomAmount).trigger('input');
  }

  // ==========================================
  // 2. 原有的連動與試算邏輯
  // ==========================================
  $(function() {
    // 貸款種類 → 期數邏輯不變
    $("#type").change(function () {
      var plan = $(this).val();
      var term = $("#term");
      term.html('<option value="">請選擇分期期數</option>');
      $('#rate').val(''); $('#total').val(''); $('#avg').val('');

      var options = {
        PERSONAL: [12, 24, 36, 48, 60],
        CAR: [12, 24, 36, 48, 60],
        MOTOR: [12, 24, 36],
        STUDENT: [60, 84, 120],
        BUSINESS: [36, 60, 84],
        HOUSE: [120, 240, 360, 480],
        LAND: [120, 180, 240]
      };
      if (options[plan]) {
        options[plan].forEach(function (v) {
          term.append('<option value="' + v + '">' + v + '期</option>');
        });
      }
    });

    // 計算利率與金額邏輯 (加上千分位顯示優化)
    $("#term, #amount").on('input change', function () {
      var plan = $("#type").val();
      var term = parseInt($("#term").val());
      var amount = parseFloat($("#amount").val());

      if (!plan || isNaN(term) || isNaN(amount) || amount <= 0) {
        $('#rate').val(''); $('#total').val(''); $('#avg').val('');
        return;
      }

      var baseRate = { PERSONAL: 0.04, CAR: 0.025, MOTOR: 0.045, STUDENT: 0.015, BUSINESS: 0.02, HOUSE: 0.018, LAND: 0.028 };
      var termRate = { 12: 0, 24: 0.002, 36: 0.005, 48: 0.008, 60: 0.01, 84: 0.015, 120: 0, 180: 0.002, 240: 0.004, 360: 0.006, 480: 0.008 };

      var rate = (plan == "STUDENT") ? 0.015 : (baseRate[plan] + (termRate[term] || 0));

      $('#rate').val((rate * 100).toFixed(2) + " %");
      var total = amount * (1 + rate * term / 12);
      var avg = total / term;

      // 加上 toLocaleString 變成有逗號的千分位格式
      $("#total").val("$ " + total.toFixed(0).replace(/\B(?=(\d{3})+(?!\d))/g, ","));
      $("#avg").val("$ " + avg.toFixed(0).replace(/\B(?=(\d{3})+(?!\d))/g, ","));
    });

    // 送出申請
    $("#loanForm").submit(function(e){
      e.preventDefault();
      var customerId = $("#customerId").val().trim();
      var amount = parseFloat($("#amount").val());
      var type = $("#type").val();
      var term = $("#term").val();

      if(!customerId || !type || isNaN(amount) || amount <= 0 || !term) {
        Swal.fire({ icon: 'warning', title: '資料不齊全', text: '請確實填寫所有欄位', confirmButtonColor: '#1a1d23' });
        return;
      }

      var postData = {
        customerId: customerId,
        applyType: type,
        applyAmount: Math.floor(amount),
        applyPeriod: term
      };

      // 送出時改變按鈕狀態，增加專業感
      var submitBtn = $(this).find('button[type="submit"]');
      submitBtn.prop('disabled', true).html('<span class="spinner-border spinner-border-sm me-2"></span>傳送中...');

      $.ajax({
        url: "${pageContext.request.contextPath}/LoanApply",
        type: "POST",
        data: postData,
        success: function(res){
          Swal.fire({
            icon: 'success',
            title: '申請成功！',
            text: '您的貸款申請已送出，請靜候專員審核。',
            confirmButtonColor: '#1a1d23'
          }).then(function(){
            location.reload();
          });
        },
        error: function(xhr, status, error){
          Swal.fire({
            icon: 'error',
            title: '申請失敗',
            text: xhr.responseText || '系統發生錯誤',
            confirmButtonColor: '#d33'
          });
          submitBtn.prop('disabled', false).html('<i class="bi bi-send me-1"></i>送出申請');
        }
      });
    });
  });
</script>
</body>
</html>