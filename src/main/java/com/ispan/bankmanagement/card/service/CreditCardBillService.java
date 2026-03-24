package com.ispan.bankmanagement.card.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ispan.bankmanagement.card.vo.CreditCard;
import com.ispan.bankmanagement.card.vo.CreditCardBill;
import com.ispan.bankmanagement.card.vo.CreditCardTransaction;
import com.ispan.bankmanagement.card.vo.Enum.BillStatus;
import com.ispan.bankmanagement.util.ConnUtil;
import com.ispan.bankmanagement.card.dao.CreditCardBillDao;
import com.ispan.bankmanagement.card.dao.CreditCardDao;
import com.ispan.bankmanagement.card.dao.CreditCardTransactionDao;
import com.ispan.bankmanagement.card.dto.MonthlyBill;


public class CreditCardBillService {
	private CreditCardBillDao billDao=new CreditCardBillDao();
	private CreditCardDao cardDao=new CreditCardDao();
	private CreditCardTransactionDao transactionDao = new CreditCardTransactionDao();
	
	
	public CreditCardBill getBillById(int billId) {
        return billDao.getBillById(billId);
    }

    public List<CreditCardBill> getAllBills() {
        return billDao.getAllBills();
    }

    public List<CreditCardTransaction> getBillDetails(CreditCardBill bill) {
    	LocalDate start = LocalDate.parse(bill.getBillingMonth() + "-01");
        LocalDate end = start.plusMonths(1);
        
        int customerId = getCustomerIdByCardId(bill.getCardId());
        List<CreditCard> cards = cardDao.getCardsByCustomerId(customerId);
        List<CreditCardTransaction> all = new ArrayList<>();
    	
        for (CreditCard card : cards) {
            all.addAll(
                transactionDao.getByCardIdAndDateRange(
                    card.getCardId(),
                     Date.valueOf(start),
                    Date.valueOf(end)
                )
            );
        }

        return all;
    }
	
	//付款
	public boolean payBill(int billId, BigDecimal paymentAmount) {
		Connection conn=null;
		try  {
			
			conn = ConnUtil.getConn();
			//開啟 transaction
			conn.setAutoCommit(false);
			//查帳單
			CreditCardBill bill = billDao.getBillById(billId);
			if (bill == null) {
                System.out.println("The bill is null");
                return false;
            }
			// 2️ 更新帳單
            BigDecimal newPaid = bill.getPaidAmount().add(paymentAmount);
            BigDecimal total = bill.getTotalAmount();

            // 防止超繳
            if (newPaid.compareTo(total) > 0) {
                newPaid = total;
            }

            bill.setPaidAmount(newPaid);

            if (newPaid.compareTo(BigDecimal.ZERO) == 0) {
                bill.setBillStatus(BillStatus.UNPAID);
            } else if (newPaid.compareTo(total) < 0) {
                bill.setBillStatus(BillStatus.PARTIAL);
            } else {
                bill.setBillStatus(BillStatus.PAID);
            }

            boolean billUpdated = billDao.updateBill(conn,bill);

            //更新信用卡餘額（扣掉已繳）
            CreditCard card = cardDao.getCreditCardById(bill.getCardId()); // ⚠️ 可再優化
            BigDecimal currentBalance = card.getCurrentBalance();
            BigDecimal newBalance = currentBalance.subtract(paymentAmount);

            boolean cardUpdated = cardDao.updateBalance(conn, bill.getCardId(), newBalance);

            //記錄交易
            boolean txnInserted = transactionDao.insertPaymentTransaction(conn,bill.getCardId(),paymentAmount);

            //全成功才 commit
            if (billUpdated && cardUpdated && txnInserted) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
            }
		} catch (SQLException e) {
			if (conn != null)
				try {
					conn.rollback();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				};
		}
		return false;
	}

	public List<CreditCardTransaction> getBillDetailsByCard(CreditCardBill bill, int cardId) {
		LocalDate start = LocalDate.parse(bill.getBillingMonth() + "-01");
	    LocalDate end = start.plusMonths(1);
//	    int customerId=getCustomerIdByCardId(cardId);
	    return transactionDao.getByCardIdAndDateRange(
	            cardId,
	             Date.valueOf(start),
	            Date.valueOf(end)
	    );
	}
	public List<CreditCardTransaction> getBillDetailsByMonth(String month,int customerId) {

	    LocalDate start = LocalDate.parse(month + "-01");
	    LocalDate end = start.plusMonths(1);

	    return transactionDao.getByDateRange(
	    		customerId,
	    		Date.valueOf(start),
	            Date.valueOf(end)
	    );
	}
	

	public List<CreditCard> getCardsByCustomerId(int customerId) {
	    return cardDao.getCardsByCustomerId(customerId);
	}
	
	
	private CreditCardDao creditCardDao = new CreditCardDao();

	public int getCustomerIdByCardId(int cardId) {
	    CreditCard card = creditCardDao.getCreditCardById(cardId);

	    if (card == null) {
	        throw new RuntimeException("Not Found the card!");
	    }

	    return card.getCustomerId();
	}
	//DTO方法
	public List<MonthlyBill> getMonthlyBills() {

	    List<CreditCardBill> bills = billDao.getAllBills();

	    Map<String, MonthlyBill> map = new HashMap<>();

	    for (CreditCardBill bill : bills) {

	        String month = bill.getBillingMonth();

	        MonthlyBill mb = map.get(month);

	        if (mb == null) {
	            mb = new MonthlyBill();
	            mb.setBillingMonth(month);
	            mb.setTotalAmount(BigDecimal.ZERO);
	            mb.setPaidAmount(BigDecimal.ZERO);
	        }

	        // 累加金額
	        mb.setTotalAmount(
	            mb.getTotalAmount().add(bill.getTotalAmount())
	        );

	        mb.setPaidAmount(
	            mb.getPaidAmount().add(bill.getPaidAmount())
	        );

	        map.put(month, mb);
	    }

	    return new ArrayList<>(map.values());
	}
	public List<CreditCard> getCardsByMonth(String month) {

	    LocalDate start = LocalDate.parse(month + "-01");
	    LocalDate end = start.plusMonths(1);

	    return cardDao.getCardsByDateRange(
	            Date.valueOf(start),
	            Date.valueOf(end)
	    );
	}
	public List<CreditCardTransaction> getBillDetailsByMonthAndCard(String month, int cardId, int customerId) {

	    LocalDate start = LocalDate.parse(month + "-01");
	    LocalDate end = start.plusMonths(1);

	    return transactionDao.getByCardIdAndDateRange(
	    		cardId,
//	            customerId,
	            Date.valueOf(start),
	            Date.valueOf(end)
	    );
	}

	public List<MonthlyBill> getMonthlyBillsByCustomerId(int customerId) {
		// TODO Auto-generated method stub
		return billDao.getMonthlyBillsByCustomer(customerId);
	}
}
