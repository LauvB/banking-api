package com.laurabeltran.banking_api.infrastructure.web.account;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRejectRequestWithoutCredentials() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/1"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void shouldRejectRequestWithInvalidCredentials() throws Exception {
        String credentials = Base64.getEncoder().encodeToString(
                "apiuser:wrong-password".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/1")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

}
