package com.laurabeltran.banking_api.infrastructure.web.transfer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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
import com.laurabeltran.banking_api.application.user.port.UserIdentityRepository;
import com.laurabeltran.banking_api.domain.account.Account;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TransferOperationSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountRepository accountRepository;

    @MockitoBean
    private UserIdentityRepository userIdentityRepository;

    private String userCredentials() {
        return "Basic " + Base64.getEncoder().encodeToString(
                "apiuser:TestPass123!".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void shouldAllowUserToTransferMoney() throws Exception {
        Account sourceAccount = new Account(
                1,
                "ACC-001",
                new BigDecimal("500.00"),
                "COP",
                3);

        Account targetAccount = new Account(
                2,
                "ACC-002",
                new BigDecimal("200.00"),
                "COP",
                7);

        when(userIdentityRepository.findIdByUsername("apiuser"))
                .thenReturn(Optional.of(3));

        when(accountRepository.findById(1))
                .thenReturn(Optional.of(sourceAccount));

        when(accountRepository.findById(2))
                .thenReturn(Optional.of(targetAccount));

        when(accountRepository.update(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/transfers")
                .with(csrf())
                .header(HttpHeaders.AUTHORIZATION, userCredentials())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "sourceAccountId": 1,
                            "targetAccountId": 2,
                            "amount": 100
                        }
                        """))
                .andExpect(status().isOk());

        verify(userIdentityRepository).findIdByUsername("apiuser");
        verify(accountRepository, times(2)).update(any(Account.class));
    }

    @Test
    void shouldForbidTransferFromAnotherUsersAccount() throws Exception {
        Account anotherUserAccount = new Account(
                1,
                "ACC-001",
                new BigDecimal("500"),
                "COP",
                7);

        when(userIdentityRepository.findIdByUsername("apiuser"))
                .thenReturn(Optional.of(3));

        when(accountRepository.findById(1))
                .thenReturn(Optional.of(anotherUserAccount));

        mockMvc.perform(post("/transfers")
                .with(csrf())
                .header(HttpHeaders.AUTHORIZATION, userCredentials())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "sourceAccountId": 1,
                            "targetAccountId": 2,
                            "amount": 100
                        }
                        """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));

        verify(accountRepository, never()).update(any(Account.class));
    }

    @Test
    void shouldRejectTransferToTheSameAccount() throws Exception {
        mockMvc.perform(post("/transfers")
                .with(csrf())
                .header(HttpHeaders.AUTHORIZATION, userCredentials())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "sourceAccountId": 1,
                            "targetAccountId": 1,
                            "amount": 100
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(accountRepository, never()).update(any(Account.class));
    }

    @Test
    void shouldRejectTransferBetweenDifferentCurrencies() throws Exception {
        Account sourceAccount = new Account(
                1,
                "ACC-001",
                new BigDecimal("500.00"),
                "COP",
                3);

        Account targetAccount = new Account(
                2,
                "ACC-002",
                new BigDecimal("200.00"),
                "USD",
                7);

        when(userIdentityRepository.findIdByUsername("apiuser"))
                .thenReturn(Optional.of(3));

        when(accountRepository.findById(1))
                .thenReturn(Optional.of(sourceAccount));

        when(accountRepository.findById(2))
                .thenReturn(Optional.of(targetAccount));

        mockMvc.perform(post("/transfers")
                .with(csrf())
                .header(HttpHeaders.AUTHORIZATION, userCredentials())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "sourceAccountId": 1,
                            "targetAccountId": 2,
                            "amount": 100
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        verify(accountRepository, never()).update(any(Account.class));
    }
}
