package com.laurabeltran.banking_api.infrastructure.persistence.account;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface JpaAccountRepository extends JpaRepository<AccountEntity, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM AccountEntity a WHERE a.id = :id")
    Optional<AccountEntity> findByIdForUpdate(@Param("id") int id);

    interface AccountOwnershipProjection {
        Integer getAccountId();

        Integer getOwnerId();
    }

    @Query("""
            SELECT a.id AS accountId, a.owner.id AS ownerId
            FROM AccountEntity a
            LEFT JOIN a.owner owner
            WHERE a.id = :id
            """)
    Optional<AccountOwnershipProjection> findOwnershipById(@Param("id") int id);

}
