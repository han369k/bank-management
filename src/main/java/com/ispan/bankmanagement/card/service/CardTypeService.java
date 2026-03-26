package com.ispan.bankmanagement.card.service;

import java.util.List;

import com.ispan.bankmanagement.card.dao.CardTypeDao;
import com.ispan.bankmanagement.card.vo.CardTypes;

public class CardTypeService {
	private CardTypeDao dao = new CardTypeDao();
	public List<CardTypes> getAllCardTypes() {
        return dao.getAllCardTypes();
    }

    public CardTypes getCardTypeById(int cardTypeId) {
        return dao.getCardTypeById(cardTypeId);
    }
	
	
}
