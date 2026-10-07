package com.laurabeltran.banking_api.infrastructure.web.exception;

import java.util.Map;

public record ErrorResponse(
        int status,
        String message,
        Map<String, String> errors) {
}
