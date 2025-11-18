package com.rental.Wypozyczalnia.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "role", schema = "mydb", indexes = {
        @Index(name = "fk_Role_Wypozyczalnie1_idx", columnList = "Wypozyczalnie_Nazwa")
}, uniqueConstraints = {
        @UniqueConstraint(name = "Role", columnNames = {"Role", "Wypozyczalnie_Nazwa"})
})
public class Role {
    @Id
    @Column(name = "Role", nullable = false, length = 10)
    private String role;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "Wypozyczalnie_Nazwa")
    private Wypozyczalnie wypozyczalnieNazwa;

    @OneToMany(mappedBy = "roleRole")
    private Set<User> users = new LinkedHashSet<>();

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Wypozyczalnie getWypozyczalnieNazwa() {
        return wypozyczalnieNazwa;
    }

    public void setWypozyczalnieNazwa(Wypozyczalnie wypozyczalnieNazwa) {
        this.wypozyczalnieNazwa = wypozyczalnieNazwa;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

}