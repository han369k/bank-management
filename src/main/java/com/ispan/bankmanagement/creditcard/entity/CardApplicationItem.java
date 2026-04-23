package com.ispan.bankmanagement.creditcard.entity;

import java.math.BigDecimal;

import com.ispan.bankmanagement.creditcard.enums.CardApplicationItemResult;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@Table(name = "CARD_APPLICATION_ITEM")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardApplicationItem {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Integer itemId;

//    @Column(name = "application_id")
//    private Integer applicationId;//拿掉
//
//    @Column(name = "card_type_id")
//    private Integer cardTypeId;//拿掉

    @ManyToOne//多對一：申請明細屬於某一申請單(支援一筆申請多卡)
    @JoinColumn(name = "application_id")
    private CardApplication application;

    @ManyToOne //多對一：申請明細對應卡片類型(多筆申請可共用同一卡種)
    @JoinColumn(name = "card_type_id")
    private CardType cardType;

    @Enumerated(EnumType.STRING)
    @Column(name = "result")
    private CardApplicationItemResult result;

    @Column(name = "approved_limit", precision = 15, scale = 2)
    private BigDecimal approvedLimit;

    @Column(name = "annual_fee", precision = 10, scale = 2)
    private BigDecimal annualFee;

    @Column(name = "create_card_flag")
    private Boolean createCardFlag;

    @Column(name = "remark", length = 200)
    private String remark;

    @PrePersist
    public void prePersist() {
        if (result == null) {
            result = CardApplicationItemResult.PENDING;
        }
        if (createCardFlag == null) {
            createCardFlag = false;
        }
    }
	
}
