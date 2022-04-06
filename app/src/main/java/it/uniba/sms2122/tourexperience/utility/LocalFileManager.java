package it.uniba.sms2122.tourexperience.utility;


public class LocalFileManager {

    protected String generalPath;

    public LocalFileManager(String generalPath) {
        if (generalPath.charAt(generalPath.length()-1) != '/') {
            generalPath += "/";
        }
        this.generalPath = generalPath;
    }


}
