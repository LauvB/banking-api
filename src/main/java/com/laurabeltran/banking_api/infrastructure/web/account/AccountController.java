package com.laurabeltran.banking_api.infrastructure.web.account;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.laurabeltran.banking_api.application.account.CreateAccountUseCase;
import com.laurabeltran.banking_api.application.account.GetAccountUseCase;
import com.laurabeltran.banking_api.application.account.UpdateAccountUseCase;
import com.laurabeltran.banking_api.application.account.dto.AccountResponse;
import com.laurabeltran.banking_api.application.account.dto.CreateAccountRequest;
import com.laurabeltran.banking_api.application.account.dto.UpdateAccountRequest;
import com.laurabeltran.banking_api.domain.account.Account;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final CreateAccountUseCase createAccountUseCase;
    private final GetAccountUseCase getAccountUseCase;
    private final UpdateAccountUseCase updateAccountUseCase;

    public AccountController(
            CreateAccountUseCase createAccountUseCase,
            GetAccountUseCase getAccountUseCase,
            UpdateAccountUseCase updateAccountUseCase) {

        this.createAccountUseCase = createAccountUseCase;
        this.getAccountUseCase = getAccountUseCase;
        this.updateAccountUseCase = updateAccountUseCase;
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

    @GetMapping("/{id}")
    public AccountResponse getById(@PathVariable int id) {

        Account account = getAccountUseCase.execute(id);

        return AccountResponse.from(account);

    }

    @PutMapping("/{id}")
    public AccountResponse update(
            @PathVariable int id,
            @Valid @RequestBody UpdateAccountRequest request) {

        Account account = updateAccountUseCase.execute(
                id,
                request.accountNumber());

        return AccountResponse.from(account);
    }

}
