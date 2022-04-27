package it.uniba.sms2122.tourexperience.operadetection;

import android.util.Log;

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
    private static final String TAG = DistanceDetection.class.getSimpleName();
    private static final int TIME_FOR_DETECTION = 4;
    private static final double MIN_DISTANCE = 0.5;

    public Thread t;

    private HashMap<String, Queue<DistanceRecord>> distanceRecords;
    private TreeSet<Opera> operasDetected;
    private OnDetectionListener onDetectionListener;

    public DistanceDetection(HashMap<String, Queue<DistanceRecord>> distanceRecords, OnDetectionListener onDetectionListener) {
        this.distanceRecords = distanceRecords;
        this.onDetectionListener = onDetectionListener;

        t = new Thread(this);
        t.start();
    }

    @Override
    public synchronized void run() {
        while(!t.isInterrupted()) {
            if(!distanceRecords.isEmpty()) {
                for(Map.Entry<String, Queue<DistanceRecord>> entry : distanceRecords.entrySet()) {
                    Queue<DistanceRecord> queue = entry.getValue();
                    // Pulisco la coda da tutti i record più vecchi di 4 secondi (non contano nella media)
                    if(!queue.isEmpty()) {
                        queue.removeIf(distanceRecord -> distanceRecord.getTimestamp().getSecond() < (LocalTime.now().getSecond() - TIME_FOR_DETECTION));

                        double avg = distanceAverage(queue);
                        if(avg != -1 && avg < MIN_DISTANCE)
                            operasDetected.add(queue.peek().getOpera());
                    }
                }
                if(!operasDetected.isEmpty()) {
                    onDetectionListener.onOperasDetected(operasDetected);
                }
            }

            // Stoppo il thread per 4 secondi
            try {
                Thread.sleep(TIME_FOR_DETECTION*1000);
            } catch (InterruptedException e) {
                Log.e(TAG, e.getMessage());
            }
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
