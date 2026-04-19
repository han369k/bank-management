package com.ispan.bankmanagement.loan;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "LOAN_APPLICATION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoanApplication {

    @Id
    @Column(name = "application_id", length = 20)
    private String applicationId;

    @Column(name = "customer_id", nullable = false, length = 20)
    private Integer customerId;

    @Column(name = "apply_type", nullable = false, length = 20)
    private String applyType;

    @Column(name = "apply_amount")
    private Long applyAmount;

    @Column(name = "apply_period")
    private Integer applyPeriod;

    @Column(name = "rate", precision = 10, scale = 6)
    private BigDecimal rate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private LoanApplicationStatus status;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "approved_amount")
    private Long approvedAmount;

    @Column(name = "approved_rate", precision = 10, scale = 6)
    private BigDecimal approvedRate;

    @Column(name = "approved_period")
    private Integer approvedPeriod;

    @Column(name = "reviewer_id")
    private Integer reviewerId;

    @Column(name = "review_time")
    private LocalDateTime reviewTime;
}
