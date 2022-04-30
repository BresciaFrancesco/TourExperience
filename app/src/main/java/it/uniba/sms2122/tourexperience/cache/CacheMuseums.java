package it.uniba.sms2122.tourexperience.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

    /** La cache è HashMap che contiene oggetti di tipo HashSet.
     * Vengono salvati i percorsi presenti in locale, ma solo il nome.
     * La struttura è:
     * "nome museo" : {"nome percorso", "nome percorso", ...},
     * "nome museo" : ...,
     * ...
     */
    public final static Map<String, Set<String>> cachePercorsiInLocale = new HashMap<>();


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


    /**
     * Aggiunge un nuovo percorso alla cache dei percorsi in locale.
     * Controlla prima che il nome del museo sia già presente, in modo
     * da eseguire correttamente l'aggiunta del nome del percorso.
     * @param nomeMuseo nome del museo che potrebbe essere già presente,
     *                  altrimenti viene aggiunto.
     * @param percorsi nomi dei percorsi da aggiungere.
     */
    public static void addNewPercorsoToCache(final String nomeMuseo,
                                             final List<String> percorsi) {
        Set<String> s = cachePercorsiInLocale.get(nomeMuseo);
        if (s == null) {
            cachePercorsiInLocale.put(nomeMuseo, new HashSet<>(percorsi));
            return;
        }
        s.addAll(percorsi);
    }

    /**
     * Controlla se la cache dei percorsi in locale contiene il percorso
     * passato come parametro.
     * @param nomeMuseo nome del museo nella quale potrebbe trovarsi o non trovarsi il percorso.
     * @param nomePercorso nome del percorso di cui controllare l'esistenza.
     * @return true se il percorso è già presente in cache, false altrimenti.
     */
    public static boolean checkRouteExistence(final String nomeMuseo,
                                              String nomePercorso) {
        if (nomePercorso.endsWith(".json")) {
            nomePercorso = nomePercorso.substring(0, nomePercorso.length()-5);
        }
        Set<String> percorsi = cachePercorsiInLocale.get(nomeMuseo);
        return percorsi != null && percorsi.contains(nomePercorso);
    }

}
