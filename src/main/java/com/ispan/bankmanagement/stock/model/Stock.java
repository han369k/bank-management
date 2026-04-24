package com.ispan.bankmanagement.stock.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "stock_info")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Stock {
    @Id
    private Integer stockId;
    private String stockName;
    private Boolean status;
}
