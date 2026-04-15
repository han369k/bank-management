package com.ispan.bankmanagement.creditcard.service;

import org.springframework.stereotype.Service;

import com.ispan.bankmanagement.creditcard.entity.CardApplicationItem;
import com.ispan.bankmanagement.creditcard.repository.CardAppItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CardAppItemService {

	private final CardAppItemRepository cardAppItemRepository;
	
	public void save(CardApplicationItem cardApplicationItem) {
		cardAppItemRepository.save(cardApplicationItem);
	}
	public void deleteById(Integer id) {
		cardAppItemRepository.deleteById(id);
	}
	public CardApplicationItem findById(Integer id) {
		return cardAppItemRepository.findById(id).orElse(null);
	}

}
