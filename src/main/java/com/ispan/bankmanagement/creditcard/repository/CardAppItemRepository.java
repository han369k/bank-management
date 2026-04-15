package com.ispan.bankmanagement.creditcard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ispan.bankmanagement.creditcard.entity.CardApplicationItem;

public interface CardAppItemRepository extends JpaRepository<CardApplicationItem, Integer> {

}
