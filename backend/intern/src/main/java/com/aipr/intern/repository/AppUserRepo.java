package com.aipr.intern.repository;

import com.aipr.intern.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepo extends JpaRepository<AppUser, Long> {

    // Authentication
    Optional<AppUser> findByUsername(String username);
    Optional<AppUser> findByEmail(String email);

    // Registration validation
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    Optional<AppUser> findByUsernameOrEmail(String username, String email);
}
