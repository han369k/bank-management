package com.ispan.bankmanagement.creditcard;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import com.ispan.bankmanagement.creditcard.Enum.CardStatus;

public class CreditCard implements Serializable{
	private static final long serialVersionUID = 1L;

    private int cardId;
    private int customerId;
    private int cardTypeId;
    private String cardNumber;
    private Date expiryDate;
//    private String cvv;
    private BigDecimal creditLimit;      // 每張卡不同
    private BigDecimal currentBalance;
    private Timestamp createDate;
    private CardStatus status;
    
	public CreditCard(int cardId, int customerId, int cardTypeId, String cardNumber, Date expiryDate,
			BigDecimal creditLimit, BigDecimal currentBalance, Timestamp createDate, CardStatus status) {
		super();
		this.cardId = cardId;
		this.customerId = customerId;
		this.cardTypeId = cardTypeId;
		this.cardNumber = cardNumber;
		this.expiryDate = expiryDate;
		this.creditLimit = creditLimit;
		this.currentBalance = currentBalance;
		this.createDate = createDate;
		this.status = status;
	}
	
	
	
	public CreditCard() {
		super();
		// TODO Auto-generated constructor stub
	}
	public int getCardId() {
		return cardId;
	}
	public void setCardId(int cardId) {
		this.cardId = cardId;
	}
	public int getCustomerId() {
		return customerId;
	}
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	public int getCardTypeId() {
		return cardTypeId;
	}
	public void setCardTypeId(int cardTypeId) {
		this.cardTypeId = cardTypeId;
	}
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	public Date getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}
	public BigDecimal getCreditLimit() {
		return creditLimit;
	}
	public void setCreditLimit(BigDecimal creditLimit) {
		this.creditLimit = creditLimit;
	}
	public BigDecimal getCurrentBalance() {
		return currentBalance;
	}
	public void setCurrentBalance(BigDecimal currentBalance) {
		this.currentBalance = currentBalance;
	}
	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
	public CardStatus getStatus() {
		return status;
	}
	public void setStatus(CardStatus status) {
		this.status = status;
	}
    
    
    
}
