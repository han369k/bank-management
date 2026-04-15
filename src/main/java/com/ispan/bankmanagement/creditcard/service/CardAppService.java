package com.ispan.bankmanagement.creditcard.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ispan.bankmanagement.creditcard.entity.CardApplication;
import com.ispan.bankmanagement.creditcard.repository.CardAppRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CardAppService {

	private final CardAppRepository cardAppRepository;
	
	public List<CardApplication> findAll() {
		return cardAppRepository.findAll();
	}
	public CardApplication findById(Integer id) {
		return cardAppRepository.findById(id).orElse(null);
	}
	public void save(CardApplication cardApplication) {
		cardAppRepository.save(cardApplication);
	}
	public void deleteById(Integer id) {
		cardAppRepository.deleteById(id);
	}
	
}
