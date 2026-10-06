package com.laurabeltran.banking_api.infrastructure.persistence.account;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "accounts")
public class AccountEntity {

    @Id
    private Integer id;

    private String accountNumber;

    private BigDecimal balance;

    private String currency;

    protected AccountEntity() {
    }

    public AccountEntity(Integer id, String accountNumber, BigDecimal balance, String currency) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.currency = currency;
    }

    public Integer getId() {
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
