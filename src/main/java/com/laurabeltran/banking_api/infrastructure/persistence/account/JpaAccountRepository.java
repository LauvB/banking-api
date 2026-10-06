package com.laurabeltran.banking_api.infrastructure.persistence.account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaAccountRepository extends JpaRepository<AccountEntity, Integer> {

}
