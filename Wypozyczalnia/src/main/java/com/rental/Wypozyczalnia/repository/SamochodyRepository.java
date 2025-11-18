package com.rental.Wypozyczalnia.repository;

import com.rental.Wypozyczalnia.model.Samochody;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SamochodyRepository extends JpaRepository<Samochody, String> {

    @Query("""
        SELECT s FROM Samochody s
        WHERE s.nrRejestracyjny LIKE CONCAT('%', :search, '%')
           OR s.marka LIKE CONCAT('%', :search, '%')
           OR s.model LIKE CONCAT('%', :search, '%')
           OR CAST(s.kosztWynajeciaNaDzien AS string) LIKE CONCAT('%', :search, '%')
           OR s.wypozyczalnieNazwa.nazwa LIKE CONCAT('%', :search, '%')
    """)
    Page<Samochody> search(String search, Pageable pageable);
}