package com.laurabeltran.banking_api.application.transfer.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransferMoneyRequest(

        @NotNull Integer sourceAccountId,
        @NotNull Integer targetAccountId,
        @NotNull @Positive BigDecimal amount) {

}
