package com.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "lector")
@DiscriminatorValue("LECTOR")
public class Lector extends Usuario {

    @Column(nullable = false, unique = true)
    private String username;


    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}

