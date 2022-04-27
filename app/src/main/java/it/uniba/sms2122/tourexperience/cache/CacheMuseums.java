package it.uniba.sms2122.tourexperience.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.uniba.sms2122.tourexperience.model.Museo;

/**
 * Permette di salvare la lista dei musei caricata dal filesystem locale,
 * così che non si debbano ulteriormente caricare i dati dai file in locale.
 *
 * Permette inoltre di salvare i percorsi ritornati dal cloud.
 */
public class CacheMuseums {
    /** La cache è una semplice HashMap */
    public final static Map<String, Museo> cacheMuseums = new HashMap<>();

    /** La cache è un semplice ArrayList.
     *  I percorsi in questo caso sono contenuti in oggetti Museo perché
     *  questo rende più facile il riutilizzo della lista dei musei per
     *  contenere i percorsi scaricati dal cloud (che nella lista sono
     *  rappresentati solo da 2 stringhe, nome del percorsi e nome del
     *  museo). */
    public final static List<Museo> cachePercorsi = new ArrayList<>();

    /** Costruttore privato perché la classe non è istanziabile */
    private CacheMuseums() {}


    /**
     * Ritorna tutti i musei nella cache sotto forma di lista.
     * @return lista dei musei.
     */
    public static List<Museo> getAllCachedMuseums() {
        final Set<String> keys = cacheMuseums.keySet();
        final List<Museo> museums = new ArrayList<>(keys.size());
        int i = 0;
        for (String key : keys) {
            museums.add(i++, cacheMuseums.get(key));
        }
        return museums;
    }


    /**
     * Ripulisce la cache e ne inserisce nuovi elementi.
     * @param percorsi lista di percorsi da inserire nella cache.
     * @return boolean of List.addAll()
     */
    public static boolean replacePercorsiInCache(final List<Museo> percorsi) {
        cachePercorsi.clear();
        return cachePercorsi.addAll(percorsi);
    }


    /**
     * Ripulisce la cache e utilizza il metodo addNewMuseumsInCache(),
     * ritornandone il risultato.
     * @param museums lista di musei da inserire nella cache.
     * @return falso se la lista passata come parametro è vuota,
     *         true altrimenti.
     */
    public static boolean replaceMuseumsInCache(final List<Museo> museums) {
        cacheMuseums.clear();
        return addNewMuseumsInCache(museums);
    }

    /**
     * Inserisce nella cache la lista di musei, aggiungendola ai
     * musei già presenti, senza eliminarli.
     * @param museums lista di musei da inserire nella cache.
     * @return falso se la lista passata come parametro è vuota,
     *         true altrimenti.
     */
    public static boolean addNewMuseumsInCache(final List<Museo> museums) {
        if (museums.isEmpty()) return false;
        for (Museo museo : museums) {
            cacheMuseums.put(museo.getNome(), museo);
        }
        return true;
    }

}
