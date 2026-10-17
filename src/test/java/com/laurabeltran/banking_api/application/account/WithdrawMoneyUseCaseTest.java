package com.laurabeltran.banking_api.application.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.laurabeltran.banking_api.application.account.port.AccountRepository;
import com.laurabeltran.banking_api.domain.account.Account;
import com.laurabeltran.banking_api.domain.account.AccountNotFoundException;
import com.laurabeltran.banking_api.domain.account.InsufficientBalanceException;

public class WithdrawMoneyUseCaseTest {

    @Test
    void shouldWithdrawMoney() {

        AccountOwnershipService service = mock(AccountOwnershipService.class);
        AccountRepository repository = mock(AccountRepository.class);

        Account existingAccount = new Account(
                1,
                "001234",
                new BigDecimal("60000"),
                "COP",
                3);

        when(service.getAccountForUser(1, "apiuser"))
                .thenReturn(existingAccount);

        when(repository.update(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WithdrawMoneyUseCase useCase = new WithdrawMoneyUseCase(service, repository);

        Account account = useCase.execute(
                1,
                new BigDecimal("20000"),
                "apiuser");

        assertEquals(new BigDecimal("40000"), account.getBalance());

        verify(service).getAccountForUser(1, "apiuser");
        verify(repository).update(account);

    }

    @Test
    void shouldThrowExceptionWhenAccountDoesNotExist() {

        AccountOwnershipService service = mock(AccountOwnershipService.class);
        AccountRepository repository = mock(AccountRepository.class);

        when(service.getAccountForUser(999, "apiuser"))
                .thenThrow(new AccountNotFoundException(
                        "Account not found with id: 999"));

        WithdrawMoneyUseCase useCase = new WithdrawMoneyUseCase(service, repository);

        AccountNotFoundException exception = assertThrows(
                AccountNotFoundException.class,
                () -> useCase.execute(999, new BigDecimal("50000"), "apiuser"));

        assertEquals(
                "Account not found with id: 999",
                exception.getMessage());

        verify(service).getAccountForUser(999, "apiuser");
        verify(repository, never()).update(any(Account.class));

    }

    @Test
    void shouldThrowExceptionWhenBalanceIsInsufficient() {

        AccountOwnershipService service = mock(AccountOwnershipService.class);
        AccountRepository repository = mock(AccountRepository.class);

        Account existingAccount = new Account(
                1,
                "001234",
                new BigDecimal("60000"),
                "COP",
                3);

        when(service.getAccountForUser(1, "apiuser"))
                .thenReturn(existingAccount);

        WithdrawMoneyUseCase useCase = new WithdrawMoneyUseCase(service, repository);

        InsufficientBalanceException exception = assertThrows(
                InsufficientBalanceException.class,
                () -> useCase.execute(
                        1,
                        new BigDecimal("80000"),
                        "apiuser"));

        assertEquals(
                "Insufficient balance",
                exception.getMessage());

        verify(service).getAccountForUser(1, "apiuser");
        verify(repository, never()).update(any(Account.class));
    }

}
