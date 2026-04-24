package com.ispan.bankmanagement.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginAdministratorRepository extends JpaRepository<LoginAdministrator, Integer> {
    Optional<LoginAdministrator> findByUsername(String username);
}