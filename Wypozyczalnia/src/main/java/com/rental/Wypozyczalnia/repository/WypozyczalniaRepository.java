package com.rental.Wypozyczalnia.repository;

import com.rental.Wypozyczalnia.model.Wypozyczalnia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WypozyczalniaRepository extends JpaRepository<Wypozyczalnia, Integer> {

    @Query("""
        SELECT w FROM Wypozyczalnia w
        WHERE CAST(w.id AS string) LIKE CONCAT('%', :search, '%')
           OR CAST(w.idKlienta.id AS string) LIKE CONCAT('%', :search, '%')
           OR w.samochodyNrRejestracyjny.nrRejestracyjny LIKE CONCAT('%', :search, '%')
    """)
    Page<Wypozyczalnia> search(String search, Pageable pageable);
}