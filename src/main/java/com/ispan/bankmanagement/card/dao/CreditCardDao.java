package com.ispan.bankmanagement.card.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ispan.bankmanagement.card.vo.CreditCard;
import com.ispan.bankmanagement.card.vo.Enum.CardStatus;
import com.ispan.bankmanagement.util.ConnUtil;


public class CreditCardDao {
	//新增卡片
	public void addCreditCard(CreditCard card) {
		String sql="INSERT INTO CREDIT_CARD "
				+ "  (customer_id, card_type_id, card_number, expiry_date, "
				+ " credit_limit, current_balance, create_date, status) "
				+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		try (Connection conn = ConnUtil.getConn();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, card.getCustomerId());
            ps.setInt(2, card.getCardTypeId());
            ps.setString(3, card.getCardNumber());
            ps.setDate(4, card.getExpiryDate());
            ps.setBigDecimal(5, card.getCreditLimit());
            ps.setBigDecimal(6, card.getCurrentBalance());
            ps.setTimestamp(7, card.getCreateDate());
            ps.setInt(8, card.getStatus().getValue());

            int rows= ps.executeUpdate();
            System.out.println("Add "+rows+"rows.");
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}//新增卡片end
	//查詢單筆
	public CreditCard getCreditCardById(int id) {
		CreditCard card=null;
		String sql = "SELECT * FROM CREDIT_CARD WHERE card_id=?";
		try (Connection conn = ConnUtil.getConn();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
				if(rs.next()) {
				    card = new CreditCard();
				    card.setCardId(rs.getInt("card_id"));
				    card.setCustomerId(rs.getInt("customer_id"));
				    card.setCardTypeId(rs.getInt("card_type_id"));
				    card.setCardNumber(rs.getString("card_number"));
				    card.setExpiryDate(rs.getDate("expiry_date"));
				    card.setCreditLimit(rs.getBigDecimal("credit_limit"));
				    card.setCurrentBalance(rs.getBigDecimal("current_balance"));
				    card.setCreateDate(rs.getTimestamp("create_date"));
				    card.setStatus(CardStatus.fromValue(rs.getInt("status")));
				}
			}          
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return card;
	}	
	//查詢全部
	public List<CreditCard> getAllCreditCards() {
		List<CreditCard>list=new ArrayList<CreditCard>();
		String sql = "SELECT * FROM CREDIT_CARD";
		try (Connection conn = ConnUtil.getConn();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while(rs.next()) {

                CreditCard card = new CreditCard();

                card.setCardId(rs.getInt("card_id"));
                card.setCustomerId(rs.getInt("customer_id"));
                card.setCardTypeId(rs.getInt("card_type_id"));
                card.setCardNumber(rs.getString("card_number"));
                card.setExpiryDate(rs.getDate("expiry_date"));
                card.setCreditLimit(rs.getBigDecimal("credit_limit"));
                card.setCurrentBalance(rs.getBigDecimal("current_balance"));
                card.setCreateDate(rs.getTimestamp("create_date"));
                card.setStatus(CardStatus.fromValue(rs.getInt("status")));

                list.add(card);
			}	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;		
	}//查詢全部end
	//透過customerId查卡片
	public List<CreditCard> getCardsByCustomerId(int customerId) {
	    List<CreditCard> list = new ArrayList<>();

	    String sql = "SELECT * FROM credit_card WHERE customer_id = ?";

	    try (Connection conn = ConnUtil.getConn();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setInt(1, customerId);
	        ResultSet rs = ps.executeQuery();

	        while (rs.next()) {
	            CreditCard card = new CreditCard();
	            card.setCardId(rs.getInt("card_id"));
	            card.setCardNumber(rs.getString("card_number"));
	            list.add(card);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return list;
	}
	
	//查卡片
	public List<CreditCard> getCardsByDateRange(Date start, Date end) {

	    String sql = "SELECT DISTINCT c.* " +
	                 "FROM CREDIT_CARD c " +
	                 "JOIN CREDIT_CARD_TRANSACTION t ON c.card_id = t.card_id " +
	                 "WHERE t.txn_date >= ? AND t.txn_date < ?";

	    List<CreditCard> list = new ArrayList<>();

	    try (Connection conn = ConnUtil.getConn();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setDate(1, start);
	        ps.setDate(2, end);

	        ResultSet rs = ps.executeQuery();

	        while (rs.next()) {
	            CreditCard card = new CreditCard();
	            card.setCardId(rs.getInt("card_id"));
	            card.setCardNumber(rs.getString("card_number"));
	            card.setCustomerId(rs.getInt("customer_id"));
	            // 其他欄位你看要不要補

	            list.add(card);
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return list;
	}
	
	//更新
	public void updateCreditCard(CreditCard card) {
		String sql="UPDATE CREDIT_CARD "
				+ "            SET customer_id=?, card_type_id=?, card_number=?, expiry_date=?,\r\n"
				+ "                credit_limit=?, current_balance=?, status=?\r\n"
				+ "            WHERE card_id=?";
		try (Connection conn = ConnUtil.getConn();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, card.getCustomerId());
            ps.setInt(2, card.getCardTypeId());
            ps.setString(3, card.getCardNumber());
            ps.setDate(4, card.getExpiryDate());
            ps.setBigDecimal(5, card.getCreditLimit());
            ps.setBigDecimal(6, card.getCurrentBalance());
            ps.setInt(7, card.getStatus().getValue());
            ps.setInt(8, card.getCardId());
            
            ps.executeUpdate();
            
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}//更新end
	//更新status
	public void updateStatus(int cardId, CardStatus status) {
	    String sql = "UPDATE credit_card SET status = ? WHERE card_id = ?";

	    try (Connection conn = ConnUtil.getConn();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setInt(1, status.getValue()); // Enum → String
	        ps.setInt(2, cardId);

	        ps.executeUpdate();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	//刪除
	public void deleteCreditCard(int id) {
		String sql = "DELETE FROM CREDIT_CARD WHERE card_id=?";
		try (Connection conn = ConnUtil.getConn();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
            ps.executeUpdate();			
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}//刪除end
	
	//更新信用卡餘額
	public boolean updateBalance(Connection conn, int cardId, BigDecimal newBalance) throws SQLException {
		// TODO Auto-generated method stub
		String sql = "UPDATE CREDIT_CARD SET current_balance=? WHERE card_id=?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setBigDecimal(1, newBalance);
	        ps.setInt(2, cardId);

	        return ps.executeUpdate() > 0;
	    }
	}



	
	
}

