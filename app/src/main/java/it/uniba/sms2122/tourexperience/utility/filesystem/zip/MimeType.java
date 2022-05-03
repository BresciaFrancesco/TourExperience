package it.uniba.sms2122.tourexperience.utility.filesystem.zip;

public enum MimeType {
    JSON("application/json"),
    ZIP("application/zip");

    private final String mimeType;

    MimeType(final String mimeType) {
        this.mimeType = mimeType;
    }

    public String mimeType() {
        return mimeType;
    }
}
