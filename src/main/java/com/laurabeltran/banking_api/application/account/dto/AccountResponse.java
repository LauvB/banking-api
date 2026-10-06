package com.laurabeltran.banking_api.application.account.dto;

import java.math.BigDecimal;

import com.laurabeltran.banking_api.domain.account.Account;

public record AccountResponse(
        int id,
        String accountNumber,
        BigDecimal balance,
        String currency) {

    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getCurrency());
    }

}
