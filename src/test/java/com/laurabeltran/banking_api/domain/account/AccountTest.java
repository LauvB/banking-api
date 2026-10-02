package com.laurabeltran.banking_api.domain.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class AccountTest {

    @Test
    void shouldDeposityMoney() {

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
    void shouldNotWithdrawMoreThanBalance() {

        Account account = new Account(
                1,
                "005678",
                new BigDecimal("10000"),
                "COP");

        assertThrows(
                IllegalArgumentException.class,
                () -> account.withdraw(new BigDecimal("15000")));

    }

}
