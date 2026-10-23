package com.laurabeltran.banking_api.infrastructure.web.account;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountRepository accountRepository;

    @MockitoBean
    private UserIdentityRepository userIdentityRepository;

    @Test
    void shouldRejectRequestWithoutCredentials() throws Exception {
        mockMvc.perform(get("/accounts/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectRequestWithInvalidCredentials() throws Exception {
        String credentials = Base64.getEncoder().encodeToString(
                "apiuser:wrong-password".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(get("/accounts/1")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAuthenticateRequestWithValidCredentials() throws Exception {
        String credentials = Base64.getEncoder().encodeToString(
                "apiuser:TestPass123!".getBytes(StandardCharsets.UTF_8));

        when(userIdentityRepository.findIdByUsername("apiuser"))
                .thenReturn(Optional.of(3));

        when(accountRepository.findById(1))
                .thenReturn(Optional.of(
                        new Account(
                                1,
                                "009999",
                                new BigDecimal("20000.00"),
                                "COP",
                                3)));

        mockMvc.perform(get("/accounts/1")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials))
                .andExpect(authenticated().withUsername("apiuser"));
    }

    @Test
    void shouldForbidAccountCreationForUserRole() throws Exception {
        String credentials = Base64.getEncoder().encodeToString(
                "apiuser:TestPass123!".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(post("/accounts")
                .with(csrf())
                .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowAccountCreationForAdminRole() throws Exception {
        String credentials = Base64.getEncoder().encodeToString(
                "admin:AdminPass123!".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(post("/accounts")
                .with(csrf())
                .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectAccountUpdateWithoutCredentials() throws Exception {
        mockMvc.perform(put("/accounts/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectDepositWithoutCredentials() throws Exception {
        mockMvc.perform(post("/accounts/1/deposit")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectWithdrawalWithoutCredentials() throws Exception {
        mockMvc.perform(post("/accounts/1/withdraw")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldForbidUserFromAccessingAnotherUsersAccount() throws Exception {
        String credentials = Base64.getEncoder().encodeToString(
                "apiuser:TestPass123!".getBytes(StandardCharsets.UTF_8));

        Account accountOwnedByAnotherUser = new Account(
                999,
                "ACC-999",
                new BigDecimal("50000.00"),
                "COP",
                7);

        when(userIdentityRepository.findIdByUsername("apiuser"))
                .thenReturn(Optional.of(3));

        when(accountRepository.findById(999))
                .thenReturn(Optional.of(accountOwnedByAnotherUser));

        mockMvc.perform(get("/accounts/999")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void shouldAllowUserToUpdateTheirAccount() throws Exception {
        String credentials = Base64.getEncoder().encodeToString(
                "apiuser:TestPass123!".getBytes(StandardCharsets.UTF_8));

        Account account = new Account(
                1,
                "ACC-001",
                new BigDecimal("500.00"),
                "COP",
                3);

        when(userIdentityRepository.findIdByUsername("apiuser"))
                .thenReturn(Optional.of(3));

        when(accountRepository.findById(1))
                .thenReturn(Optional.of(account));

        when(accountRepository.update(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/accounts/1")
                .with(csrf())
                .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"accountNumber": "ACC-999"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("ACC-999"));

        verify(userIdentityRepository).findIdByUsername("apiuser");
        verify(accountRepository).update(any(Account.class));
    }

    @Test
    void shouldForbidUserFromUpdatingFromAnotherUsersAccount() throws Exception {
        String credentials = Base64.getEncoder().encodeToString(
                "apiuser:TestPass123!".getBytes(StandardCharsets.UTF_8));

        Account anotherUsersAccount = new Account(
                2,
                "ACC-002",
                new BigDecimal("500.00"),
                "COP",
                7);

        when(userIdentityRepository.findIdByUsername("apiuser"))
                .thenReturn(Optional.of(3));

        when(accountRepository.findById(2))
                .thenReturn(Optional.of(anotherUsersAccount));

        mockMvc.perform(put("/accounts/2")
                .with(csrf())
                .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"accountNumber": "ACC-999"}
                        """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));

        verify(userIdentityRepository).findIdByUsername("apiuser");
        verify(accountRepository).findById(2);
        verify(accountRepository, never()).update(any(Account.class));

    }

}
