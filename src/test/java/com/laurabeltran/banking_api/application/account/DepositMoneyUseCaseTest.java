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

public class DepositMoneyUseCaseTest {

    @Test
    void shouldDepositMoney() {

        AccountRepository repository = mock(AccountRepository.class);

        Account existingAccount = new Account(
                1,
                "001234",
                new BigDecimal("10000"),
                "COP");

        when(repository.findById(1))
                .thenReturn(Optional.of(existingAccount));

        when(repository.update(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        DepositMoneyUseCase useCase = new DepositMoneyUseCase(repository);

        Account account = useCase.execute(
                1,
                new BigDecimal("50000"));

        assertEquals(new BigDecimal("60000"), account.getBalance());

        verify(repository).findById(1);
        verify(repository).update(account);

    }

    @Test
    void shouldThrowExceptionWhenAccountDoesNotExist() {

        AccountRepository repository = mock(AccountRepository.class);

        when(repository.findById(999))
                .thenReturn(Optional.empty());

        DepositMoneyUseCase useCase = new DepositMoneyUseCase(repository);

        AccountNotFoundException exception = assertThrows(
                AccountNotFoundException.class,
                () -> useCase.execute(999, new BigDecimal("50000")));

        assertEquals(
                "Account not found with id: 999",
                exception.getMessage());

        verify(repository).findById(999);
        verify(repository, never()).update(any(Account.class));

    }
}
