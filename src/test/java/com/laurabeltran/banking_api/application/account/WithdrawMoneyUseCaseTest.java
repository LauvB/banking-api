package com.laurabeltran.banking_api.application.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.laurabeltran.banking_api.application.account.port.AccountRepository;
import com.laurabeltran.banking_api.domain.account.Account;
import com.laurabeltran.banking_api.domain.account.AccountNotFoundException;
import com.laurabeltran.banking_api.domain.account.InsufficientBalanceException;

public class WithdrawMoneyUseCaseTest {

    @Test
    void shouldWithdrawMoney() {

        AccountRepository repository = mock(AccountRepository.class);

        Account existingAccount = new Account(
                1,
                "001234",
                new BigDecimal("60000"),
                "COP");

        when(repository.findById(1))
                .thenReturn(Optional.of(existingAccount));

        when(repository.update(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WithdrawMoneyUseCase useCase = new WithdrawMoneyUseCase(repository);

        Account account = useCase.execute(
                1,
                new BigDecimal("20000"));

        assertEquals(new BigDecimal("40000"), account.getBalance());

        verify(repository).findById(1);
        verify(repository).update(account);

    }

    @Test
    void shouldThrowExceptionWhenAccountDoesNotExist() {

        AccountRepository repository = mock(AccountRepository.class);

        when(repository.findById(999))
                .thenReturn(Optional.empty());

        WithdrawMoneyUseCase useCase = new WithdrawMoneyUseCase(repository);

        AccountNotFoundException exception = assertThrows(
                AccountNotFoundException.class,
                () -> useCase.execute(999, new BigDecimal("50000")));

        assertEquals(
                "Account not found with id: 999",
                exception.getMessage());

        verify(repository).findById(999);
        verify(repository, never()).update(any(Account.class));

    }

    @Test
    void shouldThrowExceptionWhenBalanceIsInsufficient() {

        AccountRepository repository = mock(AccountRepository.class);

        Account existingAccount = new Account(
                1,
                "001234",
                new BigDecimal("60000"),
                "COP");

        when(repository.findById(1))
                .thenReturn(Optional.of(existingAccount));

        WithdrawMoneyUseCase useCase = new WithdrawMoneyUseCase(repository);

        InsufficientBalanceException exception = assertThrows(
                InsufficientBalanceException.class,
                () -> useCase.execute(
                        1,
                        new BigDecimal("80000")));

        assertEquals(
                "Insufficient balance",
                exception.getMessage());

        verify(repository).findById(1);
        verify(repository, never()).update(any(Account.class));
    }

}
