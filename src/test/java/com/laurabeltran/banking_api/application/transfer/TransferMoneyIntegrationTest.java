package com.laurabeltran.banking_api.application.transfer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
public class TransferMoneyIntegrationTest {

    private static final int SOURCE_ACCOUNT_ID = 9001;
    private static final int TARGET_ACCOUNT_ID = 9002;
    private static final String USERNAME = "transfer-integration-test";

    private static final BigDecimal SOURCE_INITIAL_BALANCE = new BigDecimal("100000.00");
    private static final BigDecimal TARGET_INITIAL_BALANCE = new BigDecimal("50000.00");
    private static final BigDecimal TRANSFER_AMOUNT = new BigDecimal("20000.00");

    @SuppressWarnings({ "deprecation", "resource" })
    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("banking-test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
        registry.add("spring.jpa.show-sql", () -> "false");
    }

    @Autowired
    private TransferMoneyUseCase transferMoneyUseCase;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("""
                DROP TRIGGER IF EXISTS fail_target_account_update_trigger
                ON accounts
                """);

        jdbcTemplate.execute("DROP FUNCTION IF EXISTS fail_target_account_update()");

        jdbcTemplate.update(
                "DELETE FROM accounts WHERE id IN (?, ?)",
                SOURCE_ACCOUNT_ID, TARGET_ACCOUNT_ID);

        jdbcTemplate.update(
                "DELETE FROM users WHERE username = ?",
                USERNAME);

        jdbcTemplate.update(
                "INSERT INTO users (username, password, role) VALUES (?, ?, ?)",
                USERNAME, "not-a-real-password", "ROLE_USER");

        Integer userId = jdbcTemplate.queryForObject(
                "SELECT id FROM users WHERE username = ?",
                Integer.class,
                USERNAME);

        jdbcTemplate.update("""
                INSERT INTO accounts (id, account_number, balance, currency, user_id)
                VALUES (?, ?, ?, ?, ?)
                """,
                SOURCE_ACCOUNT_ID, "TEST-9001",
                SOURCE_INITIAL_BALANCE, "COP", userId);

        jdbcTemplate.update("""
                INSERT INTO accounts (id, account_number, balance, currency, user_id)
                VALUES (?, ?, ?, ?, ?)
                """,
                TARGET_ACCOUNT_ID, "TEST-9002",
                TARGET_INITIAL_BALANCE, "COP", userId);
    }

    @Test
    void shouldPersistBothAccountBalancesWhenTransferSucceeds() {
        TransferResult result = transferMoneyUseCase.execute(
                SOURCE_ACCOUNT_ID,
                TARGET_ACCOUNT_ID,
                TRANSFER_AMOUNT,
                USERNAME);

        assertThat(result.sourceBalance()).isEqualByComparingTo("80000.00");
        assertThat(result.targetBalance()).isEqualByComparingTo("70000.00");

        assertPersistedBalance(SOURCE_ACCOUNT_ID, "80000.00");
        assertPersistedBalance(TARGET_ACCOUNT_ID, "70000.00");
    }

    @Test
    void shouldRollbackBothAccountsWhenTargetUpdateFails() {
        createFailingTargetUpdateTrigger();

        assertThatThrownBy(() -> transferMoneyUseCase.execute(
                SOURCE_ACCOUNT_ID,
                TARGET_ACCOUNT_ID,
                TRANSFER_AMOUNT,
                USERNAME))
                .hasStackTraceContaining("Simulated failure updating target account");

        assertPersistedBalance(SOURCE_ACCOUNT_ID, "100000.00");
        assertPersistedBalance(TARGET_ACCOUNT_ID, "50000.00");
    }

    private void createFailingTargetUpdateTrigger() {
        jdbcTemplate.execute("""
                CREATE OR REPLACE FUNCTION fail_target_account_update()
                RETURNS TRIGGER AS $$
                BEGIN
                    IF NEW.id = 9002 THEN
                        RAISE EXCEPTION 'Simulated failure updating target account';
                    END IF;
                    RETURN NEW;
                END;
                $$ LANGUAGE plpgsql
                """);

        jdbcTemplate.execute("""
                CREATE TRIGGER fail_target_account_update_trigger
                BEFORE UPDATE ON accounts
                FOR EACH ROW
                EXECUTE FUNCTION fail_target_account_update()
                """);
    }

    private void assertPersistedBalance(int accountId, String expectedBalance) {
        BigDecimal balance = jdbcTemplate.queryForObject(
                "SELECT balance FROM accounts WHERE id = ?",
                BigDecimal.class,
                accountId);

        assertThat(balance).isEqualByComparingTo(expectedBalance);
    }

}
