package it.uniba.sms2122.tourexperience.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.uniba.sms2122.tourexperience.model.Museo;

public class CacheMuseums {
    /// The cache is a simple HashMap
    public final static Map<String, Museo> cacheMuseums = new HashMap<>();

    private CacheMuseums() {}

    public static List<Museo> getAllCachedMuseums() {
        Set<String> keys = cacheMuseums.keySet();
        List<Museo> museums = new ArrayList<>(keys.size());
        int i = 0;
        for (String key : keys) {
            museums.add(i++, cacheMuseums.get(key));
        }
        return museums;
    }

    public static boolean replaceMuseumsInCache(List<Museo> museums) {
        cacheMuseums.clear();
        return addNewMuseumsInCache(museums);
    }

    public static boolean addNewMuseumsInCache(List<Museo> museums) {
        if (museums.isEmpty()) return false;
        for (Museo museo : museums) {
            cacheMuseums.put(museo.getNome(), museo);
        }
        return true;
    }
}
