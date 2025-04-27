package com.KEA.g3.ThriveWell.repository;

import com.KEA.g3.ThriveWell.entity.PasswordResetToken;
import com.KEA.g3.ThriveWell.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUser(UserEntity user);
}