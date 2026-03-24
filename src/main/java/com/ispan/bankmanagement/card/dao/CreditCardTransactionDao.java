package com.ispan.bankmanagement.card.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.ispan.bankmanagement.card.vo.CreditCardTransaction;
import com.ispan.bankmanagement.util.ConnUtil;


public class CreditCardTransactionDao {
	//新增交易(內部用)
	private static final int SYSTEM_MERCHANT_ID = 11;// 系統預設商戶（用於繳款等內部交易
	public void insert(Connection conn,CreditCardTransaction tx) {
		String sql ="INSERT INTO CREDIT_CARD_TRANSACTION "
                + "(card_id, merchant_id, txn_amount, txn_type, description, ref_txn_id) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
		try (
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, tx.getCardId());
			if (tx.getMerchantId() != null) {
				ps.setInt(2, tx.getMerchantId());
				
			}else {
				ps.setNull(2, Types.INTEGER);
			}
			ps.setBigDecimal(3, tx.getTxnAmount());
            ps.setString(4, tx.getTxnType());
            ps.setString(5, tx.getDescription());

            if (tx.getRefTxnId() != null) {
                ps.setInt(6, tx.getRefTxnId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//新增交易end
	//刷卡
	public void purchase(CreditCardTransaction txn) {
		
		if (txn.getTxnAmount().compareTo(BigDecimal.ZERO)<=0) {
			throw new IllegalArgumentException("消費金額必須大於0");
		}
		txn.setTxnType("PURCHASE");
		try (Connection conn = ConnUtil.getConn()) {
			insert(conn,txn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}//刷卡end
	//繳款(轉負數)
	public void payment(CreditCardTransaction txn) {
		if (txn.getTxnAmount().compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("繳款金額必須大於0");
		}
		txn.setTxnType("PAYMENT");
        txn.setTxnAmount(txn.getTxnAmount().negate());
		
        try (Connection conn = ConnUtil.getConn()) {
        	insert(conn, txn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}//繳款end
	//退款
//	public boolean refund(int originalTxnId, BigDecimal refundAmount) {
//		String getOriginalSql = "SELECT * FROM CreditCardTransactions WHERE txn_id = ?";
//		String sumRefundSql = "SELECT ISNULL(SUM(txn_amount),0) FROM CreditCardTransactions WHERE ref_txn_id = ?";
//		try (Connection conn = DButil.getConnection()) {
//			conn.setAutoCommit(false);
//			//查原交易
//			try {
//				PreparedStatement ps1=conn.prepareStatement(getOriginalSql);
//				ps1.setInt(1, originalTxnId);
//				ResultSet rs=ps1.executeQuery();
//				if (!rs.next()) {
//				    conn.rollback();
//				    return false;
//				}
//				BigDecimal originalAmount=rs.getBigDecimal("txn_amount");
//				int cardId = rs.getInt("card_id");
//				Integer merchantId = (Integer) rs.getObject("merchant_id");
//				String type = rs.getString("txn_type");
//				//只能退消費
//				if (!"PURCHASE".equals(type)) {
//				    conn.rollback();
//				    throw new RuntimeException("只能退款消費交易");
//				}
//				PreparedStatement ps2 = conn.prepareStatement(sumRefundSql);
//				ps2.setInt(1, originalTxnId);
//				ResultSet rs2 = ps2.executeQuery();
//				
//				BigDecimal refunded=BigDecimal.ZERO;
//				if (rs2.next()) {
//				    refunded = rs2.getBigDecimal(1).abs();
//				}
//				//防超額退款
//				if (refunded.add(refundAmount).compareTo(originalAmount) > 0) {
//				    conn.rollback();
//				    return false;
//				}
//				//建退款交易
//				CreditCardTransaction refundTxn = new CreditCardTransaction();
//				refundTxn.setCardId(cardId);
//				refundTxn.setMerchantId(merchantId);
//				refundTxn.setTxnAmount(refundAmount.negate());
//				refundTxn.setTxnType("REFUND");
//				refundTxn.setRefTxnId(originalTxnId);
//				refundTxn.setDescription("Refund txn=" + originalTxnId);
//				
//				insert(conn, refundTxn);
//				conn.commit();//成功才提交
//				return true;
//			} catch (Exception e) {
//				conn.rollback();//失敗退回
//				e.printStackTrace();
//			}            
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return false;		
//	}//退款end
	//查餘額
	public BigDecimal getBalance(int cardId) {
		String sql = "SELECT ISNULL(SUM(txn_amount),0) FROM CREDIT_CARD_TRANSACTION WHERE card_id=?";
		BigDecimal balance = BigDecimal.ZERO;
		try (Connection conn = ConnUtil.getConn();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, cardId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
				    balance = rs.getBigDecimal(1);
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return balance;		
	}//查餘額end
	//mapRow
	private CreditCardTransaction mapRow(ResultSet rs) throws SQLException {
		CreditCardTransaction txn = new CreditCardTransaction();
		txn.setTxnId(rs.getInt("txn_id"));
	    txn.setCardId(rs.getInt("card_id"));
	    txn.setMerchantId((Integer) rs.getObject("merchant_id"));

	    txn.setTxnAmount(rs.getBigDecimal("txn_amount"));
	    txn.setTxnType(rs.getString("txn_type"));
	    txn.setTxnDate(rs.getTimestamp("txn_date"));
	    txn.setDescription(rs.getString("description"));
	    txn.setRefTxnId((Integer) rs.getObject("ref_txn_id"));
		return txn;		
	}//mapRow end
	public List<CreditCardTransaction> getByCardId(int cardId) {
		List<CreditCardTransaction> list = new ArrayList<>();
		String sql = "SELECT * FROM CREDIT_CARD_TRANSACTION WHERE card_id=? ORDER BY txn_date DESC";
		try (Connection conn = ConnUtil.getConn();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, cardId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
	                list.add(mapRow(rs));
	            }
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	//查詢單筆
	public CreditCardTransaction getById(Connection conn, int txnId) throws SQLException {
	    String sql = "SELECT * FROM CREDIT_CARD_TRANSACTION WHERE txn_id = ?";
	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, txnId);
	        ResultSet rs = ps.executeQuery();
	        if (rs.next()) {
	            return mapRow(rs);
	        }
	    }
	    return null;
	}
	//查已退款總額
	public BigDecimal getRefundedAmount(Connection conn, int txnId) throws SQLException {
	    String sql = "SELECT ISNULL(SUM(txn_amount),0) FROM CREDIT_CARD_TRANSACTION WHERE ref_txn_id = ?";
	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, txnId);
	        ResultSet rs = ps.executeQuery();
	        if (rs.next()) {
	            return rs.getBigDecimal(1).abs();
	        }
	    }
	    return BigDecimal.ZERO;
	}
	
	public boolean insertPaymentTransaction(Connection conn, int cardId, BigDecimal amount) throws SQLException {

	    String sql = "INSERT INTO CREDIT_CARD_TRANSACTION " +
	                 "(card_id, merchant_id, txn_amount, txn_type, txn_date, description) " +
	                 "VALUES (?, ?, ?, ?, GETDATE(), ?)";

	    try (PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setInt(1, cardId);
	        ps.setInt(2, SYSTEM_MERCHANT_ID);
	        ps.setBigDecimal(3, amount.negate());
	        ps.setString(4, "PAYMENT");
	        ps.setString(5, "Credit card payment");

	        return ps.executeUpdate() > 0;
	    }
	}
	//
	public List<CreditCardTransaction> getByCardIdAndDateRange(int cardId, Date startDate,Date endDate) {
		String sql = "SELECT * FROM CREDIT_CARD_TRANSACTION "
				+ "   WHERE card_id = ?  "
//				+"AND customer_id = ? "
				+ "  AND txn_date >= ?  "
				+ "  AND txn_date < ? "
				+ " ORDER BY txn_date DESC";
		
		List<CreditCardTransaction>list=new ArrayList<CreditCardTransaction>();
		try (Connection conn = ConnUtil.getConn();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, cardId);
	        ps.setDate(2, startDate);
	        ps.setDate(3, endDate);
	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                list.add(mapRow(rs));
	            }
	        }
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
		
	}
	public List<CreditCardTransaction> getByDateRange(int customerId ,Date startDate, Date endDate) {

	    String sql = "SELECT t.* FROM CREDIT_CARD_TRANSACTION t " +
	    		"JOIN CREDIT_CARD c ON t.card_id = c.card_id "+
	    		"WHERE c.customer_id = ? "+
	                 "AND t.txn_date >= ? " +
	                 "AND t.txn_date < ? " +
	                 "ORDER BY t.txn_date DESC";

	    List<CreditCardTransaction> list = new ArrayList<>();

	    try (Connection conn = ConnUtil.getConn();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	    	ps.setInt(1, customerId);
	        ps.setDate(2, startDate);
	        ps.setDate(3, endDate);

	        ResultSet rs = ps.executeQuery();

	        while (rs.next()) {
	            list.add(mapRow(rs));
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return list;
	}
	
	
	
	
	
	
}
