package it.uniba.sms2122.tourexperience.model;

public class User {
    private String name;
    private String surname;
    private String dateBirth;

    public User(String name, String surname, String dateBirth) {
        this.name = name;
        this.surname = surname;
        this.dateBirth = dateBirth;
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

