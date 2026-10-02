package com.laurabeltran.banking_api.domain.account;

public class InvalidAmountException extends RuntimeException {

    public InvalidAmountException(String message) {
        super(message);
    }

}
