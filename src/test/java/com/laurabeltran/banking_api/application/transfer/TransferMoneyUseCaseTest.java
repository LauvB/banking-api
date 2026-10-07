package com.laurabeltran.banking_api.application.transfer;

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

public class TransferMoneyUseCaseTest {

        @Test
        void shouldTransferAmount() {

                AccountRepository repository = mock(AccountRepository.class);

                Account sourceAccount = new Account(
                                1,
                                "001234",
                                new BigDecimal("60000"),
                                "COP");

                Account targetAccount = new Account(
                                2,
                                "005678",
                                new BigDecimal("20000"),
                                "COP");

                when(repository.findById(1))
                                .thenReturn(Optional.of(sourceAccount));

                when(repository.findById(2))
                                .thenReturn(Optional.of(targetAccount));

                TransferMoneyUseCase useCase = new TransferMoneyUseCase(repository);

                useCase.execute(
                                1,
                                2,
                                new BigDecimal("20000"));

                assertEquals(new BigDecimal("40000"), sourceAccount.getBalance());
                assertEquals(new BigDecimal("40000"), targetAccount.getBalance());

                verify(repository).findById(1);
                verify(repository).findById(2);
                verify(repository).update(sourceAccount);
                verify(repository).update(targetAccount);
        }

        @Test
        void shouldThrowExceptionWhenSourceAccountDoesNotExist() {

                AccountRepository repository = mock(AccountRepository.class);

                when(repository.findById(1))
                                .thenReturn(Optional.empty());

                TransferMoneyUseCase useCase = new TransferMoneyUseCase(repository);

                AccountNotFoundException exception = assertThrows(
                                AccountNotFoundException.class,
                                () -> useCase.execute(
                                                1,
                                                2,
                                                new BigDecimal("20000")));

                assertEquals(
                                "Account not found with id: 1",
                                exception.getMessage());

                verify(repository).findById(1);
                verify(repository, never()).findById(2);
                verify(repository, never()).update(any(Account.class));
        }

        @Test
        void shouldThrowExceptionWhenTargetAccountDoesNotExist() {

                AccountRepository repository = mock(AccountRepository.class);

                Account sourceAccount = new Account(
                                1,
                                "001234",
                                new BigDecimal("60000"),
                                "COP");

                when(repository.findById(1))
                                .thenReturn(Optional.of(sourceAccount));

                when(repository.findById(2))
                                .thenReturn(Optional.empty());

                TransferMoneyUseCase useCase = new TransferMoneyUseCase(repository);

                AccountNotFoundException exception = assertThrows(
                                AccountNotFoundException.class,
                                () -> useCase.execute(
                                                1,
                                                2,
                                                new BigDecimal("20000")));

                assertEquals(
                                "Account not found with id: 2",
                                exception.getMessage());

                verify(repository).findById(1);
                verify(repository).findById(2);
                verify(repository, never()).update(any(Account.class));
        }

        @Test
        void shouldThrowExceptionWhenSourceAccountHasInsufficientBalance() {

                AccountRepository repository = mock(AccountRepository.class);

                Account sourceAccount = new Account(
                                1,
                                "001234",
                                new BigDecimal("20000"),
                                "COP");

                Account targetAccount = new Account(
                                2,
                                "005678",
                                new BigDecimal("30000"),
                                "COP");

                when(repository.findById(1))
                                .thenReturn(Optional.of(sourceAccount));

                when(repository.findById(2))
                                .thenReturn(Optional.of(targetAccount));

                TransferMoneyUseCase useCase = new TransferMoneyUseCase(repository);

                InsufficientBalanceException exception = assertThrows(
                                InsufficientBalanceException.class,
                                () -> useCase.execute(
                                                1,
                                                2,
                                                new BigDecimal("50000")));

                assertEquals(
                                "Insufficient balance",
                                exception.getMessage());

                assertEquals(new BigDecimal("20000"), sourceAccount.getBalance());
                assertEquals(new BigDecimal("30000"), targetAccount.getBalance());

                verify(repository).findById(1);
                verify(repository).findById(2);
                verify(repository, never()).update(any(Account.class));
        }

}
