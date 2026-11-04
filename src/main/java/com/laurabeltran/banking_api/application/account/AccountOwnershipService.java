package com.laurabeltran.banking_api.application.account;

import org.springframework.stereotype.Service;

import com.laurabeltran.banking_api.application.account.port.AccountOwnership;
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

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found with id: " + accountId));

        validateAccountOwnership(account, username);

        return account;
    }

    public Account getAccountForUserForUpdate(int accountId, String username) {

        Account account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found with id: " + accountId));

        validateAccountOwnership(account, username);

        return account;
    }

    public void validateAccountOwnership(Account account, String username) {

        Integer userId = userIdentityRepository.findIdByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

        if (account.getOwnerId() == null || !account.getOwnerId().equals(userId)) {
            throw new AccountAccessDeniedException("You do not have access to this account");
        }
    }

    public void validateAccountOwnershipById(int accountId, String username) {

        AccountOwnership ownership = accountRepository.findOwnershipById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found with id: " + accountId));

        Integer userId = userIdentityRepository.findIdByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

        if (ownership.ownerId() == null || !ownership.ownerId().equals(userId)) {
            throw new AccountAccessDeniedException("You do not have access to this account");
        }
    }

}
