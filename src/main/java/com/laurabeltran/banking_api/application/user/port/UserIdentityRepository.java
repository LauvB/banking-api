package com.laurabeltran.banking_api.application.user.port;

import java.util.Optional;

public interface UserIdentityRepository {

    Optional<Integer> findIdByUsername(String username);

}
