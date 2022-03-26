package it.uniba.sms2122.tourexperience.graph;

import com.google.gson.InstanceCreator;

import java.lang.reflect.Type;
import java.util.HashSet;

class PercorsoInstanceCreator implements InstanceCreator<Percorso> {

    @Override
    public Percorso createInstance(Type type) {
        return new Percorso(new HashSet<>());
    }

}