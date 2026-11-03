package com.laurabeltran.banking_api.application.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.laurabeltran.banking_api.application.account.port.AccountRepository;
import com.laurabeltran.banking_api.application.user.port.UserIdentityRepository;
import com.laurabeltran.banking_api.domain.account.Account;

public class CreateAccountUseCaseTest {

    @Test
    void shouldCreateAccountForExistingCustomer() {
        AccountRepository accountRepository = mock(AccountRepository.class);
        UserIdentityRepository userIdentityRepository = mock(UserIdentityRepository.class);

        when(userIdentityRepository.findIdByUsername("apiuser"))
                .thenReturn(Optional.of(3));

        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> {
                    Account accountToSave = invocation.getArgument(0);
                    return new Account(
                            42,
                            accountToSave.getAccountNumber(),
                            accountToSave.getBalance(),
                            accountToSave.getCurrency(),
                            accountToSave.getOwnerId());
                });

        CreateAccountUseCase useCase = new CreateAccountUseCase(
                accountRepository,
                userIdentityRepository);

        Account account = useCase.execute(
                "apiuser",
                "001234",
                new BigDecimal("10000"),
                "COP");

        verify(userIdentityRepository).findIdByUsername("apiuser");
        verify(accountRepository).save(any(Account.class));

        assertEquals(42, account.getId());
        assertEquals("001234", account.getAccountNumber());
        assertEquals(new BigDecimal("10000"), account.getBalance());
        assertEquals("COP", account.getCurrency());
        assertEquals(3, account.getOwnerId());
    }

    @Test
    void shouldNotCreateAccountWhenCustomerDoesNotExist() {
        AccountRepository accountRepository = mock(AccountRepository.class);
        UserIdentityRepository userIdentityRepository = mock(UserIdentityRepository.class);

        when(userIdentityRepository.findIdByUsername("unknown"))
                .thenReturn(Optional.empty());

        CreateAccountUseCase useCase = new CreateAccountUseCase(
                accountRepository,
                userIdentityRepository);

        assertThrows(
                IllegalArgumentException.class,
                () -> useCase.execute(
                        "unknown",
                        "001234",
                        new BigDecimal("10000"),
                        "COP"));

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void shouldBuildAccountWithoutIdBeforePersistence() {
        AccountRepository accountRepository = mock(AccountRepository.class);
        UserIdentityRepository userIdentityRepository = mock(UserIdentityRepository.class);

        when(userIdentityRepository.findIdByUsername("apiuser"))
                .thenReturn(Optional.of(3));

        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreateAccountUseCase useCase = new CreateAccountUseCase(
                accountRepository,
                userIdentityRepository);

        useCase.execute(
                "apiuser",
                "001234",
                new BigDecimal("10000"),
                "COP");

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());

        Account accountToSave = captor.getValue();

        assertEquals(3, accountToSave.getOwnerId());
        assertNull(accountToSave.getId());

    }

}
