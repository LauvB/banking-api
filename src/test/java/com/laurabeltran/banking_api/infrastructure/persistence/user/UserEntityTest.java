package com.laurabeltran.banking_api.infrastructure.persistence.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class UserEntityTest {

    @Test
    void shouldCreateUserEntityWithProvidedValues() {
        UserEntity user = new UserEntity(
                "apiuser",
                "$2a$10$encodedPassword",
                "USER");

        assertNull(user.getId());
        assertEquals("apiuser", user.getUsername());
        assertEquals("$2a$10$encodedPassword", user.getPasswordHash());
        assertEquals("USER", user.getRole());
    }

}
