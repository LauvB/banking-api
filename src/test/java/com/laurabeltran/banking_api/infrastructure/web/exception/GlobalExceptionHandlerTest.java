package com.laurabeltran.banking_api.infrastructure.web.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.laurabeltran.banking_api.application.account.dto.CreateAccountRequest;
import com.laurabeltran.banking_api.domain.account.AccountAccessDeniedException;
import com.laurabeltran.banking_api.domain.account.AccountNotFoundException;
import com.laurabeltran.banking_api.domain.account.CustomerNotFoundException;
import com.laurabeltran.banking_api.domain.account.InsufficientBalanceException;
import com.laurabeltran.banking_api.domain.account.InvalidAmountException;

import jakarta.validation.Valid;

public class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ExceptionTestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @RestController
    static class ExceptionTestController {

        @GetMapping("/test/invalid-amount")
        void invalidAmount() {
            throw new InvalidAmountException("Amount must be positive");
        }

        @PostMapping("/test/validation")
        void validation(@Valid @RequestBody CreateAccountRequest request) {
        }

        @GetMapping("/test/account-not-found")
        void accountNotFound() {
            throw new AccountNotFoundException("Account not found");
        }

        @GetMapping("/test/insufficient-balance")
        void insufficientBalance() {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        @GetMapping("/test/access-denied")
        void accessDenied() {
            throw new AccountAccessDeniedException("Access denied");
        }

        @GetMapping("/test/illegal-argument")
        void illegalArgument() {
            throw new IllegalArgumentException("Invalid argument");
        }

        @GetMapping("/test/customer-not-found")
        void customerNotFound() {
            throw new CustomerNotFoundException("Customer not found");
        }

        @GetMapping("/test/illegal-state")
        void illegalState() {
            throw new IllegalStateException("Internal database detail");
        }
    }

    @Test
    void shouldReturnBadRequestWhenAmountIsInvalid() throws Exception {
        mockMvc.perform(get("/test/invalid-amount"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid amount"))
                .andExpect(jsonPath("$.errors.amount").value("Amount must be positive"));
    }

    @Test
    void shouldReturnBadRequestWhenRequestValidationFails() throws Exception {
        mockMvc.perform(post("/test/validation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "username": "",
                            "accountNumber": "123456",
                            "balance": -100,
                            "currency": "USD"
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.username").exists())
                .andExpect(jsonPath("$.errors.balance").exists());
    }

    @Test
    void shouldReturnNotFoundWhenAccountDoesNotExist() throws Exception {
        mockMvc.perform(get("/test/account-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Account not found"))
                .andExpect(jsonPath("$.errors").doesNotExist());
    }

    @Test
    void shouldReturnBadRequestWhenBalanceIsInsufficient() throws Exception {
        mockMvc.perform(get("/test/insufficient-balance"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Insufficient balance"))
                .andExpect(jsonPath("$.errors").doesNotExist());
    }

    @Test
    void shouldReturnForbiddenWhenAccountAccessIsDenied() throws Exception {
        mockMvc.perform(get("/test/access-denied"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("Access denied"))
                .andExpect(jsonPath("$.errors").doesNotExist());
    }

    @Test
    void shouldReturnBadRequestWhenArgumentIsIllegal() throws Exception {
        mockMvc.perform(get("/test/illegal-argument"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid argument"))
                .andExpect(jsonPath("$.errors").doesNotExist());
    }

    @Test
    void shouldReturnNotFoundWhenCustomerDoesNotExist() throws Exception {
        mockMvc.perform(get("/test/customer-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Customer not found"))
                .andExpect(jsonPath("$.errors").doesNotExist());
    }

    @Test
    void shouldReturnInternalServerErrorWithoutExposingInternalDetails() throws Exception {
        mockMvc.perform(get("/test/illegal-state"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"))
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.not("Internal database detail")))
                .andExpect(jsonPath("$.errors").doesNotExist());
    }

}
