package com.ispan.bankmanagement.creditcard.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.bankmanagement.creditcard.entity.CardApplication;
import com.ispan.bankmanagement.creditcard.entity.CardType;
import com.ispan.bankmanagement.creditcard.entity.CreditCard;
import com.ispan.bankmanagement.creditcard.service.CardAppService;
import com.ispan.bankmanagement.creditcard.service.CardTypeService;
import com.ispan.bankmanagement.creditcard.service.CreditCardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/card")
@RequiredArgsConstructor
public class CardController {

	private final CardTypeService cardTypeService;
	private final CreditCardService creditCardService;
	private final CardAppService cardAppService;
	
	//卡片列表
	
	//http://localhost:8080/card/types
	@GetMapping("/types")
	public List<CardType> getAllCardType() {
		return cardTypeService.findAll();
	}
	//卡片詳細
	//http://localhost:8080/card/types/1
	@GetMapping("/types/{id}")
	public CardType getCardTypeDetail(@PathVariable Integer id) {
		return cardTypeService.findById(id);
		
	}
	// 申請信用卡
	//http://localhost:8080/card/apply 此不能透過瀏覽器
	@PostMapping("/apply")
	public ResponseEntity<String> applyCard(@RequestBody CardApplication application) {
		cardAppService.save(application);
		return ResponseEntity.ok("Application submitted.");
	}
	//我的申請
	//http://localhost:8080/card/my-applications
	@GetMapping("/my-applications")
    public List<CardApplication> getMyApplications() {
         return cardAppService.findAll();
    }
	// 我的卡片
	//http://localhost:8080/card/my-cards
	@GetMapping("/my-cards")
    public List<CreditCard> getMyCards() {
        return creditCardService.findAll();
    }
	
	

	
}
