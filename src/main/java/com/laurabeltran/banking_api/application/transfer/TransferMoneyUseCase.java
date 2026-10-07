package com.laurabeltran.banking_api.application.transfer;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.laurabeltran.banking_api.application.account.port.AccountRepository;
import com.laurabeltran.banking_api.domain.account.Account;
import com.laurabeltran.banking_api.domain.account.AccountNotFoundException;

@Service
public class TransferMoneyUseCase {

    private final AccountRepository accountRepository;

    public TransferMoneyUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public TransferResult execute(int sourceAccountId, int targetAccountId, BigDecimal amount) {

        Account sourceAccount = accountRepository.findById(sourceAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + sourceAccountId));

        Account targetAccount = accountRepository.findById(targetAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + targetAccountId));

        sourceAccount.withdraw(amount);

        targetAccount.deposit(amount);

        accountRepository.update(sourceAccount);
        accountRepository.update(targetAccount);

        return new TransferResult(
                sourceAccountId,
                targetAccountId,
                amount,
                sourceAccount.getBalance(),
                targetAccount.getBalance());

    }

}
