package com.ispan.bankmanagement.creditcard.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ispan.bankmanagement.creditcard.entity.CardType;
import com.ispan.bankmanagement.creditcard.repository.CardTypeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CardTypeService {

	
	private final CardTypeRepository cardTypeRepository;

	public CardType insert(CardType cardType) {
		return cardTypeRepository.save(cardType);
	}
	public CardType update(CardType cardType) {
		return cardTypeRepository.save(cardType);
	}
	public void deleteById(Integer id) {
		cardTypeRepository.deleteById(id);
	}
	public Optional<CardType> findById(Integer id) {
		return cardTypeRepository.findById(id);
	}
	public List<CardType> findAll() {
		return cardTypeRepository.findAll();
	}
	
	
	
	
	
	
	
}
