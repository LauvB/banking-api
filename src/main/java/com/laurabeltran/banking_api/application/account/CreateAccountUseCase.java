package com.laurabeltran.banking_api.application.account;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.laurabeltran.banking_api.application.account.port.AccountRepository;
import com.laurabeltran.banking_api.application.user.port.UserIdentityRepository;
import com.laurabeltran.banking_api.domain.account.Account;

@Service
public class CreateAccountUseCase {

    private final AccountRepository accountRepository;
    private final UserIdentityRepository userIdentityRepository;

    public CreateAccountUseCase(AccountRepository accountRepository, UserIdentityRepository userIdentityRepository) {
        this.accountRepository = accountRepository;
        this.userIdentityRepository = userIdentityRepository;
    }

    public Account execute(String username, String accountNumber, BigDecimal balance, String currency) {

        Integer ownerId = userIdentityRepository.findIdByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + username));

        Account account = new Account(
                null,
                accountNumber,
                balance,
                currency,
                ownerId);

        return accountRepository.save(account);
    }

}
