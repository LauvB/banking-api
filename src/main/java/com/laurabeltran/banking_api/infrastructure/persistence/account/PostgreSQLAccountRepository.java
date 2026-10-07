package com.laurabeltran.banking_api.infrastructure.persistence.account;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.laurabeltran.banking_api.application.account.port.AccountRepository;
import com.laurabeltran.banking_api.domain.account.Account;

@Repository
public class PostgreSQLAccountRepository implements AccountRepository {

    private final JpaAccountRepository jpaAccountRepository;

    public PostgreSQLAccountRepository(JpaAccountRepository jpaAccountRepository) {
        this.jpaAccountRepository = jpaAccountRepository;
    }

    @Override
    public Account save(Account account) {

        AccountEntity entity = new AccountEntity(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getCurrency());

        AccountEntity savedEntity = jpaAccountRepository.save(entity);

        return new Account(
                savedEntity.getId(),
                savedEntity.getAccountNumber(),
                savedEntity.getBalance(),
                savedEntity.getCurrency());
    }

    @Override
    public Optional<Account> findById(int id) {

        return jpaAccountRepository.findById(id)
                .map(entity -> new Account(
                        entity.getId(),
                        entity.getAccountNumber(),
                        entity.getBalance(),
                        entity.getCurrency()));
    }

    @Override
    public Account update(Account account) {

        AccountEntity entity = new AccountEntity(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getCurrency());

        AccountEntity updatedEntity = jpaAccountRepository.save(entity);

        return new Account(
                updatedEntity.getId(),
                updatedEntity.getAccountNumber(),
                updatedEntity.getBalance(),
                updatedEntity.getCurrency());
    }

}
