package com.laurabeltran.banking_api.infrastructure.web.account;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.laurabeltran.banking_api.application.account.CreateAccountUseCase;
import com.laurabeltran.banking_api.application.account.dto.AccountResponse;
import com.laurabeltran.banking_api.application.account.dto.CreateAccountRequest;
import com.laurabeltran.banking_api.domain.account.Account;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final CreateAccountUseCase createAccountUseCase;

    public AccountController(CreateAccountUseCase createAccountUseCase) {
        this.createAccountUseCase = createAccountUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse create(
            @Valid @RequestBody CreateAccountRequest request) {

        Account account = createAccountUseCase.execute(
                request.id(),
                request.accountNumber(),
                request.balance(),
                request.currency());

        return AccountResponse.from(account);

    }

}
