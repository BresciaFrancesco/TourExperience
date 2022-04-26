package it.uniba.sms2122.tourexperience.model.DTO;

import java.io.File;
import java.util.Optional;

public class MuseoLocalStorageDTO {
    private Optional<File> museoDir = Optional.empty();
    private Optional<File> stanzeDir = Optional.empty();
    private Optional<File> percorsiDir = Optional.empty();
    private Optional<File> info = Optional.empty();
    private Optional<File> immaginePrincipale = Optional.empty();

    private MuseoLocalStorageDTO() {}

    public static MuseoLocalStorageDTO newBuilder() {
        return new MuseoLocalStorageDTO();
    }

    public MuseoLocalStorageDTO setMuseoDir(File museoDir) {
        this.museoDir = Optional.ofNullable(museoDir);
        return this;
    }

    public MuseoLocalStorageDTO setStanzeDir(File stanzeDir) {
        this.stanzeDir = Optional.ofNullable(stanzeDir);
        return this;
    }

    public MuseoLocalStorageDTO setPercorsiDir(File percorsiDir) {
        this.percorsiDir = Optional.ofNullable(percorsiDir);
        return this;
    }

    public MuseoLocalStorageDTO setInfo(File info) {
        this.info = Optional.ofNullable(info);
        return this;
    }

    public MuseoLocalStorageDTO setImmaginePrincipale(File immaginePrincipale) {
        this.immaginePrincipale = Optional.ofNullable(immaginePrincipale);
        return this;
    }

    public Optional<File> getMuseoDir() {
        return museoDir;
    }

    public Optional<File> getStanzeDir() {
        return stanzeDir;
    }

    public Optional<File> getPercorsiDir() {
        return percorsiDir;
    }

    public Optional<File> getInfo() {
        return info;
    }

    public Optional<File> getImmaginePrincipale() {
        return immaginePrincipale;
    }
}
