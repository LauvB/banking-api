package com.laurabeltran.banking_api.application.account;

import org.springframework.stereotype.Service;

import com.laurabeltran.banking_api.application.account.port.AccountRepository;
import com.laurabeltran.banking_api.domain.account.Account;

@Service
public class UpdateAccountUseCase {

    private final AccountOwnershipService accountOwnershipService;
    private final AccountRepository accountRepository;

    public UpdateAccountUseCase(AccountOwnershipService accountOwnershipService, AccountRepository accountRepository) {
        this.accountOwnershipService = accountOwnershipService;
        this.accountRepository = accountRepository;
    }

    public Account execute(int id, String accountNumber, String name) {

        Account account = accountOwnershipService.getAccountForUser(id, name);

        account.updateAccountNumber(accountNumber);

        return accountRepository.update(account);
    }

}
