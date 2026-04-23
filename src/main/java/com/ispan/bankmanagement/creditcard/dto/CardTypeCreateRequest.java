package com.ispan.bankmanagement.creditcard.dto;

import java.math.BigDecimal;

import lombok.Data;
@Data
public class CardTypeCreateRequest {

	private String cardTypeName;
    private String brand;
    private BigDecimal annualFee;
    private BigDecimal cashbackRate;
}
