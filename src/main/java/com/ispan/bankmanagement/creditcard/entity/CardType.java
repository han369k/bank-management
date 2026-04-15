package com.ispan.bankmanagement.creditcard.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class CardType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "card_type_id")
	private Integer cardTypeId;
    private String cardTypeName;
    private String brand;
    @Column(name = "annual_fee", precision = 15, scale = 2)
    private BigDecimal annualFee;
    @Column(name = "cashback_rate", precision = 15, scale = 2)
    private BigDecimal cashbackRate;
    private String cardImageUrl;
	

}
