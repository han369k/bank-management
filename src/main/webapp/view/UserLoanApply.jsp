<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-TW">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>貸款申請</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
  <link rel="stylesheet" href="../css/style.css">
  <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>

<nav class="navbar navbar-expand-lg">
  <div class="container-fluid px-4">
    <a class="navbar-brand" href="../index.html"><i class="bi bi-bank me-2"></i>Bank Management</a>
    <div class="navbar-nav ms-3">
      <a class="nav-link" href="account-list.html">帳戶</a>
      <a class="nav-link active" href="UserLoanApply.jsp">申請貸款</a>
      <a class="nav-link" href="translog-search.html">交易紀錄</a>
    </div>
  </div>
</nav>

<div class="page-wrapper">
  <div class="page-title">貸款申請</div>

  <div class="card" style="max-width: 500px;">
    <div class="card-header">填寫申請資料</div>
    <div class="card-body">
      <form id="loanForm" class="row g-3">
        <div class="col-12">
          <label class="form-label">客戶 ID</label>
          <input name="customerId" id="customerId" class="form-control" placeholder="請輸入客戶身分識別碼" required>
        </div>

        <div class="col-md-6">
          <label class="form-label">貸款方案</label>
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
          <label class="form-label">期數</label>
          <select name="applyPeriod" id="term" class="form-select" required>
            <option value="">請先選擇貸款種類</option>
          </select>
        </div>

        <div class="col-12">
          <label class="form-label">貸款金額</label>
          <input name="applyAmount" id="amount" type="number" class="form-control" min="1000" placeholder="最低 1,000 元" required>
        </div>

        <hr class="mt-4 mb-2">

        <div class="col-md-4">
          <label class="form-label text-muted">試算利率</label>
          <input id="rate" class="form-control" readonly style="background-color: #f8f9fa;">
        </div>
        <div class="col-md-4">
          <label class="form-label text-muted">估計總金額</label>
          <input id="total" class="form-control" readonly style="background-color: #f8f9fa;">
        </div>
        <div class="col-md-4">
          <label class="form-label text-muted">每期應繳</label>
          <input id="avg" class="form-control text-primary fw-bold" readonly style="background-color: #f8f9fa;">
        </div>

        <div class="col-12 mt-4">
          <button type="submit" class="btn btn-primary w-100"><i class="bi bi-send me-1"></i> 送出申請</button>
        </div>
      </form>
    </div>
  </div>
</div>

<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script>
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

    // 計算利率與金額邏輯不變
    $("#term, #amount").on('input change', function () {
      var plan = $("#type").val();
      var term = parseInt($("#term").val());
      var amount = parseFloat($("#amount").val());

      if (!plan || isNaN(term) || isNaN(amount) || amount <= 0) return;

      var baseRate = { PERSONAL: 0.04, CAR: 0.025, MOTOR: 0.045, STUDENT: 0.015, BUSINESS: 0.02, HOUSE: 0.018, LAND: 0.028 };
      var termRate = { 12: 0, 24: 0.002, 36: 0.005, 48: 0.008, 60: 0.01, 84: 0.015, 120: 0, 180: 0.002, 240: 0.004, 360: 0.006, 480: 0.008 };

      var rate = (plan == "STUDENT") ? 0.015 : (baseRate[plan] + termRate[term]);

      $('#rate').val((rate * 100).toFixed(2) + "%");
      var total = amount * (1 + rate * term / 12);
      var avg = total / term;

      $("#total").val(total.toFixed(0));
      $("#avg").val(avg.toFixed(0));
    });

    // 送出申請
    $("#loanForm").submit(function(e){
      e.preventDefault();
      var customerId = $("#customerId").val().trim();
      var amount = parseFloat($("#amount").val());
      var type = $("#type").val();
      var term = $("#term").val();

      if(!customerId || !type || isNaN(amount) || amount <= 0 || !term) {
        Swal.fire({ icon: 'warning', title: '資料不齊全', text: '請確實填寫所有欄位' });
        return;
      }

      var postData = {
        customerId: customerId,
        applyType: type,
        applyAmount: Math.floor(amount),
        applyPeriod: term
      };

      $.ajax({
        url: "${pageContext.request.contextPath}/LoanApply",
        type: "POST",
        data: postData,
        success: function(res){
          Swal.fire({ icon: 'success', title: '申請成功！', text: '您的貸款申請已送出' }).then(function(){
            location.reload();
          });
        },
        error: function(xhr, status, error){
          Swal.fire({
            icon: 'error',
            title: '申請失敗',
            text: xhr.responseText || '系統發生錯誤'
          });
        }
      });
    });
  });
</script>
</body>
</html>