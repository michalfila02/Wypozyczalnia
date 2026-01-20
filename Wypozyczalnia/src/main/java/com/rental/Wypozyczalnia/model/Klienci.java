package com.rental.Wypozyczalnia.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "klienci", schema = "mydb")
public class Klienci {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_klienta", nullable = false)
    private Integer id;

    @Column(name = "`Imię`", nullable = false, length = 45)
    private String imię;

    @Column(name = "Nazwisko", nullable = false, length = 45)
    private String nazwisko;

    @Column(name = "PESEL", nullable = false)
    private Integer pesel;

    @Column(name = "Data_utworzenia")
    private Instant dataUtworzenia;

    @Column(name = "Data_zmiany")
    private Instant dataZmiany;

    @Column(name = "Utworzone_przez", length = 45)
    private String utworzonePrzez;

    @Column(name = "Zmienione_przez", length = 45)
    private String zmienionePrzez;

    @OneToMany(mappedBy = "idKlienta")
    private Set<Wypozyczalnia> wypozyczalnias = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImię() {
        return imię;
    }

    public void setImię(String imię) {
        this.imię = imię;
    }

    public String getNazwisko() {
        return nazwisko;
    }

    public void setNazwisko(String nazwisko) {
        this.nazwisko = nazwisko;
    }

    public Integer getPesel() {
        return pesel;
    }

    public void setPesel(Integer pesel) {
        this.pesel = pesel;
    }

    public Instant getDataUtworzenia() {
        return dataUtworzenia;
    }

    public void setDataUtworzenia(Instant dataUtworzenia) {
        this.dataUtworzenia = dataUtworzenia;
    }

    public Instant getDataZmiany() {
        return dataZmiany;
    }

    public void setDataZmiany(Instant dataZmiany) {
        this.dataZmiany = dataZmiany;
    }

    public String getUtworzonePrzez() {
        return utworzonePrzez;
    }

    public void setUtworzonePrzez(String utworzonePrzez) {
        this.utworzonePrzez = utworzonePrzez;
    }

    public String getZmienionePrzez() {
        return zmienionePrzez;
    }

    public void setZmienionePrzez(String zmienionePrzez) {
        this.zmienionePrzez = zmienionePrzez;
    }

    public Set<Wypozyczalnia> getWypozyczalnias() {
        return wypozyczalnias;
    }

    public void setWypozyczalnias(Set<Wypozyczalnia> wypozyczalnias) {
        this.wypozyczalnias = wypozyczalnias;
    }

}