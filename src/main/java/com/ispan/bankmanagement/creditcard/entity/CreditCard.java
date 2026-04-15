package com.ispan.bankmanagement.creditcard.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ispan.bankmanagement.creditcard.enums.CardStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreditCard {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "card_id")
    private Integer cardId;
    private Integer customerId;
//    private Integer cardTypeId;//FK關聯 不留
    
    @ManyToOne//對應很多cardType
    @JoinColumn(name = "card_type_id")
    private CardType cardType;
    
    private String cardNumber;
    private LocalDate expiryDate;
    @Column(name = "credit_limit", precision = 15, scale = 2)
    private BigDecimal creditLimit;
    @Column(name = "current_balance", precision = 15, scale = 2)
    private BigDecimal currentBalance;
    private LocalDateTime createDate;
    @Enumerated(EnumType.STRING)
    private CardStatus status;
    
//    private Integer applicationItemId;//FK關聯不留
    
    @ManyToOne
    @JoinColumn(name = "application_item_id")
    private CardApplicationItem applicationItem;

}
