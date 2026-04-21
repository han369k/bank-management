package com.ispan.bankmanagement.creditcard.service;

import java.util.List;
import java.util.Optional;

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
	public Optional<CardApplication> findById(Integer id) {
		return cardAppRepository.findById(id);
	}
	public void save(CardApplication cardApplication) {
		cardAppRepository.save(cardApplication);
	}
	public void deleteById(Integer id) {
		cardAppRepository.deleteById(id);
	}
	
}
