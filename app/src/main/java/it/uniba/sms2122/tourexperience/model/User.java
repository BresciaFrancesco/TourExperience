package it.uniba.sms2122.tourexperience.model;

public class User {
    private String email;
    private String name;
    private String surname;
    private String dateBirth;

    public User(String email, String name, String surname, String dateBirth) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.dateBirth = dateBirth;
    }

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

