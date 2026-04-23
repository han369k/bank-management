package com.ispan.bankmanagement.creditcard.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ispan.bankmanagement.creditcard.dto.CardTypeCreateRequest;
import com.ispan.bankmanagement.creditcard.entity.CardApplication;
import com.ispan.bankmanagement.creditcard.entity.CardType;
import com.ispan.bankmanagement.creditcard.entity.CreditCard;
import com.ispan.bankmanagement.creditcard.enums.CardApplicationStatus;
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
	public ResponseEntity<List<CardType>> getAllCardType() {
		return ResponseEntity.ok(cardTypeService.findAll());
	}
	//卡片詳細
	//http://localhost:8080/card/types/1
	@GetMapping("/types/{id}")
	public ResponseEntity<CardType> getCardTypeDetail(@PathVariable Integer id) {
		Optional<CardType>op=cardTypeService.findById(id);
		if (op.isPresent()) {
			return ResponseEntity.ok(op.get());
		} else {
			return ResponseEntity.notFound().build();
		}
		
	}
	// 申請信用卡
	//http://localhost:8080/card/apply 此不能透過瀏覽器
	@PostMapping("/apply")
	public ResponseEntity<Map<String, String>> applyCard(@RequestBody CardApplication application) {
		cardAppService.save(application);
		return  ResponseEntity.ok(Map.of("message","Application submitted.")) ;
	}
	//所有申請表
	//http://localhost:8080/card/my-applications
	@GetMapping("/my-applications")
    public ResponseEntity<List<CardApplication>> getMyApplications() {
         return ResponseEntity.ok(cardAppService.findAll());
    }
	// 卡片列表
	//http://localhost:8080/card/my-cards
	@GetMapping("/my-cards")
    public ResponseEntity<List<CreditCard>> getMyCards() {
        return ResponseEntity.ok(creditCardService.findAll());
    }
	//修改申請表狀態列
	@PutMapping("/application/{id}/status")
	public ResponseEntity<?> updateStatus(
	        @PathVariable Integer id,
	        @RequestBody Map<String, String> body) {

	    String status = body.get("status");
	    CardApplication app = cardAppService.findById(id).orElseThrow();
	    app.setStatus(CardApplicationStatus.valueOf(status));
	    cardAppService.save(app);
	    return ResponseEntity.ok(Map.of("message", "updated"));
	}
	//新增卡別
	@PostMapping("/types")
	public ResponseEntity<?>create(@ModelAttribute CardTypeCreateRequest req,@RequestParam MultipartFile mf) throws IOException {
		System.out.println("file = " + mf.getOriginalFilename());

	    cardTypeService.createCardType(req, mf);

	    return ResponseEntity.ok("OK");
		
	}
	//刪除卡別
	@DeleteMapping("/types/{id}")
	public ResponseEntity<?> deleteCardType(@PathVariable Integer id) {
	    cardTypeService.deleteById(id);
	    return ResponseEntity.ok("deleted");
	}
	@PutMapping("/types/{id}")
	public ResponseEntity<?> updateCardType(
	        @PathVariable Integer id,
	        @ModelAttribute CardTypeCreateRequest req,
	        @RequestParam(required = false) MultipartFile mf
	) throws IOException {

	    cardTypeService.updateCardType(id, req, mf);

	    return ResponseEntity.ok("updated");
	}
}
