package com.rental.Wypozyczalnia.model;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "wypozyczalnie", schema = "mydb")
public class Wypozyczalnie {
    @Id
    @Column(name = "Nazwa", nullable = false, length = 20)
    private String nazwa;

    @Column(name = "Miasto", nullable = false, length = 45)
    private String miasto;

    @Column(name = "Ulica", nullable = false, length = 45)
    private String ulica;

    @Column(name = "Nr_ulicy", nullable = false)
    private Integer nrUlicy;

    @Column(name = "Telefon_kontaktowy", nullable = false, length = 45)
    private String telefonKontaktowy;

    @OneToMany(mappedBy = "wypozyczalnieNazwa")
    private Set<Role> roles = new LinkedHashSet<>();

    @OneToMany(mappedBy = "wypozyczalnieNazwa")
    private Set<Samochody> samochodies = new LinkedHashSet<>();

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public String getMiasto() {
        return miasto;
    }

    public void setMiasto(String miasto) {
        this.miasto = miasto;
    }

    public String getUlica() {
        return ulica;
    }

    public void setUlica(String ulica) {
        this.ulica = ulica;
    }

    public Integer getNrUlicy() {
        return nrUlicy;
    }

    public void setNrUlicy(Integer nrUlicy) {
        this.nrUlicy = nrUlicy;
    }

    public String getTelefonKontaktowy() {
        return telefonKontaktowy;
    }

    public void setTelefonKontaktowy(String telefonKontaktowy) {
        this.telefonKontaktowy = telefonKontaktowy;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<Samochody> getSamochodies() {
        return samochodies;
    }

    public void setSamochodies(Set<Samochody> samochodies) {
        this.samochodies = samochodies;
    }

}