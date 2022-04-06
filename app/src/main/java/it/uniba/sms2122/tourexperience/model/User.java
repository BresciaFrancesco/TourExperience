package it.uniba.sms2122.tourexperience.model;

import com.google.firebase.database.Exclude;

public class User {
    private String email;
    private String name;
    private String surname;
    private String dateBirth;

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

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getDateBirth() {
        return dateBirth;
    }

}

