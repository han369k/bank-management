package com.ispan.bankmanagement.creditcard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ispan.bankmanagement.creditcard.entity.CardType;

public interface CardTypeRepository extends JpaRepository<CardType, Integer> {

}
