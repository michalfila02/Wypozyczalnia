package com.rental.Wypozyczalnia.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "wypozyczalnia", schema = "mydb", indexes = {
        @Index(name = "Id_klienta", columnList = "Id_klienta"),
        @Index(name = "Samochody_Nr_rejestracyjny", columnList = "Samochody_Nr_rejestracyjny")
})
public class Wypozyczalnia {
    @Id
    @Column(name = "Nr_wypozyczenia", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "Id_klienta", nullable = false)
    private Klienci idKlienta;

    @Column(name = "`Data_wypożyczenia`", nullable = false)
    private LocalDate dataWypożyczenia;

    @Column(name = "Data_oddania")
    private LocalDate dataOddania;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "Samochody_Nr_rejestracyjny", nullable = false)
    private Samochody samochodyNrRejestracyjny;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Klienci getIdKlienta() {
        return idKlienta;
    }

    public void setIdKlienta(Klienci idKlienta) {
        this.idKlienta = idKlienta;
    }

    public LocalDate getDataWypożyczenia() {
        return dataWypożyczenia;
    }

    public void setDataWypożyczenia(LocalDate dataWypożyczenia) {
        this.dataWypożyczenia = dataWypożyczenia;
    }

    public LocalDate getDataOddania() {
        return dataOddania;
    }

    public void setDataOddania(LocalDate dataOddania) {
        this.dataOddania = dataOddania;
    }

    public Samochody getSamochodyNrRejestracyjny() {
        return samochodyNrRejestracyjny;
    }

    public void setSamochodyNrRejestracyjny(Samochody samochodyNrRejestracyjny) {
        this.samochodyNrRejestracyjny = samochodyNrRejestracyjny;
    }

}