package com.laurabeltran.banking_api.infrastructure.persistence.account;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.laurabeltran.banking_api.application.account.port.AccountRepository;
import com.laurabeltran.banking_api.domain.account.Account;
import com.laurabeltran.banking_api.infrastructure.persistence.user.JpaUserRepository;
import com.laurabeltran.banking_api.infrastructure.persistence.user.UserEntity;

@Repository
public class PostgreSQLAccountRepository implements AccountRepository {

    private final JpaAccountRepository jpaAccountRepository;
    private final JpaUserRepository jpaUserRepository;

    public PostgreSQLAccountRepository(JpaAccountRepository jpaAccountRepository,
            JpaUserRepository jpaUserRepository) {
        this.jpaAccountRepository = jpaAccountRepository;
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    public Account save(Account account) {
        AccountEntity entity = toEntity(account);
        AccountEntity savedEntity = jpaAccountRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Account> findById(int id) {
        return jpaAccountRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Account update(Account account) {
        AccountEntity entity = toEntity(account);
        AccountEntity updatedEntity = jpaAccountRepository.save(entity);
        return toDomain(updatedEntity);
    }

    private AccountEntity toEntity(Account account) {
        AccountEntity entity = new AccountEntity(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getCurrency());

        if (account.getOwnerId() != null) {
            UserEntity owner = jpaUserRepository.findById(account.getOwnerId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Owner not found: " + account.getOwnerId()));

            entity.setOwner(owner);
        }

        return entity;
    }

    private Account toDomain(AccountEntity entity) {
        Integer ownerId = entity.getOwner() != null
                ? entity.getOwner().getId()
                : null;

        return new Account(
                entity.getId(),
                entity.getAccountNumber(),
                entity.getBalance(),
                entity.getCurrency(),
                ownerId);
    }

}
