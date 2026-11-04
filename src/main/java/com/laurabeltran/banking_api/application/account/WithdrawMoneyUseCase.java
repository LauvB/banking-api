package com.laurabeltran.banking_api.application.account;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public Account execute(int id, BigDecimal amount, String name) {
        Account account = accountOwnershipService.getAccountForUserForUpdate(id, name);

        account.withdraw(amount);

        return accountRepository.update(account);
    }

}
