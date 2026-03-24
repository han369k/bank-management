package com.ispan.bankmanagement.card.vo;

public class Enum {
	public enum BillStatus {
	    UNPAID,
	    PARTIAL,
	    PAID
	}
	public enum CardStatus {
		INACTIVE(0),
		ACTIVE(1),
	    BLOCKED(2);
		
		private final int value;
		private CardStatus(int value) {
			// TODO Auto-generated constructor stub
			this.value=value;
		}
		public int getValue() {
			return value;
		}
		public static CardStatus fromValue(int value) {
			for (CardStatus status : CardStatus.values()) {
				if (status.value==value) {
					return status;
				}
			}
			return null;
		}
		
	}
	
}
