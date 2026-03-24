package com.ispan.bankmanagement.card.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import com.ispan.bankmanagement.card.vo.CreditCard;
import com.ispan.bankmanagement.card.vo.Enum.CardStatus;
import com.ispan.bankmanagement.util.ConnUtil;
import com.ispan.bankmanagement.card.dao.CreditCardDao;


public class CardService {
	
	private CreditCardDao dao=new CreditCardDao();
	
	//新增卡片
	public void addCard(CreditCard card) {
		
		if (card.getCreateDate() == null) {
			card.setCreateDate(new Timestamp(System.currentTimeMillis()));
		}
		if (card.getCurrentBalance() == null) {
            card.setCurrentBalance(BigDecimal.ZERO);
        }
        dao.addCreditCard(card);
	}
	//查詢單筆
    public CreditCard getCardById(int id) {
        return dao.getCreditCardById(id);
    }
 // 查詢全部
    public List<CreditCard> getAllCards() {
        return dao.getAllCreditCards();
    }

    //更新卡片
    public void updateCard(CreditCard card) {
        dao.updateCreditCard(card);
    }
    //更新卡片status
    public void updateStatus(int cardId, CardStatus status) {
        dao.updateStatus(cardId, status);
    }

    //刪除卡片
    public void deleteCard(int id) {
        dao.deleteCreditCard(id);
    }
	//更新餘額
    public boolean updateBalance(int cardId,BigDecimal newBalance) {
		
    	Connection conn=null;
		try {
			conn=ConnUtil.getConn();
			conn.setAutoCommit(false);
			
			boolean result=dao.updateBalance(conn, cardId, newBalance);
			conn.commit();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			if (conn != null)
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
		}finally {
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return false;
	}
	
	
}
