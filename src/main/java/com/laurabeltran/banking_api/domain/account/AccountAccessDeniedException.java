package com.laurabeltran.banking_api.domain.account;

public class AccountAccessDeniedException extends RuntimeException {

    public AccountAccessDeniedException(String message) {
        super(message);
    }

}
