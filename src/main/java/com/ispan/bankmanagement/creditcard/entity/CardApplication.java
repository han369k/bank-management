package com.ispan.bankmanagement.creditcard.entity;

import java.time.LocalDateTime;

import com.ispan.bankmanagement.creditcard.enums.CardApplicationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "CARD_APPLICATION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardApplication {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Integer applicationId;

    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "apply_date")
    private LocalDateTime applyDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CardApplicationStatus status;

    @Column(name = "remark", length = 200)
    private String remark;
    
    @PrePersist
    public void prePersist() {
		if (applyDate == null) {
			applyDate=LocalDateTime.now();
		}
		if (status == null) {
			status=CardApplicationStatus.PENDING;
		}
	}
    
    
    
}
