package com.ispan.bankmanagement.loan;

import java.math.BigDecimal;

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
                validateTerm(term, new int[]{12,24,36,48,60});
                break;
            case "CAR":
                baseRate = new BigDecimal("0.025");
                validateTerm(term, new int[]{12,24,36,48,60});
                break;
            case "MOTOR":
                baseRate = new BigDecimal("0.045");
                validateTerm(term, new int[]{12,24,36});
                break;
            case "STUDENT":
                validateTerm(term, new int[]{60,84,120});
                return new BigDecimal("0.015");
            case "BUSINESS":
                baseRate = new BigDecimal("0.02");
                validateTerm(term, new int[]{36,60,84});
                break;
            case "HOUSE":
                baseRate = new BigDecimal("0.018");
                validateTerm(term, new int[]{120,240,360,480});
                break;
            case "LAND":
                baseRate = new BigDecimal("0.028");
                validateTerm(term, new int[]{120,180,240});
                break;
            default:
                baseRate = new BigDecimal("0.03");
        }

        BigDecimal termRate;
        switch (term) {
            case 12: termRate = BigDecimal.ZERO; break;
            case 24: termRate = new BigDecimal("0.002"); break;
            case 36: termRate = new BigDecimal("0.005"); break;
            case 48: termRate = new BigDecimal("0.008"); break;
            case 60: termRate = new BigDecimal("0.01"); break;
            case 84: termRate = new BigDecimal("0.015"); break;

            case 120: termRate = BigDecimal.ZERO; break;
            case 180: termRate = new BigDecimal("0.002"); break;
            case 240: termRate = new BigDecimal("0.004"); break;
            case 360: termRate = new BigDecimal("0.006"); break;
            case 480: termRate = new BigDecimal("0.008"); break;

            default:
                termRate = BigDecimal.ZERO;
        }

        return baseRate.add(termRate);
    }

    private void validateTerm(int term, int[] allowed) {

        for (int t : allowed) {
            if (t == term) {
                return;
            }
        }

        throw new RuntimeException("此貸款種類不支援該期數：" + term);
    }
}
