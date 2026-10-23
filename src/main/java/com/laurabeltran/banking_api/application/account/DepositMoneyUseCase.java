package com.laurabeltran.banking_api.application.account;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.laurabeltran.banking_api.application.account.port.AccountRepository;
import com.laurabeltran.banking_api.domain.account.Account;

@Service
public class DepositMoneyUseCase {

    private final AccountOwnershipService accountOwnershipService;
    private final AccountRepository accountRepository;

    public DepositMoneyUseCase(AccountOwnershipService accountOwnershipService, AccountRepository accountRepository) {
        this.accountOwnershipService = accountOwnershipService;
        this.accountRepository = accountRepository;
    }

    public Account execute(int id, BigDecimal amount, String username) {
        Account account = accountOwnershipService.getAccountForUser(id, username);

        account.deposit(amount);

        return accountRepository.update(account);
    }

}
