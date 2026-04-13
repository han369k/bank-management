package com.ispan.bankmanagement.creditcard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ispan.bankmanagement.creditcard.Enum.BillStatus;
import com.ispan.bankmanagement.common.util.ConnUtil;
import com.ispan.bankmanagement.creditcard.dto.MonthlyBill;


public class CreditCardBillDao {
	//查詢單筆(卡號+月份)
	public CreditCardBill getBillByCardIdAndMonth(int cardId, String billMonth) {
		String sql = "SELECT * FROM CREDIT_CARD_BILL WHERE card_id=? AND billing_month=?";
		CreditCardBill bill = null;
		try (Connection conn = ConnUtil.getConn();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, cardId);
            ps.setString(2, billMonth);
            try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
				    bill = mapRowToBill(rs);
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bill;
		
	}
	//查詢某卡
	public List<CreditCardBill> getBillsByCardId(int cardId) {
		String sql = "SELECT * FROM CREDIT_CARD_BILL WHERE card_id=? ORDER BY billing_month DESC";
		List<CreditCardBill> list = new ArrayList<>();
		try (Connection conn = ConnUtil.getConn();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, cardId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
				    list.add(mapRowToBill(rs));
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	//新增帳單(出帳用)
	public boolean insertBill(CreditCardBill bill) {
		String sql = "INSERT INTO CREDIT_CARD_BILL " +
                "(card_id, billing_month, bill_date, due_date, total_amount, minimum_payment, paid_amount, bill_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		try (Connection conn = ConnUtil.getConn();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, bill.getCardId());
            ps.setString(2, bill.getBillingMonth());
            ps.setDate(3, bill.getBillDate());
            ps.setDate(4, bill.getDueDate());
            ps.setBigDecimal(5, bill.getTotalAmount());
            ps.setBigDecimal(6, bill.getMinimumPayment());
            ps.setBigDecimal(7, bill.getPaidAmount());
            ps.setString(8, bill.getBillStatus().name());
			
            return ps.executeUpdate() > 0;
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	//更新帳單（付款用）
	public boolean updateBill(CreditCardBill bill) {
        String sql = "UPDATE CREDIT_CARD_BILL SET " +
                "paid_amount=?, bill_status=? " +
                "WHERE bill_id=?";

        try (Connection conn = ConnUtil.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBigDecimal(1, bill.getPaidAmount());
            ps.setString(2, bill.getBillStatus().name());
            ps.setInt(3, bill.getBillId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
	
	//overload 更新帳單（付款用）
	public boolean updateBill(Connection conn, CreditCardBill bill) throws SQLException {
	    String sql = "UPDATE CREDIT_CARD_BILL SET " +
	            "paid_amount=?, bill_status=? " +
	            "WHERE bill_id=?";

	    try (PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setBigDecimal(1, bill.getPaidAmount());
	        ps.setString(2, bill.getBillStatus().name());
	        ps.setInt(3, bill.getBillId());

	        return ps.executeUpdate() > 0;
	    }
	}
	
	//查單筆(bill_id)
	public CreditCardBill getBillById(int billId) {
	    String sql = "SELECT * FROM CREDIT_CARD_BILL WHERE bill_id=?";
	    CreditCardBill bill = null;

	    try (Connection conn = ConnUtil.getConn();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setInt(1, billId);

	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                bill = mapRowToBill(rs);
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return bill;
	}
	
	//查詢全部
	public List<CreditCardBill>getAllBills() {
		String sql = "SELECT * FROM CREDIT_CARD_BILL ORDER BY billing_month DESC";
		List<CreditCardBill>list=new ArrayList<CreditCardBill>();
		try (Connection conn = ConnUtil.getConn();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				list.add(mapRowToBill(rs));
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
		
		
	}
	
	
	// 刪除(保留但不建議用)
    public boolean deleteBill(int billId) {
        String sql = "DELETE FROM CREDIT_CARD_BILL WHERE bill_id=?";

        try (Connection conn = ConnUtil.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, billId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
	
	//Mapping（DB → Java）
	private CreditCardBill mapRowToBill(ResultSet rs) throws SQLException {
		CreditCardBill bill = new CreditCardBill();

        bill.setBillId(rs.getInt("bill_id"));
        bill.setCardId(rs.getInt("card_id"));
        bill.setBillingMonth(rs.getString("billing_month"));
        bill.setBillDate(rs.getDate("bill_date"));
        bill.setDueDate(rs.getDate("due_date"));
        bill.setTotalAmount(rs.getBigDecimal("total_amount"));
        bill.setMinimumPayment(rs.getBigDecimal("minimum_payment"));
        bill.setPaidAmount(rs.getBigDecimal("paid_amount"));
        //String->Enum
        String statusStr=rs.getString("bill_status");
        if (statusStr != null) {
        	bill.setBillStatus(BillStatus.valueOf(statusStr.toUpperCase()));			
		}		
		return bill;
	}
	//
	public List<MonthlyBill> getMonthlyBillsByCustomer(int customerId) {
		// TODO Auto-generated method stub
		String sql="SELECT b.billing_month, SUM(b.total_amount) AS total_amount, SUM(b.paid_amount) AS paid_amount FROM CREDIT_CARD_BILL b JOIN CREDIT_CARD c ON b.card_id = c.card_id WHERE c.customer_id = ? GROUP BY b.billing_month ORDER BY b.billing_month DESC";
		List<MonthlyBill>list=new ArrayList<MonthlyBill>();
		try (Connection conn = ConnUtil.getConn();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, customerId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
				    MonthlyBill mb = new MonthlyBill();

				    mb.setBillingMonth(rs.getString("billing_month"));
				    mb.setTotalAmount(rs.getBigDecimal("total_amount"));
				    mb.setPaidAmount(rs.getBigDecimal("paid_amount"));

				    list.add(mb);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return list;
	}
	
}
