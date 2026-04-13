package com.ispan.bankmanagement.creditcard;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

import com.ispan.bankmanagement.creditcard.Enum.BillStatus;


public class CreditCardBill implements Serializable{
	private static final long serialVersionUID = 1L;
	private int billId;
    private int cardId;
    

    private String billingMonth;

    private Date billDate;
    private Date dueDate;

    private BigDecimal totalAmount;
    private BigDecimal minimumPayment;
    private BigDecimal paidAmount;
    private BillStatus billStatus;
	public CreditCardBill(int billId, int cardId, String billingMonth, Date billDate, Date dueDate,
			BigDecimal totalAmount, BigDecimal minimumPayment, BigDecimal paidAmount, BillStatus billStatus) {
		super();
		this.billId = billId;
		this.cardId = cardId;
		this.billingMonth = billingMonth;
		this.billDate = billDate;
		this.dueDate = dueDate;
		this.totalAmount = totalAmount;
		this.minimumPayment = minimumPayment;
		this.paidAmount = paidAmount;
		this.billStatus = billStatus;
	}
	
	public CreditCardBill() {
		super();
	}
	
	public BigDecimal getRemainingAmount() {
		if (totalAmount==null||paidAmount==null) {
			return BigDecimal.ZERO;
		}
		return totalAmount.subtract(paidAmount);
	}

	public int getBillId() {
		return billId;
	}
	public void setBillId(int billId) {
		this.billId = billId;
	}
	public int getCardId() {
		return cardId;
	}
	public void setCardId(int cardId) {
		this.cardId = cardId;
	}
	public String getBillingMonth() {
		return billingMonth;
	}
	public void setBillingMonth(String billingMonth) {
		this.billingMonth = billingMonth;
	}
	public Date getBillDate() {
		return billDate;
	}
	public void setBillDate(Date billDate) {
		this.billDate = billDate;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public BigDecimal getMinimumPayment() {
		return minimumPayment;
	}
	public void setMinimumPayment(BigDecimal minimumPayment) {
		this.minimumPayment = minimumPayment;
	}
	public BigDecimal getPaidAmount() {
		return paidAmount;
	}
	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}
	public BillStatus getBillStatus() {
		return billStatus;
	}
	public void setBillStatus(BillStatus billStatus) {
		this.billStatus = billStatus;
	}
    
    
}
