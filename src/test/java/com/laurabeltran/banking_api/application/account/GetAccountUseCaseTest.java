package com.laurabeltran.banking_api.application.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.laurabeltran.banking_api.domain.account.Account;
import com.laurabeltran.banking_api.domain.account.AccountNotFoundException;

public class GetAccountUseCaseTest {

        @Test
        void shouldGetAccountById() {

                AccountOwnershipService accountOwnershipService = mock(AccountOwnershipService.class);

                Account expectedAccount = new Account(
                                1,
                                "001234",
                                new BigDecimal("10000"),
                                "COP",
                                3);

                when(accountOwnershipService.getAccountForUser(1, "apiuser"))
                                .thenReturn(expectedAccount);

                GetAccountUseCase useCase = new GetAccountUseCase(accountOwnershipService);

                Account account = useCase.execute(1, "apiuser");

                assertEquals(1, account.getId());
                assertEquals("001234", account.getAccountNumber());
                assertEquals(new BigDecimal("10000"), account.getBalance());
                assertEquals("COP", account.getCurrency());

                verify(accountOwnershipService).getAccountForUser(1, "apiuser");
        }

        @Test
        void shouldThrowExceptionWhenAccountDoesNotExist() {

                AccountOwnershipService accountOwnershipService = mock(AccountOwnershipService.class);

                when(accountOwnershipService.getAccountForUser(999, "apiuser"))
                                .thenThrow(new AccountNotFoundException("Account not found with id: 999"));

                GetAccountUseCase useCase = new GetAccountUseCase(accountOwnershipService);

                AccountNotFoundException exception = assertThrows(
                                AccountNotFoundException.class,
                                () -> useCase.execute(999, "apiuser"));

                assertEquals(
                                "Account not found with id: 999",
                                exception.getMessage());

                verify(accountOwnershipService).getAccountForUser(999, "apiuser");

        }

}