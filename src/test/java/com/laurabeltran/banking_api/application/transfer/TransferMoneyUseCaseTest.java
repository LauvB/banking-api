package com.laurabeltran.banking_api.application.transfer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
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

import com.laurabeltran.banking_api.application.account.AccountOwnershipService;
import com.laurabeltran.banking_api.application.account.port.AccountRepository;
import com.laurabeltran.banking_api.domain.account.Account;
import com.laurabeltran.banking_api.domain.account.AccountNotFoundException;
import com.laurabeltran.banking_api.domain.account.InsufficientBalanceException;

@ExtendWith(MockitoExtension.class)
public class TransferMoneyUseCaseTest {

    @Mock
    private AccountOwnershipService service;

    @Mock
    private AccountRepository repository;

    private TransferMoneyUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new TransferMoneyUseCase(service, repository);
    }

    @Test
    void shouldTransferAmount() {

        Account sourceAccount = new Account(
                1,
                "001234",
                new BigDecimal("60000"),
                "COP",
                3);

        Account targetAccount = new Account(
                2,
                "005678",
                new BigDecimal("20000"),
                "COP",
                7);

        when(service.getAccountForUser(1, "apiuser"))
                .thenReturn(sourceAccount);

        when(repository.findById(2))
                .thenReturn(Optional.of(targetAccount));

        useCase.execute(
                1,
                2,
                new BigDecimal("20000"),
                "apiuser");

        assertEquals(new BigDecimal("40000"), sourceAccount.getBalance());
        assertEquals(new BigDecimal("40000"), targetAccount.getBalance());

        verify(service).getAccountForUser(1, "apiuser");
        verify(repository).findById(2);
        verify(repository).update(sourceAccount);
        verify(repository).update(targetAccount);
    }

    @Test
    void shouldThrowExceptionWhenSourceAccountDoesNotExist() {

        when(service.getAccountForUser(1, "apiuser"))
                .thenThrow(new AccountNotFoundException(
                        "Account not found with id: 1"));

        AccountNotFoundException exception = assertThrows(
                AccountNotFoundException.class,
                () -> useCase.execute(
                        1,
                        2,
                        new BigDecimal("20000"),
                        "apiuser"));

        assertEquals(
                "Account not found with id: 1",
                exception.getMessage());

        verify(service).getAccountForUser(1, "apiuser");
        verify(repository, never()).findById(2);
        verify(repository, never()).update(any(Account.class));
    }

    @Test
    void shouldThrowExceptionWhenTargetAccountDoesNotExist() {

        Account sourceAccount = new Account(
                1,
                "001234",
                new BigDecimal("60000"),
                "COP",
                3);

        when(service.getAccountForUser(1, "apiuser"))
                .thenReturn(sourceAccount);

        when(repository.findById(2))
                .thenReturn(Optional.empty());

        AccountNotFoundException exception = assertThrows(
                AccountNotFoundException.class,
                () -> useCase.execute(
                        1,
                        2,
                        new BigDecimal("20000"),
                        "apiuser"));

        assertEquals(
                "Account not found with id: 2",
                exception.getMessage());

        verify(service).getAccountForUser(1, "apiuser");
        verify(repository).findById(2);
        verify(repository, never()).update(any(Account.class));
    }

    @Test
    void shouldThrowExceptionWhenSourceAccountHasInsufficientBalance() {

        Account sourceAccount = new Account(
                1,
                "001234",
                new BigDecimal("20000"),
                "COP",
                3);

        Account targetAccount = new Account(
                2,
                "005678",
                new BigDecimal("30000"),
                "COP",
                7);

        when(service.getAccountForUser(1, "apiuser"))
                .thenReturn(sourceAccount);

        when(repository.findById(2))
                .thenReturn(Optional.of(targetAccount));

        InsufficientBalanceException exception = assertThrows(
                InsufficientBalanceException.class,
                () -> useCase.execute(
                        1,
                        2,
                        new BigDecimal("50000"),
                        "apiuser"));

        assertEquals(
                "Insufficient balance",
                exception.getMessage());

        assertEquals(new BigDecimal("20000"), sourceAccount.getBalance());
        assertEquals(new BigDecimal("30000"), targetAccount.getBalance());

        verify(service).getAccountForUser(1, "apiuser");
        verify(repository).findById(2);
        verify(repository, never()).update(any(Account.class));
    }

    @Test
    void shouldRejectTransferToTheSameAccount() {

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(1, 1, new BigDecimal("100"), "apiuser"));

        assertEquals(
                "Source and target accounts must be different",
                exception.getMessage());

        verifyNoInteractions(service, repository);
    }

    @Test
    void shouldRejectTransferBetweenDifferentCurrencies() {

        Account sourceAccount = new Account(
                1,
                "ACC-001",
                new BigDecimal("500.00"),
                "COP",
                3);

        Account targetAccount = new Account(
                2,
                "ACC-002",
                new BigDecimal("200.00"),
                "USD",
                7);

        when(service.getAccountForUser(1, "apiuser"))
                .thenReturn(sourceAccount);

        when(repository.findById(2))
                .thenReturn(Optional.of(targetAccount));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(1, 2, new BigDecimal("100"), "apiuser"));

        assertEquals(
                "Source and target accounts must use the same currency",
                exception.getMessage());

        assertEquals(new BigDecimal("500.00"), sourceAccount.getBalance());
        assertEquals(new BigDecimal("200.00"), targetAccount.getBalance());

        verify(service).getAccountForUser(1, "apiuser");
        verify(repository).findById(2);
        verify(repository, never()).update(any(Account.class));
    }

    @Test
    void shouldPropagateExceptionWhenUpdatingTargetAccountFails() {

        Account sourceAccount = new Account(
                1,
                "001234",
                new BigDecimal("60000"),
                "COP",
                3);

        Account targetAccount = new Account(
                2,
                "005678",
                new BigDecimal("20000"),
                "COP",
                7);

        when(service.getAccountForUser(1, "apiuser"))
                .thenReturn(sourceAccount);

        when(repository.findById(2))
                .thenReturn(Optional.of(targetAccount));

        doAnswer(invocation -> {
            Account account = invocation.getArgument(0);

            if (account == targetAccount) {
                throw new RuntimeException("Database update failed");
            }

            return account;
        }).when(repository).update(any(Account.class));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> useCase.execute(
                        1,
                        2,
                        new BigDecimal("20000"),
                        "apiuser"));

        assertEquals("Database update failed", exception.getMessage());

        verify(repository).update(sourceAccount);
        verify(repository).update(targetAccount);
    }

}
