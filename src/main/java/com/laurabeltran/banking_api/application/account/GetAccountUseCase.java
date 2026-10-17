package com.laurabeltran.banking_api.application.account;

import org.springframework.stereotype.Service;

import com.laurabeltran.banking_api.domain.account.Account;

@Service
public class GetAccountUseCase {

    private final AccountOwnershipService accountOwnershipService;

    public GetAccountUseCase(AccountOwnershipService accountOwnershipService) {
        this.accountOwnershipService = accountOwnershipService;
    }

    public Account execute(int id, String username) {
        return accountOwnershipService.getAccountForUser(id, username);
    }

}
