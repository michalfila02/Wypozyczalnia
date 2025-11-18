package com.rental.Wypozyczalnia.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "samochody", schema = "mydb", indexes = {
        @Index(name = "fk_Samochody_Wypozyczalnie1_idx", columnList = "Wypozyczalnie_Nazwa")
})
public class Samochody {
    @Id
    @Column(name = "Nr_rejestracyjny", nullable = false, length = 20)
    private String nrRejestracyjny;

    @Column(name = "Marka", nullable = false, length = 45)
    private String marka;

    @Column(name = "Model", nullable = false, length = 45)
    private String model;

    @Column(name = "Koszt_wynajecia_na_dzien", nullable = false)
    private Double kosztWynajeciaNaDzien;

    @Column(name = "Przebieg_w_km", nullable = false)
    private Integer przebiegWKm;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "Wypozyczalnie_Nazwa", nullable = false)
    private Wypozyczalnie wypozyczalnieNazwa;

    @OneToMany(mappedBy = "samochodyNrRejestracyjny")
    private Set<Wypozyczalnia> wypozyczalnias = new LinkedHashSet<>();

    public String getNrRejestracyjny() {
        return nrRejestracyjny;
    }

    public void setNrRejestracyjny(String nrRejestracyjny) {
        this.nrRejestracyjny = nrRejestracyjny;
    }

    public String getMarka() {
        return marka;
    }

    public void setMarka(String marka) {
        this.marka = marka;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Double getKosztWynajeciaNaDzien() {
        return kosztWynajeciaNaDzien;
    }

    public void setKosztWynajeciaNaDzien(Double kosztWynajeciaNaDzien) {
        this.kosztWynajeciaNaDzien = kosztWynajeciaNaDzien;
    }

    public Integer getPrzebiegWKm() {
        return przebiegWKm;
    }

    public void setPrzebiegWKm(Integer przebiegWKm) {
        this.przebiegWKm = przebiegWKm;
    }

    public Wypozyczalnie getWypozyczalnieNazwa() {
        return wypozyczalnieNazwa;
    }

    public void setWypozyczalnieNazwa(Wypozyczalnie wypozyczalnieNazwa) {
        this.wypozyczalnieNazwa = wypozyczalnieNazwa;
    }

    public Set<Wypozyczalnia> getWypozyczalnias() {
        return wypozyczalnias;
    }

    public void setWypozyczalnias(Set<Wypozyczalnia> wypozyczalnias) {
        this.wypozyczalnias = wypozyczalnias;
    }

}