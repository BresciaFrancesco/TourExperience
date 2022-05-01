package it.uniba.sms2122.tourexperience.musei.checkzip;


import java.util.Map;

public class Tree {
    private String value;
    public Map<String, Tree> children = null;

    public Tree(String value) {
        this.value = value;
    }

    public Tree(String value, Map<String, Tree> children) {
        this.value = value;
        if (children != null)
            this.children = children;
    }

    public String value() {
        return value;
    }
}
