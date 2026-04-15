package com.ispan.bankmanagement.creditcard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ispan.bankmanagement.creditcard.entity.CreditCard;

public interface CreditCardRepository extends JpaRepository<CreditCard, Integer> {

}
