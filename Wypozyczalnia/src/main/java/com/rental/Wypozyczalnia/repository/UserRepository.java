package com.rental.Wypozyczalnia.repository;

import com.rental.Wypozyczalnia.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // Search across fields actually in the model
    @Query("""
        SELECT u FROM User u
        WHERE u.email LIKE CONCAT('%', :search, '%')
           OR u.password LIKE CONCAT('%', :search, '%')
           OR CAST(u.roleRole AS string) LIKE CONCAT('%', :search, '%')
    """)
    Page<User> search(String search, Pageable pageable);

    Optional<User> findByEmail(String email);
}