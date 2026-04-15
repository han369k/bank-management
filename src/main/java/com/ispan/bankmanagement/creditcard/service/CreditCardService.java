package com.ispan.bankmanagement.creditcard.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ispan.bankmanagement.creditcard.entity.CreditCard;
import com.ispan.bankmanagement.creditcard.repository.CreditCardRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CreditCardService {

	private final CreditCardRepository cardRepository;
	
	public List<CreditCard>findAll() {
		return cardRepository.findAll();
	}
	public void save(CreditCard card) {
		cardRepository.save(card);
	}
	public CreditCard findById(Integer id) {
		return cardRepository.findById(id).orElse(null);
	}
	public void deleteById(Integer id) {
		cardRepository.deleteById(id);
	}
	
	
}
