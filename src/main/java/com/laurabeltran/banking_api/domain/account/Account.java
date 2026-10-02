package com.laurabeltran.banking_api.domain.account;

import java.math.BigDecimal;

public class Account {

    private final int id;
    private final String accountNumber;
    private BigDecimal balance;
    private final String currency;

    public Account(int id, String accountNumber, BigDecimal balance, String currency) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.currency = currency;
    }

    public void deposit(BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be positive");
        }

        balance = balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be positive");
        }

        if (amount.compareTo(balance) > 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        balance = balance.subtract(amount);
    }

    public int getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getCurrency() {
        return currency;
    }

}
