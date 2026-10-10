package com.laurabeltran.banking_api.infrastructure.web.account;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.laurabeltran.banking_api.application.account.port.AccountRepository;
import com.laurabeltran.banking_api.domain.account.Account;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountOperationSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountRepository accountRepository;

    private String userCredentials() {
        return "Basic " + Base64.getEncoder().encodeToString(
                "apiuser:TestPass123!".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void shouldAllowUserToDeposit() throws Exception {
        Account account = new Account(
                1, "ACC-001", new BigDecimal("500.00"), "COP");

        when(accountRepository.findById(1)).thenReturn(Optional.of(account));
        when(accountRepository.update(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/accounts/1/deposit")
                .with(csrf())
                .header(HttpHeaders.AUTHORIZATION, userCredentials())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"amount": 100}
                        """))
                .andExpect(status().isOk());

        verify(accountRepository).update(any(Account.class));
    }

    @Test
    void shouldAllowUserToWithdraw() throws Exception {
        Account account = new Account(
                1, "ACC-001", new BigDecimal("500.00"), "COP");

        when(accountRepository.findById(1)).thenReturn(Optional.of(account));
        when(accountRepository.update(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/accounts/1/withdraw")
                .with(csrf())
                .header(HttpHeaders.AUTHORIZATION, userCredentials())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"amount": 100}
                        """))
                .andExpect(status().isOk());

        verify(accountRepository).update(any(Account.class));
    }

}
