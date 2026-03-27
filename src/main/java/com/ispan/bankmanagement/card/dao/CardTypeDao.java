package com.ispan.bankmanagement.card.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ispan.bankmanagement.card.vo.CardTypes;
import com.ispan.bankmanagement.util.ConnUtil;


public class CardTypeDao {
	//新增
	public void addCardType(CardTypes cardType) {
		String sql = "INSERT INTO CARD_TYPE (card_type_name, brand, annual_fee,cashback_rate,card_image_url) VALUES (?, ?, ?,?)";
		try (Connection conn = ConnUtil.getConn();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, cardType.getCardTypeName());
			ps.setString(2, cardType.getBrand());
			ps.setBigDecimal(3, cardType.getAnnualFee());
			ps.setBigDecimal(4, cardType.getCashbackRate());
			ps.setString(5, cardType.getCardImageUrl());
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
                ct.setCardImageUrl(rs.getString("card_image_url"));
                list.add(ct);								
			}
		} catch (SQLException e) {
			throw new RuntimeException("查詢 CardTypes 失敗", e);
		}
		return list;		
	}//查全部end
	
	//查詢單筆
	public CardTypes getCardTypeById(int cardTypeId) {
		String sql = """
	            SELECT card_type_id, card_type_name, brand, annual_fee, cashback_rate
	            FROM CARD_TYPE
	            WHERE card_type_id = ?
	            """;

	    try (Connection conn = ConnUtil.getConn();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        // 設定參數
	        ps.setInt(1, cardTypeId);

	        try (ResultSet rs = ps.executeQuery()) {

	            // ❗ 如果查不到資料 → 直接丟例外
	            if (!rs.next()) {
	                throw new RuntimeException("查無此卡種，ID: " + cardTypeId);
	            }

	            // ✅ 有資料才會走到這裡
	            CardTypes type = new CardTypes();
	            type.setCardTypeId(rs.getInt("card_type_id"));
	            type.setCardTypeName(rs.getString("card_type_name"));
	            type.setBrand(rs.getString("brand"));
	            type.setAnnualFee(rs.getBigDecimal("annual_fee"));
	            type.setCashbackRate(rs.getBigDecimal("cashback_rate"));
	            type.setCardImageUrl(rs.getString("card_image_url"));
	            return type;
	        }

	    } catch (SQLException e) {
	        // ❗ DB 錯誤也轉成 RuntimeException 丟出去
	        throw new RuntimeException("查詢卡種失敗", e);
	    }
		
		
	}
	
	
	
	//修改卡別
	public void updateCardType(CardTypes cardTypes) {
		String sql = "UPDATE CARD_TYPE SET card_type_name=?, brand=?, annual_fee=?,cashback_rate=?,card_image_url=?  WHERE card_type_id=?";
		try (Connection conn = ConnUtil.getConn();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, cardTypes.getCardTypeName());
            ps.setString(2, cardTypes.getBrand());
            ps.setBigDecimal(3, cardTypes.getAnnualFee());
            ps.setBigDecimal(4, cardTypes.getCashbackRate());            
            ps.setInt(5, cardTypes.getCardTypeId());
            ps.setString(6,cardTypes.getCardImageUrl());
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
