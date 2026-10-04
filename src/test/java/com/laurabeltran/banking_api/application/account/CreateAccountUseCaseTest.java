package com.laurabeltran.banking_api.application.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.laurabeltran.banking_api.application.account.port.AccountRepository;
import com.laurabeltran.banking_api.domain.account.Account;

public class CreateAccountUseCaseTest {

    @Test
    void shouldCreateAccount() {

        AccountRepository repository = mock(AccountRepository.class);

        when(repository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreateAccountUseCase useCase = new CreateAccountUseCase(repository);

        Account account = useCase.execute(
                1,
                "001234",
                new BigDecimal("10000"),
                "COP");

        verify(repository).save(account);

        assertEquals(1, account.getId());

        assertEquals(
                "001234",
                account.getAccountNumber());

        assertEquals(
                new BigDecimal("10000"),
                account.getBalance());

        assertEquals(
                "COP",
                account.getCurrency());
    }

}
