package com.rental.Wypozyczalnia.repository;

import com.rental.Wypozyczalnia.model.Wypozyczalnie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WypozyczalnieRepository extends JpaRepository<Wypozyczalnie, String> {

    @Query("""
        SELECT w FROM Wypozyczalnie w
        WHERE w.nazwa LIKE CONCAT('%', :search, '%')
           OR w.miasto LIKE CONCAT('%', :search, '%')
           OR w.ulica LIKE CONCAT('%', :search, '%')
           OR w.telefonKontaktowy LIKE CONCAT('%', :search, '%')
    """)
    Page<Wypozyczalnie> search(String search, Pageable pageable);

    String nazwa(String nazwa);
}