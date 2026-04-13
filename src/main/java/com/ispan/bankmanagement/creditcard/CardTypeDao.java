package com.ispan.bankmanagement.creditcard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ispan.bankmanagement.common.util.ConnUtil;


public class CardTypeDao {
	//新增
	public void addCardType(CardTypes cardType) {
		String sql = "INSERT INTO CARD_TYPE (card_type_name, brand, annual_fee,cashback_rate) VALUES (?, ?, ?,?)";
		try (Connection conn = ConnUtil.getConn();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, cardType.getCardTypeName());
			ps.setString(2, cardType.getBrand());
			ps.setBigDecimal(3, cardType.getAnnualFee());
			ps.setBigDecimal(4, cardType.getCashbackRate());
			ps.executeUpdate();			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//新增結束
	//查全部
	public List<CardTypes> getAllCardTypes() {
		List<CardTypes> list=new ArrayList<CardTypes>();
		String sql = "SELECT * FROM CARD_TYPE";
		try (Connection conn = ConnUtil.getConn();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				CardTypes ct = new CardTypes();
				
				ct.setCardTypeId(rs.getInt("card_type_id"));
				ct.setCardTypeName(rs.getString("card_type_name"));
                ct.setBrand(rs.getString("brand"));
                ct.setAnnualFee(rs.getBigDecimal("annual_fee"));
                ct.setCashbackRate(rs.getBigDecimal("cashback_rate"));
                list.add(ct);
								
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
		
	}//查全部end
	//修改卡別
	public void updateCardType(CardTypes cardTypes) {
		String sql = "UPDATE CARD_TYPE SET card_type_name=?, brand=?, annual_fee=?,cashback_rate=?  WHERE card_type_id=?";
		try (Connection conn = ConnUtil.getConn();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, cardTypes.getCardTypeName());
            ps.setString(2, cardTypes.getBrand());
            ps.setBigDecimal(3, cardTypes.getAnnualFee());
            ps.setBigDecimal(4, cardTypes.getCashbackRate());            
            ps.setInt(5, cardTypes.getCardTypeId());
            ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}//修改卡別end
	//刪除卡別
	public void deleteCardType(int id) {
		String sql = "DELETE FROM CARD_TYPE WHERE card_type_id=?";
		try (Connection conn = ConnUtil.getConn();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
            ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}//刪除卡別end
	//
	
		
		
		
		
		
		
		
		
		
		
		
}
