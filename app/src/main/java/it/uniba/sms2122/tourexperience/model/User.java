package it.uniba.sms2122.tourexperience.model;

import com.google.firebase.database.Exclude;

/**
 * Classe POJO per memorizzare e modificare l'utente.
 * @author Catignano Francesco
 */
public class User {
    private String email;
    private String name;
    private String surname;
    private String dateBirth;
    private String password;

    private boolean isDirty = false;

    public User() {}

    public User(String email, String name, String surname, String dateBirth) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.dateBirth = dateBirth;
    }

    @Exclude
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        isDirty = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        isDirty = true;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
        this.isDirty = true;
    }

    public String getDateBirth() {
        return dateBirth;
    }

    public void setDateBirth(String dateBirth) {
        this.dateBirth = dateBirth;
        this.isDirty = true;
    }

    @Exclude
    public boolean isDirty() {
        return isDirty;
    }

    @Exclude
    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

