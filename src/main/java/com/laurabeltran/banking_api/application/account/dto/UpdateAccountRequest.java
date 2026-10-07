package com.laurabeltran.banking_api.application.account.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateAccountRequest(

        @NotBlank String accountNumber) {

}
