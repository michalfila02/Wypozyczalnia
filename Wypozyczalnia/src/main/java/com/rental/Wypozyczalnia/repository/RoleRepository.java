package com.rental.Wypozyczalnia.repository;

import com.rental.Wypozyczalnia.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

    @Query("""
        SELECT r FROM Role r
        WHERE r.role LIKE CONCAT('%', :search, '%')
           OR r.wypozyczalnieNazwa.nazwa LIKE CONCAT('%', :search, '%')
    """)
    Page<Role> search(@Param("search") String search, Pageable pageable);
}