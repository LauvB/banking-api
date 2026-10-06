package com.laurabeltran.banking_api.application.account.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateAccountRequest(

        @NotNull Integer id,

        @NotBlank String accountNumber,

        @NotNull @PositiveOrZero BigDecimal balance,

        @NotBlank String currency) {
}
