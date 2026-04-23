package com.ispan.bankmanagement.creditcard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ispan.bankmanagement.creditcard.entity.CardApplication;

public interface CardAppRepository extends JpaRepository<CardApplication, Integer> {

}
