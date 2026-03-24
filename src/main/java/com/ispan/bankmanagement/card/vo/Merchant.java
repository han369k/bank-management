package com.ispan.bankmanagement.card.vo;

import java.io.Serializable;

public class Merchant implements Serializable{
	private static final long serialVersionUID = 1L;
	private int merchantId;
    private String merchantName;
    private String merchantCategory;
	public Merchant(int merchantId, String merchantName, String merchantCategory) {
		super();
		this.merchantId = merchantId;
		this.merchantName = merchantName;
		this.merchantCategory = merchantCategory;
	}
	public int getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(int merchantId) {
		this.merchantId = merchantId;
	}
	public String getMerchantName() {
		return merchantName;
	}
	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}
	public String getMerchantCategory() {
		return merchantCategory;
	}
	public void setMerchantCategory(String merchantCategory) {
		this.merchantCategory = merchantCategory;
	}
    
    
    
}
