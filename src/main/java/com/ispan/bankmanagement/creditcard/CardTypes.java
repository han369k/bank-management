package com.ispan.bankmanagement.creditcard;

import java.io.Serializable;
import java.math.BigDecimal;

public class CardTypes implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private int cardTypeId;
    private String cardTypeName;
    private String brand;
    private BigDecimal annualFee;
    private BigDecimal cashbackRate;
//    private double creditLimit;
	public CardTypes(int cardTypeId, String cardTypeName, String brand, BigDecimal annualFee, BigDecimal cashbackRate,
			double creditLimit) {
		super();
		this.cardTypeId = cardTypeId;
		this.cardTypeName = cardTypeName;
		this.brand = brand;
		this.annualFee = annualFee;
		this.cashbackRate = cashbackRate;
//		this.creditLimit = creditLimit;
	}
	
	public CardTypes() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getCardTypeId() {
		return cardTypeId;
	}
	public void setCardTypeId(int cardTypeId) {
		this.cardTypeId = cardTypeId;
	}
	public String getCardTypeName() {
		return cardTypeName;
	}
	public void setCardTypeName(String cardTypeName) {
		this.cardTypeName = cardTypeName;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public BigDecimal getAnnualFee() {
		return annualFee;
	}
	public void setAnnualFee(BigDecimal annualFee) {
		this.annualFee = annualFee;
	}
	public BigDecimal getCashbackRate() {
		return cashbackRate;
	}
	public void setCashbackRate(BigDecimal cashbackRate) {
		this.cashbackRate = cashbackRate;
	}
	
    
    
    
    
    
}
