package com.ispan.bankmanagement.card.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

import com.ispan.bankmanagement.card.vo.CreditCardTransaction;
import com.ispan.bankmanagement.util.ConnUtil;
import com.ispan.bankmanagement.card.dao.CreditCardTransactionDao;


public class TransactionService {
	
	private CreditCardTransactionDao dao=new CreditCardTransactionDao();
	
	public boolean refund(int originalTxnId, BigDecimal refundAmount) {
		try (Connection conn = ConnUtil.getConn()) {
			try {
				conn.setAutoCommit(false);
				//查原交易
				CreditCardTransaction original = dao.getById(conn, originalTxnId);

				if (original == null) {
				    conn.rollback();
				    return false;
				}

				//只能退 PURCHASE
				if (!"PURCHASE".equals(original.getTxnType())) {
				    conn.rollback();
				    throw new RuntimeException("只能退款消費交易");
				}

				//查已退款金額
				BigDecimal refunded = dao.getRefundedAmount(conn, originalTxnId);

				//防超額退款
				if (refunded.add(refundAmount).compareTo(original.getTxnAmount()) > 0) {
				    conn.rollback();
				    return false;
				}

				//建退款交易
				CreditCardTransaction refundTxn = new CreditCardTransaction();
				refundTxn.setCardId(original.getCardId());
				refundTxn.setMerchantId(original.getMerchantId());
				refundTxn.setTxnAmount(refundAmount.negate());
				refundTxn.setTxnType("REFUND");
				refundTxn.setRefTxnId(originalTxnId);
				refundTxn.setDescription("Refund txn=" + originalTxnId);

				//存入
				dao.insert(conn, refundTxn);

				conn.commit();
				return true;
			} catch (Exception e) {
				conn.rollback();
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;		
	}
}
