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
import com.laurabeltran.banking_api.application.user.port.UserIdentityRepository;
import com.laurabeltran.banking_api.domain.account.Account;
import com.laurabeltran.banking_api.domain.account.AccountAccessDeniedException;
import com.laurabeltran.banking_api.domain.account.AccountNotFoundException;

public class AccountOwnershipServiceTest {

    @Test
    void shouldAllowUserToAccessOwnAccount() {

        AccountRepository accountRepository = mock(AccountRepository.class);
        UserIdentityRepository userIdentityRepository = mock(UserIdentityRepository.class);

        Account account = new Account(
                1,
                "001234",
                new BigDecimal("10000"),
                "COP",
                3);

        when(userIdentityRepository.findIdByUsername("apiuser"))
                .thenReturn(Optional.of(3));

        when(accountRepository.findById(1))
                .thenReturn(Optional.of(account));

        AccountOwnershipService service = new AccountOwnershipService(accountRepository, userIdentityRepository);

        Account result = service.getAccountForUser(1, "apiuser");

        assertEquals(1, result.getId());
        assertEquals(3, result.getOwnerId());

        verify(userIdentityRepository).findIdByUsername("apiuser");
        verify(accountRepository).findById(1);
    }

    @Test
    void shouldRejectUserAccessToAnotherUsersAccount() {

        AccountRepository accountRepository = mock(AccountRepository.class);
        UserIdentityRepository userIdentityRepository = mock(UserIdentityRepository.class);

        Account account = new Account(
                2,
                "005678",
                new BigDecimal("40000"),
                "COP",
                7);

        when(userIdentityRepository.findIdByUsername("apiuser"))
                .thenReturn(Optional.of(3));

        when(accountRepository.findById(2))
                .thenReturn(Optional.of(account));

        AccountOwnershipService service = new AccountOwnershipService(accountRepository, userIdentityRepository);

        AccountAccessDeniedException exception = assertThrows(
                AccountAccessDeniedException.class,
                () -> service.getAccountForUser(2, "apiuser"));

        assertEquals(
                "You do not have access to this account",
                exception.getMessage());

    }

    @Test
    void shouldThrowExceptionWhenAccountDoesNotExist() {

        AccountRepository accountRepository = mock(AccountRepository.class);
        UserIdentityRepository userIdentityRepository = mock(UserIdentityRepository.class);

        when(userIdentityRepository.findIdByUsername("apiuser"))
                .thenReturn(Optional.of(3));

        when(accountRepository.findById(999))
                .thenReturn(Optional.empty());

        AccountOwnershipService service = new AccountOwnershipService(
                accountRepository,
                userIdentityRepository);

        assertThrows(AccountNotFoundException.class,
                () -> service.getAccountForUser(999, "apiuser"));
    }

}
