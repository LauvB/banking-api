package com.laurabeltran.banking_api.application.transfer.dto;

import java.math.BigDecimal;

public record TransferResponse(
        int sourceAccountId,
        int targetAccountId,
        BigDecimal amount,
        BigDecimal sourceBalance,
        BigDecimal targetBalance) {

}
