package com.laurabeltran.banking_api.application.transfer;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.laurabeltran.banking_api.application.account.AccountOwnershipService;
import com.laurabeltran.banking_api.application.account.port.AccountRepository;
import com.laurabeltran.banking_api.domain.account.Account;
import com.laurabeltran.banking_api.domain.account.AccountNotFoundException;

@Service
public class TransferMoneyUseCase {

    private final AccountOwnershipService accountOwnershipService;
    private final AccountRepository accountRepository;

    public TransferMoneyUseCase(AccountOwnershipService accountOwnershipService, AccountRepository accountRepository) {
        this.accountOwnershipService = accountOwnershipService;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public TransferResult execute(int sourceAccountId, int targetAccountId, BigDecimal amount, String username) {

        if (sourceAccountId == targetAccountId) {
            throw new IllegalArgumentException("Source and target accounts must be different");
        }

        Account sourceAccount = accountOwnershipService.getAccountForUser(sourceAccountId, username);

        Account targetAccount = accountRepository.findById(targetAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + targetAccountId));

        if (!sourceAccount.getCurrency().equals(targetAccount.getCurrency())) {
            throw new IllegalArgumentException("Source and target accounts must use the same currency");
        }

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
