package com.laurabeltran.banking_api.application.transfer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.laurabeltran.banking_api.application.account.AccountOwnershipService;
import com.laurabeltran.banking_api.application.account.port.AccountRepository;
import com.laurabeltran.banking_api.domain.account.Account;
import com.laurabeltran.banking_api.domain.account.AccountNotFoundException;
import com.laurabeltran.banking_api.domain.account.InsufficientBalanceException;

public class TransferMoneyUseCaseTest {

    @Test
    void shouldTransferAmount() {

        AccountOwnershipService service = mock(AccountOwnershipService.class);
        AccountRepository repository = mock(AccountRepository.class);

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

        TransferMoneyUseCase useCase = new TransferMoneyUseCase(service, repository);

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

        AccountOwnershipService service = mock(AccountOwnershipService.class);
        AccountRepository repository = mock(AccountRepository.class);

        when(service.getAccountForUser(1, "apiuser"))
                .thenThrow(new AccountNotFoundException(
                        "Account not found with id: 1"));

        TransferMoneyUseCase useCase = new TransferMoneyUseCase(service, repository);

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

        AccountOwnershipService service = mock(AccountOwnershipService.class);
        AccountRepository repository = mock(AccountRepository.class);

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

        TransferMoneyUseCase useCase = new TransferMoneyUseCase(service, repository);

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

        AccountOwnershipService service = mock(AccountOwnershipService.class);
        AccountRepository repository = mock(AccountRepository.class);

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

        TransferMoneyUseCase useCase = new TransferMoneyUseCase(service, repository);

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
        AccountOwnershipService service = mock(AccountOwnershipService.class);
        AccountRepository repository = mock(AccountRepository.class);

        TransferMoneyUseCase useCase = new TransferMoneyUseCase(service, repository);

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
        AccountOwnershipService service = mock(AccountOwnershipService.class);
        AccountRepository repository = mock(AccountRepository.class);

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

        TransferMoneyUseCase useCase = new TransferMoneyUseCase(service, repository);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(1, 2, new BigDecimal("100"), "apiuser"));

        assertEquals(
                "Source and target accounts must use the same currency",
                exception.getMessage());

        assertEquals(new BigDecimal("500.00"), sourceAccount.getBalance());
        assertEquals(new BigDecimal("200.00"), targetAccount.getBalance());

        verify(repository, never()).update(any(Account.class));
    }

    @Test
    void shouldPropagateExceptionWhenUpdatingTargetAccountFails() {
        AccountOwnershipService service = mock(AccountOwnershipService.class);
        AccountRepository repository = mock(AccountRepository.class);

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

        doThrow(new RuntimeException("Database update failed"))
                .when(repository).update(targetAccount);

        TransferMoneyUseCase useCase = new TransferMoneyUseCase(service, repository);

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
