package com.laurabeltran.banking_api.application.account.port;

import com.laurabeltran.banking_api.domain.account.Account;

public interface AccountRepository {

    Account save(Account account);

}
