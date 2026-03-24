<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>貸款申請</title>
  <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
  <style>
    .container{
      width:320px;
      margin:auto;
    }
    .container h2{
      text-align:center;
    }
    .form-row{
      margin:12px 0;
    }
    label{
      display:inline-block;
      width:90px;
    }
    input, select{
      width:180px;
    }
    button{
      padding:6px 15px;
    }
  </style>
</head>
<body>
<div class="container">

  <h2>貸款申請</h2>
  <form id="loanForm">

    <div class="form-row">
      <label>客戶ID</label>
      <input name="customerId" id="customerId" required>
    </div>

    <div class="form-row">
      <label>貸款方案</label>
      <select name="applyType" id="type" required>
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

    <div class="form-row">
      <label>貸款金額</label>
      <input name="applyAmount" id="amount" type="number" min="1000" required>
    </div>

    <div class="form-row">
      <label>期數</label>
      <select name="applyPeriod" id="term" required>
        <option value="">請選擇分期期數</option>
      </select>
    </div>

    <div class="form-row">
      <label>利率</label>
      <input id="rate" readonly>
    </div>

    <div class="form-row">
      <label>估計總金額</label>
      <input id="total" readonly>
    </div>

    <div class="form-row">
      <label>每期應繳金額</label>
      <input id="avg" readonly>
    </div>

    <div class="form-row" style="text-align:center;">
      <button type="submit">送出申請</button>
    </div>

  </form>
</div>

<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>

<script>
  $(function() {
    // ===============================
    // ⭐ 貸款種類 → 期數
    // ===============================
    $("#type").change(function () {

      var plan = $(this).val();
      var term = $("#term");

      term.html('<option value="">請選擇分期期數</option>');

      // 清空利率與金額
      $('#rate').val('');
      $('#total').val('');
      $('#avg').val('');

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

    // ===============================
    // ⭐ 計算利率 / 金額
    // ===============================
    $("#term").change(function () {
      var plan = $("#type").val();
      var term = parseInt($("#term").val());
      var amount = parseFloat($("#amount").val());
      if (!amount || amount <= 0) {
        alert("請先輸入正確金額");
        return;
      }

      var rate;

      var baseRate = {
        PERSONAL: 0.04,
        CAR: 0.025,
        MOTOR: 0.045,
        STUDENT: 0.015,
        BUSINESS: 0.02,
        HOUSE: 0.018,
        LAND: 0.028
      };
      var termRate = {
        12: 0,
        24: 0.002,
        36: 0.005,
        48: 0.008,
        60: 0.01,
        84: 0.015,

        120: 0,
        180: 0.002,
        240: 0.004,
        360: 0.006,
        480: 0.008
      };

      if (plan == "STUDENT") {
        rate = 0.015;
      } else {
        rate = baseRate[plan] + termRate[term];
      }

      //顯示利率
      $('#rate').val((rate * 100).toFixed(2) + "%");

      //顯示總金額&每期應繳金額
      var total = amount * (1 + rate * term / 12);
      var avg = total / term;

      $("#total").val(total.toFixed(0));
      $("#avg").val(avg.toFixed(0));
    });

    // ===============================
    // ⭐ 送出申請（AJAX）
    // ===============================
    $("#loanForm").submit(function(e){

      e.preventDefault();

      var customerId = $("#customerId").val().trim();
      var amount = parseFloat($("#amount").val());
      var type = $("#type").val();
      var term = $("#term").val();

      if(!customerId){
        alert("請輸入客戶ID");
        return;
      }

      if(!type){
        alert("請選擇貸款種類");
        return;
      }

      if(isNaN(amount) || amount <= 0){
        alert("貸款金額需為有效數字且大於0");
        return;
      }

      if(!term){
        alert("請選擇期數");
        return;
      }

      // 組裝資料（明確指定每個欄位，避免 serialize 問題）
      var postData = {
        customerId: customerId,
        applyType: type,
        applyAmount: Math.floor(amount),
        applyPeriod: term
      };

      console.log("送出資料：", postData);

      $.ajax({
        url: "${pageContext.request.contextPath}/LoanApply",
        type: "POST",
        data: postData,

        success: function(res){
          console.log("伺服器回應：", res);
          Swal.fire({
            icon: 'success',
            title: '申請成功！',
            text: '您的貸款申請已送出'
          }).then(function(){
            location.reload();
          });
        },

        error: function(xhr, status, error){
          console.log("HTTP狀態碼：", xhr.status);
          console.log("錯誤狀態：", status);
          console.log("錯誤訊息：", error);
          console.log("回應內容：", xhr.responseText);
          Swal.fire({
            icon: 'error',
            title: '申請失敗（' + xhr.status + '）',
            html: '<p>請開啟 F12 Console 查看詳細錯誤</p>' +
                    '<pre style="text-align:left;font-size:12px;max-height:200px;overflow:auto;">' +
                    (xhr.responseText || '無回應內容') + '</pre>'
          });
        }
      });

    });

  });

</script>

</body>
</html>
