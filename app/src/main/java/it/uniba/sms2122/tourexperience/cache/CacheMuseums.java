package it.uniba.sms2122.tourexperience.cache;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.uniba.sms2122.tourexperience.model.Museo;
import it.uniba.sms2122.tourexperience.utility.filesystem.LocalFileMuseoManager;

/**
 * Permette di salvare la lista dei musei caricata dal filesystem locale,
 * così che non si debbano ulteriormente caricare i dati dai file in locale.
 *
 * Permette inoltre di salvare i percorsi ritornati dal cloud.
 */
public class CacheMuseums {
    /** La cache è una semplice HashMap */
    public final static Map<String, Museo> cacheMuseums = new HashMap<>();

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
     * Ritorna il museo associato alla chiave "nomeMuseo". Se il museo non è presente,
     * controlla che la cache non sia vuota. Se non è vuota, ritorna null, altrimenti
     * cerca nel filesystem locale per ottenere tutti i musei salvati. Se trova dei musei,
     * riempie la cache e la interroga nuovamente, altrimenti ritorna null.
     * @param nomeMuseo chiave di ricerca per la cache.
     * @param context android context.
     * @return Museo associato alla chiave o null.
     */
    public static Museo getMuseoByName(final String nomeMuseo, final Context context) {
        Museo m = cacheMuseums.get(nomeMuseo);
        if (m != null || !cacheMuseums.isEmpty())
            return m;
        try {
            final LocalFileMuseoManager localFileManager = new LocalFileMuseoManager(context.getFilesDir().toString());
            final List<Museo> tmpMusei = localFileManager.getListMusei();
            if (tmpMusei != null && !tmpMusei.isEmpty()) {
                replaceMuseumsInCache(tmpMusei);
                return cacheMuseums.get(nomeMuseo);
            }
        } catch (IOException e) {
            Log.e("CACHE - getMuseoByName", "Cache vuota e file non reperibili");
            e.printStackTrace();
        }
        return m;
    }

    /**
     * Ritorna il set dei percorsi associato alla chiave "nomeMuseo". Se il set non è presente,
     * controlla che la cache non sia vuota. Se non è vuota, ritorna null, altrimenti
     * cerca nel filesystem locale per ottenere tutti i percorsi salvati. Se trova dei
     * percorsi, riempie la cache e la interroga nuovamente, altrimenti ritorna null.
     * @param nomeMuseo chiave di ricerca per la cache.
     * @param context android context.
     * @return Museo associato alla chiave o null.
     */
    public static Set<String> getPercorsiByMuseo(final String nomeMuseo, final Context context) {
        Set<String> p = cachePercorsiInLocale.get(nomeMuseo);
        if (p != null || !cachePercorsiInLocale.isEmpty())
            return p;
        try {
            new LocalFileMuseoManager(context.getFilesDir().toString())
                    .getPercorsiInLocale();
            if (!cachePercorsiInLocale.isEmpty())
                return cachePercorsiInLocale.get(nomeMuseo);
        }
        catch (IOException e) {
            Log.e("CACHE - getPercorsiByMuseo", "Cache vuota e file non reperibili");
            e.printStackTrace();
        }
        return p;
    }

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
