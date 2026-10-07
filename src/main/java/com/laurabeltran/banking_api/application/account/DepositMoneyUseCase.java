package com.laurabeltran.banking_api.application.account;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.laurabeltran.banking_api.application.account.port.AccountRepository;
import com.laurabeltran.banking_api.domain.account.Account;
import com.laurabeltran.banking_api.domain.account.AccountNotFoundException;

@Service
public class DepositMoneyUseCase {

    private final AccountRepository accountRepository;

    public DepositMoneyUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account execute(int id, BigDecimal amount) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + id));

        account.deposit(amount);

        return accountRepository.update(account);
    }

}
