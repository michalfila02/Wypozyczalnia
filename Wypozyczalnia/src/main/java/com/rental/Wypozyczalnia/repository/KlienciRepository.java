package com.rental.Wypozyczalnia.repository;

import com.rental.Wypozyczalnia.model.Klienci;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface KlienciRepository extends JpaRepository<Klienci, Integer> {

    @Query("""
        SELECT k FROM Klienci k
        WHERE k.imię LIKE LOWER(CONCAT('%', :search, '%'))
           OR k.nazwisko LIKE LOWER(CONCAT('%', :search, '%'))
           OR CAST(k.pesel AS string) LIKE CONCAT('%', :search, '%')
    """)
    Page<Klienci> search(@Param("search") String search, Pageable pageable);
}