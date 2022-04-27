package it.uniba.sms2122.tourexperience.operadetection;

import java.time.LocalTime;
import java.util.Objects;

/**
 * Classe che rappresenta un singolo record di una scansione effettuata con il Bluetooth Low Energy. A differenza della classe ScanResult, qui vengono registrati
 * solo i dati relativi alla distanza e all'istante di registrazione.
 * @author Catignano Francesco
 */
public class DistanceRecord implements Comparable<DistanceRecord> {
    private String operaId;
    private double distance;
    private final LocalTime timestamp;

    public DistanceRecord(String operaId, double distance) {
        this.operaId = operaId;
        this.distance = distance;
        timestamp = LocalTime.now();
    }

    public String getOperaId() {
        return operaId;
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
        return Double.compare(that.distance, distance) == 0 && Objects.equals(operaId, that.operaId) && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operaId);
    }
}
