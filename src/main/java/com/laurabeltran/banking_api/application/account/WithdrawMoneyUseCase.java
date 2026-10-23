package com.laurabeltran.banking_api.application.account;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.laurabeltran.banking_api.application.account.port.AccountRepository;
import com.laurabeltran.banking_api.domain.account.Account;

@Service
public class WithdrawMoneyUseCase {

    private final AccountOwnershipService accountOwnershipService;
    private final AccountRepository accountRepository;

    public WithdrawMoneyUseCase(AccountOwnershipService accountOwnershipService, AccountRepository accountRepository) {
        this.accountOwnershipService = accountOwnershipService;
        this.accountRepository = accountRepository;
    }

    public Account execute(int id, BigDecimal amount, String name) {
        Account account = accountOwnershipService.getAccountForUser(id, name);

        account.withdraw(amount);

        return accountRepository.update(account);
    }

}
