package com.laurabeltran.banking_api.domain.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class AccountTest {

    @Test
    void shouldDepositMoney() {

        Account account = new Account(
                1,
                "001234",
                new BigDecimal("10000"),
                "COP");

        account.deposit(new BigDecimal("5000"));

        assertEquals(
                new BigDecimal("15000"),
                account.getBalance());

    }

    @Test
    void shouldNotDepositNullAmount() {

        Account account = new Account(
                1,
                "001234",
                new BigDecimal("10000"),
                "COP");

        assertThrows(
                InvalidAmountException.class,
                () -> account.deposit(null));
    }

    @Test
    void shouldNotDepositInvalidAmount() {

        Account account = new Account(
                1,
                "001234",
                new BigDecimal("10000"),
                "COP");

        assertThrows(
                InvalidAmountException.class,
                () -> account.deposit(new BigDecimal("-1500")));

    }

    @Test
    void shouldNotWithdrawMoreThanBalance() {

        Account account = new Account(
                1,
                "001234",
                new BigDecimal("10000"),
                "COP");

        assertThrows(
                InsufficientBalanceException.class,
                () -> account.withdraw(new BigDecimal("15000")));

    }

    @Test
    void shouldNotWithdrawInvalidAmount() {

        Account account = new Account(
                1,
                "001234",
                new BigDecimal("10000"),
                "COP");

        assertThrows(
                InvalidAmountException.class,
                () -> account.withdraw(new BigDecimal("-500")));
    }

}
