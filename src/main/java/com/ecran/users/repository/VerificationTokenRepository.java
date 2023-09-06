package com.ecran.users.repository;

import com.ecran.users.entity.UserEntity;
import com.ecran.users.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface VerificationTokenRepository
        extends CrudRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);

    VerificationToken findByUser(UserEntity user);
}
