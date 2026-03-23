package com.ispan.bankmanagement.loan.service;

import com.ispan.bankmanagement.loan.dao.LoanApplyDao;

import java.math.BigDecimal;
import java.util.List;

public class LoanApplyService {

    private LoanApplyDao loanDao = new LoanApplyDao();

    public BigDecimal calculateRate(String applyType, Integer term) {

        if (applyType == null || term == null) {
            return new BigDecimal("0.03");
        }

        BigDecimal baseRate;
        switch (applyType) {
            case "PERSONAL":
                baseRate = new BigDecimal("0.04");
                break;
            case "CAR":
                baseRate = new BigDecimal("0.025");
                break;
            case "MOTOR":
                baseRate = new BigDecimal("0.045");
                break;
            case "STUDENT":
                return new BigDecimal("0.015");
            case "BUSINESS":
                baseRate = new BigDecimal("0.02");
                break;
            case "HOUSE":
                baseRate = new BigDecimal("0.018");
                break;
            case "LAND":
                baseRate = new BigDecimal("0.028");
                break;
            default:
                baseRate = new BigDecimal("0.03");
        }

        BigDecimal termRate;
        switch (term) {
            case 12:
                termRate = BigDecimal.ZERO;
                break;
            case 24:
                termRate = new BigDecimal("0.002");
                break;
            case 36:
                termRate = new BigDecimal("0.005");
                break;
            case 48:
                termRate = new BigDecimal("0.008");
                break;
            case 60:
                termRate = new BigDecimal("0.01");
                break;
            case 84:
                termRate = new BigDecimal("0.015");
                break;

            case 120:
                termRate = BigDecimal.ZERO;
                break;
            case 180:
                termRate = new BigDecimal("0.002");
                break;
            case 240:
                termRate = new BigDecimal("0.004");
                break;
            case 360:
                termRate = new BigDecimal("0.006");
                break;
            case 480:
                termRate = new BigDecimal("0.008");
                break;

            default:
                termRate = BigDecimal.ZERO;
        }

        return baseRate.add(termRate);
    }
}