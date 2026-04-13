package com.ispan.bankmanagement.creditcard;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class CreditCardTransaction implements Serializable {
	private static final long serialVersionUID = 1L;
	private int txnId;
    private int cardId;
    private Integer merchantId;
    private BigDecimal txnAmount;
    private String txnType;
    private Timestamp txnDate;
    private String description;
    private Integer refTxnId;
	
	public int getTxnId() {
		return txnId;
	}
	
	public CreditCardTransaction() {
		super();
		// TODO Auto-generated constructor stub
	}
//
//	//刷卡建構子
//	public CreditCardTransaction(int cardId, Integer merchantId, BigDecimal txnAmount, String txnType, String description) {
//		super();
//		this.cardId = cardId;
//		this.merchantId = merchantId;
//		this.txnAmount = txnAmount;
//		this.txnType = "PURCHASE";
//		this.description = description;
//	}
//	//繳款建構子
//	public CreditCardTransaction(int cardId, BigDecimal txnAmount, String txnType, String description) {
//		super();
//		this.cardId = cardId;
//		this.txnAmount = txnAmount;
//		this.txnType = "PAYMENT";
//		this.description = description;
//	}
//	//退款建構子
//	public CreditCardTransaction(int cardId, Integer merchantId, BigDecimal txnAmount, String txnType, String description,
//			Integer refTxnId) {
//		super();
//		this.cardId = cardId;
//		this.merchantId = merchantId;
//		this.txnAmount = txnAmount;
//		this.txnType = "REFUND";
//		this.description = description;
//		this.refTxnId = refTxnId;
//	}
	public void setTxnId(int txnId) {
		this.txnId = txnId;
	}

	public int getCardId() {
		return cardId;
	}
	public void setCardId(int cardId) {
		this.cardId = cardId;
	}
	public Integer getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(Integer merchantId) {
		this.merchantId = merchantId;
	}
	public BigDecimal getTxnAmount() {
		return txnAmount;
	}
	public void setTxnAmount(BigDecimal txnAmount) {
		this.txnAmount = txnAmount;
	}
	public String getTxnType() {
		return txnType;
	}
	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}
	public Timestamp getTxnDate() {
		return txnDate;
	}
	public void setTxnDate(Timestamp txnDate) {
		this.txnDate = txnDate;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getRefTxnId() {
		return refTxnId;
	}
	public void setRefTxnId(Integer refTxnId) {
		this.refTxnId = refTxnId;
	}
    
    
    
    
}
