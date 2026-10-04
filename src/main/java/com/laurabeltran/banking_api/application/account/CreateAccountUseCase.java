package com.laurabeltran.banking_api.application.account;

import java.math.BigDecimal;

import com.laurabeltran.banking_api.application.account.port.AccountRepository;
import com.laurabeltran.banking_api.domain.account.Account;

public class CreateAccountUseCase {

    private final AccountRepository accountRepository;

    public CreateAccountUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account execute(int id, String accountNumber, BigDecimal balance, String currency) {
        Account account = new Account(
                id,
                accountNumber,
                balance,
                currency);

        return accountRepository.save(account);
    }

}
