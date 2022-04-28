package it.uniba.sms2122.tourexperience.operadetection;

import java.time.LocalTime;
import java.util.Objects;

import it.uniba.sms2122.tourexperience.model.Opera;

/**
 * Classe che rappresenta un singolo record di una scansione effettuata con il Bluetooth Low Energy. A differenza della classe ScanResult, qui vengono registrati
 * solo i dati relativi alla distanza e all'istante di registrazione.
 * @author Catignano Francesco
 */
public class DistanceRecord implements Comparable<DistanceRecord> {
    private Opera opera;
    private double distance;
    private final LocalTime timestamp;

    public DistanceRecord(Opera opera, double distance) {
        this.opera = opera;
        this.distance = distance;
        timestamp = LocalTime.now();
    }

    public Opera getOpera() {
        return opera;
    }

    public double getDistance() {
        return distance;
    }

    public LocalTime getTimestamp() {
        return timestamp;
    }

    @Override
    public int compareTo(DistanceRecord d1) {
        return Double.compare(distance, d1.distance);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DistanceRecord that = (DistanceRecord) o;
        return Double.compare(that.distance, distance) == 0 && Objects.equals(opera, that.opera);
    }

    @Override
    public int hashCode() {
        return Objects.hash(opera.getId());
    }
}
