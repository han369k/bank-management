package com.ispan.bankmanagement.creditcard.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ispan.bankmanagement.creditcard.dto.CardTypeCreateRequest;
import com.ispan.bankmanagement.creditcard.entity.CardType;
import com.ispan.bankmanagement.creditcard.repository.CardTypeRepository;
import com.ispan.bankmanagement.creditcard.repository.CreditCardRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CardTypeService {

	
	private final CardTypeRepository cardTypeRepository;
	private final CreditCardRepository cardRepository;

	public CardType insert(CardType cardType) {
		return cardTypeRepository.save(cardType);
	}
	public CardType update(CardType cardType) {
		return cardTypeRepository.save(cardType);
	}
	public void deleteById(Integer id) {

		// 1. 確認卡片存在
	    CardType cardType = cardTypeRepository.findById(id)
	        .orElseThrow(() -> new RuntimeException("找不到卡片"));

	    // 2. 檢查是否被使用（FK）
	    boolean isUsed = cardRepository.existsByCardType_CardTypeId(id);

	    if (isUsed) {
	        throw new RuntimeException("此卡別已被使用，無法刪除");
	    }

	    // 3. 刪除
	    cardTypeRepository.delete(cardType);
	}
	public Optional<CardType> findById(Integer id) {
		return cardTypeRepository.findById(id);
	}
	public List<CardType> findAll() {
		return cardTypeRepository.findAll();
	}
	
	public CardType createCardType(CardTypeCreateRequest request,MultipartFile mf) throws IOException {
		//存圖片
		String fileName=System.currentTimeMillis()+"_"+mf.getOriginalFilename();
		Path path=Paths.get("uploads/"+fileName);
		Files.createDirectories(path.getParent());
		Files.write(path, mf.getBytes());
		
		CardType card = new CardType();
	    card.setCardTypeName(request.getCardTypeName());
	    card.setBrand(request.getBrand());
	    card.setAnnualFee(request.getAnnualFee());
	    card.setCashbackRate(request.getCashbackRate());
	    card.setCardImageUrl("/uploads/" + fileName);
		return cardTypeRepository.save(card);
		
		
	}
	public CardType updateCardType(Integer id, CardTypeCreateRequest request, MultipartFile mf) throws IOException {
		CardType card = cardTypeRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("找不到卡片"));

	    // 2. 更新基本欄位
	    card.setCardTypeName(request.getCardTypeName());
	    card.setBrand(request.getBrand());
	    card.setAnnualFee(request.getAnnualFee());
	    card.setCashbackRate(request.getCashbackRate());

	    // 3. 如果有上傳新圖片 → 才更新圖片
	    if (mf != null && !mf.isEmpty()) {
	        String fileName = System.currentTimeMillis() + "_" + mf.getOriginalFilename();
	        Path path = Paths.get("uploads/" + fileName);
	        Files.createDirectories(path.getParent());
	        Files.write(path, mf.getBytes());

	        card.setCardImageUrl("/uploads/" + fileName);
	    }

	    return cardTypeRepository.save(card);
	}
	
	
	
	
	
}
