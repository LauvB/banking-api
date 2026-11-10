
package com.laurabeltran.banking_api.infrastructure.persistence.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.laurabeltran.banking_api.domain.account.Account;
import com.laurabeltran.banking_api.infrastructure.persistence.user.JpaUserRepository;
import com.laurabeltran.banking_api.infrastructure.persistence.user.UserEntity;

@ExtendWith(MockitoExtension.class)
public class PostgreSQLAccountRepositoryTest {

    @Mock
    private JpaAccountRepository jpaAccountRepository;

    @Mock
    private JpaUserRepository jpaUserRepository;

    private PostgreSQLAccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        accountRepository = new PostgreSQLAccountRepository(
                jpaAccountRepository,
                jpaUserRepository);
    }

    @Test
    void shouldSaveAccountWithoutOwner() {
        Account account = new Account(
                null,
                "ACC-001",
                new BigDecimal("500.00"),
                "COP");

        when(jpaAccountRepository.save(any(AccountEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Account savedAccount = accountRepository.save(account);

        assertNull(savedAccount.getOwnerId());
        assertNull(savedAccount.getId());
        assertEquals("ACC-001", savedAccount.getAccountNumber());
        assertEquals(new BigDecimal("500.00"), savedAccount.getBalance());
        assertEquals("COP", savedAccount.getCurrency());

        verify(jpaAccountRepository).save(any(AccountEntity.class));
        verifyNoInteractions(jpaUserRepository);
    }

    @Test
    void shouldSaveAccountWithOwner() {

        UserEntity owner = mock(UserEntity.class);

        when(owner.getId()).thenReturn(3);

        Account account = new Account(
                null,
                "ACC-002",
                new BigDecimal("750.00"),
                "COP",
                3);

        when(jpaUserRepository.findById(3))
                .thenReturn(Optional.of(owner));

        when(jpaAccountRepository.save(any(AccountEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Account savedAccount = accountRepository.save(account);

        assertEquals(3, savedAccount.getOwnerId());
        assertEquals("ACC-002", savedAccount.getAccountNumber());
        assertEquals(new BigDecimal("750.00"), savedAccount.getBalance());
        assertEquals("COP", savedAccount.getCurrency());

        verify(jpaUserRepository).findById(3);
        verify(jpaAccountRepository).save(any(AccountEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenOwnerDoesNotExist() {
        Account account = new Account(
                null,
                "ACC-003",
                new BigDecimal("900.00"),
                "COP",
                3);

        when(jpaUserRepository.findById(3))
                .thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> accountRepository.save(account));

        assertEquals("Owner not found: 3", exception.getMessage());

        verify(jpaUserRepository).findById(3);
        verify(jpaAccountRepository, never()).save(any(AccountEntity.class));
    }

    @Test
    void shouldFindAccountById() {
        AccountEntity entity = new AccountEntity(
                1,
                "ACC-001",
                new BigDecimal("500.00"),
                "COP");

        when(jpaAccountRepository.findById(1))
                .thenReturn(Optional.of(entity));

        Optional<Account> result = accountRepository.findById(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getId());
        assertEquals("ACC-001", result.get().getAccountNumber());
        assertEquals(new BigDecimal("500.00"), result.get().getBalance());
        assertEquals("COP", result.get().getCurrency());
        assertNull(result.get().getOwnerId());

        verify(jpaAccountRepository).findById(1);
    }

    @Test
    void shouldReturnEmptyWhenAccountDoesNotExist() {
        when(jpaAccountRepository.findById(99))
                .thenReturn(Optional.empty());

        Optional<Account> result = accountRepository.findById(99);

        assertTrue(result.isEmpty());

        verify(jpaAccountRepository).findById(99);
    }

}
