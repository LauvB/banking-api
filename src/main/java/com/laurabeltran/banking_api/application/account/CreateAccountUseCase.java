package com.laurabeltran.banking_api.application.account;

import java.math.BigDecimal;

import com.laurabeltran.banking_api.domain.account.Account;

public class CreateAccountUseCase {

    public Account execute(int id, String accountNumber, BigDecimal balance, String currency) {
        return new Account(
                id,
                accountNumber,
                balance,
                currency);
    }

}
