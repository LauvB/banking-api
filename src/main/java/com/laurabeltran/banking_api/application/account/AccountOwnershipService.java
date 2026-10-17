package com.laurabeltran.banking_api.application.account;

import org.springframework.stereotype.Service;

import com.laurabeltran.banking_api.application.account.port.AccountRepository;
import com.laurabeltran.banking_api.application.user.port.UserIdentityRepository;
import com.laurabeltran.banking_api.domain.account.Account;
import com.laurabeltran.banking_api.domain.account.AccountAccessDeniedException;
import com.laurabeltran.banking_api.domain.account.AccountNotFoundException;

@Service
public class AccountOwnershipService {

    private final AccountRepository accountRepository;
    private final UserIdentityRepository userIdentityRepository;

    public AccountOwnershipService(AccountRepository accountRepository, UserIdentityRepository userIdentityRepository) {
        this.accountRepository = accountRepository;
        this.userIdentityRepository = userIdentityRepository;
    }

    public Account getAccountForUser(int accountId, String username) {

        Integer userId = userIdentityRepository.findIdByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found with id: " + accountId));

        if (account.getOwnerId() == null || !account.getOwnerId().equals(userId)) {
            throw new AccountAccessDeniedException("You do not have access to this account");
        }

        return account;
    }

}
