package com.laurabeltran.banking_api.infrastructure.persistence.user;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.laurabeltran.banking_api.application.user.port.UserIdentityRepository;

@Repository
public class PostgreSQLUserIdentityRepository implements UserIdentityRepository {

    private final JpaUserRepository jpaUserRepository;

    public PostgreSQLUserIdentityRepository(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    public Optional<Integer> findIdByUsername(String username) {
        return jpaUserRepository.findByUsername(username)
                .map(UserEntity::getId);
    }

}
