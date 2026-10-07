package com.laurabeltran.banking_api.application.transfer;

import java.math.BigDecimal;

public record TransferResult(

        int sourceAccountId,
        int targetAccountId,
        BigDecimal amount,
        BigDecimal sourceBalance,
        BigDecimal targetBalance) {

}
