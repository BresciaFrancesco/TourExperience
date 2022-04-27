package it.uniba.sms2122.tourexperience.operadetection;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.TreeSet;

import it.uniba.sms2122.tourexperience.model.Opera;

/**
 * Classe che si occupa di trovare le opere più vicine al dispositivo corrente.
 * @author Catignano Francesco
 */
public class DistanceDetection implements Runnable {
    public static final int TIME_FOR_DETECTION = 4;
    private static final double MIN_DISTANCE = 0.5;

    private HashMap<String, Queue<DistanceRecord>> distanceRecords;
    private TreeSet<Opera> operasDetected;
    private OnDetectionListener onDetectionListener;

    public DistanceDetection(HashMap<String, Queue<DistanceRecord>> distanceRecords, OnDetectionListener onDetectionListener) {
        this.distanceRecords = distanceRecords;
        this.onDetectionListener = onDetectionListener;
    }

    @Override
    public synchronized void run() {
        for(Map.Entry<String, Queue<DistanceRecord>> entry : distanceRecords.entrySet()) {
            Queue<DistanceRecord> queue = entry.getValue();

            if(!queue.isEmpty()) {
                queue.removeIf(distanceRecord -> distanceRecord.getTimestamp().getSecond() < (LocalTime.now().getSecond()) - TIME_FOR_DETECTION);
                double avg = distanceAverage(queue);

                if(avg != -1 && avg < MIN_DISTANCE)
                    operasDetected.add(queue.peek().getOpera());
            }
        }
        if(!operasDetected.isEmpty()) {
            onDetectionListener.onOperasDetected(operasDetected);
        }
    }

    /**
     * Calcola la media delle distanze per un'opera.
     * @param queue La coda contenente le distanze dell'opera
     * @return La media delle distanze
     */
    private double distanceAverage(Queue<DistanceRecord> queue) {
        if(queue.isEmpty())
            return -1;

        double temp = 0;
        for(DistanceRecord distanceRecord : queue)
            temp += distanceRecord.getDistance();

        return temp/queue.size();
    }

    /**
     * Interfaccia contenente un solo metodo da eseguire quando vengono rilevate le opere vicine.
     */
    public interface OnDetectionListener {
        void onOperasDetected(TreeSet<Opera> operasDetected);
    }
}
