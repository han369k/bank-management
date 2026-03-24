package com.ispan.bankmanagement.card.dto;

import java.math.BigDecimal;

public class MonthlyBill {
	private String billingMonth;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    
    
    

    public MonthlyBill() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BigDecimal getRemainingAmount() {
        return totalAmount.subtract(paidAmount);
    }

	public String getBillingMonth() {
		return billingMonth;
	}

	public void setBillingMonth(String billingMonth) {
		this.billingMonth = billingMonth;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}
    
    
    
    
    
    
    
    
    
}
