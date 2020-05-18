package com.topher.jcalc.api.db.repository;
import com.topher.jcalc.api.db.entities.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // We don't really need to use the @Query annotation for simple queries like this
    Optional<User> findByEmail(final String email);

    @Query("select u from User u where u.id = :userId")
    Optional<User> findById(@Param("userId") final String userId);

    @Query("select u from User u where u.auth0UserId = :auth0UserId")
    Optional<User> findByAuth0UserId(@Param("auth0UserId") final String auth0UserId);

}
