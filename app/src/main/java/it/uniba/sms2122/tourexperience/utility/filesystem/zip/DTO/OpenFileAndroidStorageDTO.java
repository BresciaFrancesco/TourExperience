package it.uniba.sms2122.tourexperience.utility.filesystem.zip.DTO;

import android.content.Context;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;

import it.uniba.sms2122.tourexperience.utility.filesystem.zip.OpenFile;

/**
 * Data Transfer Object utile per aprire un file selezionato
 * dall'utente dalla memoria esterna di android.
 */
public class OpenFileAndroidStorageDTO implements OpenFile {
    private final Context context;
    private final Uri fileUri;

    public OpenFileAndroidStorageDTO(final Context context, final Uri fileUri) {
        this.context = context;
        this.fileUri = fileUri;
    }

    @Override
    public InputStream openFile() throws IOException {
        return context.getContentResolver().openInputStream(fileUri);
    }
}
