package com.ispan.bankmanagement.creditcard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ispan.bankmanagement.creditcard.entity.CardApplication;
import com.ispan.bankmanagement.creditcard.service.CardAppService;
import com.ispan.bankmanagement.creditcard.service.CardTypeService;
import com.ispan.bankmanagement.creditcard.service.CreditCardService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/card")
@RequiredArgsConstructor
public class CardController {

	private CardTypeService cardTypeService;
	private CreditCardService creditCardService;
	private CardAppService cardAppService;
	
	//卡片列表
	//http://localhost:8080/card/types.controller
	@GetMapping("/types")
	public String getAllCardType(Model model) {
		model.addAttribute("cardTypes",cardTypeService);
		return "cardType-list";
	}
	//卡片詳細
	@GetMapping("/types/{id}")
	public String getCardTypeDetail(@PathVariable Integer id,Model model) {
		model.addAttribute("cardType",cardTypeService.findById(id));
		return "cardType-detail";
	}
	// 申請信用卡
	@PostMapping("/apply")
	public String applyCard(@ModelAttribute CardApplication application) {
		cardAppService.save(application);
		return "apply";
	}
	//我的申請
	@GetMapping("/my-applications")
    public String getMyApplications(Model model) {
        model.addAttribute("applications", cardAppService.findAll());
        return "my-applications";
    }
	// 我的卡片
	@GetMapping("/my-cards")
    public String getMyCards(Model model) {
        model.addAttribute("cards", creditCardService.findAll());
        return "my-cards";
    }
	
	
	
	
	
	
	
	
	
	
	
	
}
