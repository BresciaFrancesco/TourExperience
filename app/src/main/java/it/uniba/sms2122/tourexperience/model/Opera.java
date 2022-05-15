package it.uniba.sms2122.tourexperience.model;


import static it.uniba.sms2122.tourexperience.utility.Validate.isTrue;
import static it.uniba.sms2122.tourexperience.utility.Validate.notBlank;
import static it.uniba.sms2122.tourexperience.utility.Validate.notNull;

import java.io.Serializable;
import java.util.Objects;

public class Opera implements Serializable {

    private String id;
    private String nome;
    private String percorsoImg;
    private String descrizione;

    public Opera() {}

    public Opera(String id, String nome, String percorsoImg, String descrizione) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.percorsoImg = percorsoImg;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public String getPercorsoImg() {
        return percorsoImg;
    }

    public void setPercorsoImg(String percorsoImg) {
        this.percorsoImg = percorsoImg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Opera opera = (Opera) o;
        return Objects.equals(id, opera.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    /**
     * Controlla un oggetto Opera arbitrario secondo tale contratto:
     * 0. opera non null
     * 1. id non Blank.
     * 2. nome non Blank.
     * 3. descrizione non Blank.
     * 4. tipologia non Blank.
     * 5. percorso immagine deve essere null.
     *
     * Per la definizione di "non Blank" guardare la documentazione del metodo
     * notBlank della classe it.uniba.sms2122.tourexperience.utility.Validate
     * @param o opera da controllare.
     */
    public static void checkOpera(final Opera o) throws NullPointerException, IllegalArgumentException {
        final String erroreBlank = "Opera: Blank %s";
        final String erroreNull = "Opera: Null %s";
        final String erroreNotNull = "Opera: Not Null %s";
        notNull(o, erroreNull, "opera");
        notBlank(o.getId(), erroreBlank, "id");
        notBlank(o.getNome(), erroreBlank, "nome");
        notBlank(o.getDescrizione(), erroreBlank, "descrizione");
        isTrue(o.getPercorsoImg() == null || o.getPercorsoImg().isEmpty(), erroreNotNull, "percorso img");
    }
}