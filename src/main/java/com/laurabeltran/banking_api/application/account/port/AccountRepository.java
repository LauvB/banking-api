package com.laurabeltran.banking_api.application.account.port;

import java.util.Optional;

import com.laurabeltran.banking_api.domain.account.Account;

public interface AccountRepository {

    Account save(Account account);

    Optional<Account> findById(int id);

    Account update(Account account);

}
