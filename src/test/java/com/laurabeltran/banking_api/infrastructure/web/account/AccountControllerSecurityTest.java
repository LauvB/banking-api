package com.laurabeltran.banking_api.infrastructure.web.account;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

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

}
