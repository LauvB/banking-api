package com.laurabeltran.banking_api.application.account;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.laurabeltran.banking_api.domain.account.Account;

public class CreateAccountUseCaseTest {

    @Test
    void shouldCreateAccount() {

        CreateAccountUseCase useCase = new CreateAccountUseCase();

        Account account = useCase.execute(
                1,
                "001234",
                new BigDecimal("10000"),
                "COP");

        assertEquals(1, account.getId());

        assertEquals(
                "001234",
                account.getAccountNumber());

        assertEquals(
                new BigDecimal("10000"),
                account.getBalance());

        assertEquals(
                "COP",
                account.getCurrency());
    }

}
