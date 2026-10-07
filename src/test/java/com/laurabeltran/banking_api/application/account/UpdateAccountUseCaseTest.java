package com.laurabeltran.banking_api.application.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.laurabeltran.banking_api.application.account.port.AccountRepository;
import com.laurabeltran.banking_api.domain.account.Account;

public class UpdateAccountUseCaseTest {

    @Test
    void shouldUpdateAccountNumber() {

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

        UpdateAccountUseCase useCase = new UpdateAccountUseCase(repository);

        Account account = useCase.execute(
                1,
                "009999");

        assertEquals(1, account.getId());
        assertEquals("009999", account.getAccountNumber());
        assertEquals(new BigDecimal("10000"), account.getBalance());
        assertEquals("COP", account.getCurrency());

        verify(repository).findById(1);
        verify(repository).update(account);
    }

}
