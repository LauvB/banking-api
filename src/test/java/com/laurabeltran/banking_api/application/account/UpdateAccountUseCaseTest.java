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

public class UpdateAccountUseCaseTest {

    @Test
    void shouldUpdateAccountNumber() {

        AccountOwnershipService service = mock(AccountOwnershipService.class);
        AccountRepository repository = mock(AccountRepository.class);

        Account existingAccount = new Account(
                1,
                "001234",
                new BigDecimal("10000"),
                "COP",
                3);

        when(service.getAccountForUser(1, "apiuser"))
                .thenReturn(existingAccount);

        when(repository.update(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UpdateAccountUseCase useCase = new UpdateAccountUseCase(service, repository);

        Account account = useCase.execute(
                1,
                "009999",
                "apiuser");

        assertEquals(1, account.getId());
        assertEquals("009999", account.getAccountNumber());
        assertEquals(new BigDecimal("10000"), account.getBalance());
        assertEquals("COP", account.getCurrency());

        verify(service).getAccountForUser(1, "apiuser");
        verify(repository).update(account);
    }

}
