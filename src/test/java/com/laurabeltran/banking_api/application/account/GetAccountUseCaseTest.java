package com.laurabeltran.banking_api.application.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.laurabeltran.banking_api.application.account.port.AccountRepository;
import com.laurabeltran.banking_api.domain.account.Account;
import com.laurabeltran.banking_api.domain.account.AccountNotFoundException;

public class GetAccountUseCaseTest {

    @Test
    void shouldGetAccountById() {

        AccountRepository repository = mock(AccountRepository.class);

        Account expectedAccount = new Account(
                1,
                "001234",
                new BigDecimal("10000"),
                "COP");

        when(repository.findById(1))
                .thenReturn(Optional.of(expectedAccount));

        GetAccountUseCase useCase = new GetAccountUseCase(repository);

        Account account = useCase.execute(1);

        assertEquals(1, account.getId());
        assertEquals("001234", account.getAccountNumber());
        assertEquals(new BigDecimal("10000"), account.getBalance());
        assertEquals("COP", account.getCurrency());

        verify(repository).findById(1);
    }

    @Test
    void shouldThrowExceptionWhenAccountDoesNotExist() {

        AccountRepository repository = mock(AccountRepository.class);

        when(repository.findById(999))
                .thenReturn(Optional.empty());

        GetAccountUseCase useCase = new GetAccountUseCase(repository);

        AccountNotFoundException exception = assertThrows(
                AccountNotFoundException.class,
                () -> useCase.execute(999));

        assertEquals(
                "Account not found with id: 999",
                exception.getMessage());

        verify(repository).findById(999);

    }

}