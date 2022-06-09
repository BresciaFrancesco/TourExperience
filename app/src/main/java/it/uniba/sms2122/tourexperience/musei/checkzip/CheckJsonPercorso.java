package it.uniba.sms2122.tourexperience.musei.checkzip;

import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.cachePercorsiInLocale;
import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.getPercorsiByMuseo;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import it.uniba.sms2122.tourexperience.graph.Percorso;
import it.uniba.sms2122.tourexperience.utility.filesystem.LocalFileManager;
import it.uniba.sms2122.tourexperience.utility.filesystem.zip.OpenFile;
import static it.uniba.sms2122.tourexperience.utility.Validate.*;

import androidx.annotation.NonNull;

/**
 * Esegue controlli sui file .json dei percorsi da importare.
 */
public class CheckJsonPercorso {

    private final OpenFile dto;
    private final LocalFileManager localFileManager;
    private final Context context;
    private String nomeMuseo = null;
    private String nomePercorso = null;

    public CheckJsonPercorso(final OpenFile dto, final LocalFileManager localFileManager, final Context context) {
        this.dto = notNull(dto);
        this.localFileManager = notNull(localFileManager);
        this.context = context;
    }

    /**
     * Controllo approssimativo di un percorso .json
     * @return True se il file json è accettabile, False altrimenti.
     */
    public boolean check() {
        final Gson gson = new Gson();
        try ( Reader reader = new InputStreamReader(dto.openFile()) ) {
            final Percorso test = gson.fromJson(reader, Percorso.class);
            Percorso.checkAll(test);
            final Set<String> percorsi = cachePercorsiInLocale.get(test.getNomeMuseo());
            notNull(percorsi, "La cache dei percorsi in locale è nulla");
            isTrue(!percorsi.contains(test.getNomePercorso()), "Percorso già esistente");
            return save(test, gson);
        }
        catch (Exception e) {
            Log.e("LOCAL_IMPORT_JSON", "Json Percorso non valido\n" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Crea e salva un file con il json importato.
     * @param p oggetto percorso da salvare in file e precedentemente analizzato.
     * @param gson parser Gson.
     * @return True se il file è stato correttamente salvato, False altrimenti.
     * @throws NullPointerException
     * @throws IOException
     * @throws JsonSyntaxException
     * @throws JsonIOException
     */
    private boolean save(final Percorso p, final Gson gson)
            throws NullPointerException, IllegalArgumentException,
            IOException, JsonSyntaxException, JsonIOException
    {
        notNull(p, "Percorso vuoto");
        notNull(gson, "Gson vuoto");

        final File filePercorsoJson = LocalFileManager.buildGeneralPath(localFileManager.getGeneralPath(),
                new String[] {p.getNomeMuseo(), "Percorsi", p.getNomePercorso()+".json"}).toFile();

        notNull(getPercorsiByMuseo(p.getNomeMuseo(), context),
                "La cache dei percorsi in locale non ha il museo %s",
                p.getNomeMuseo())
        .add(p.getNomePercorso());

        isTrue(!filePercorsoJson.exists(), "File percorso o già esistente in locale");
        isTrue(filePercorsoJson.createNewFile());

        final BufferedWriter targetFileWriter = new BufferedWriter(new FileWriter(filePercorsoJson));
        targetFileWriter.write(gson.toJson(p));
        targetFileWriter.close();

        Log.v("LOCAL_IMPORT_JSON", "Json Percorso " + p.getNomePercorso() + " salvato correttamente");
        this.nomeMuseo = p.getNomeMuseo();
        this.nomePercorso = p.getNomePercorso();
        return true;
    }

    /**
     * Salva i dati del percorso su firebase se esso non è già presente.
     */
    public void updateFirebase() {
        if (nomeMuseo == null || nomePercorso == null) {
            Log.e("CheckJsonPercorso.updateFirebase", "nomeMuseo e/o nomePercorso null");
            return;
        }
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Museums/" + nomeMuseo);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) return;
                if (snapshot.hasChildren()) {
                    if (snapshot.hasChild(nomePercorso)) {
                        return;
                    }
                }
                final Map<String, Object> mappa = new HashMap<>();
                mappa.put("Nome_percorso", nomePercorso);
                mappa.put("Numero_starts", 0);
                mappa.put("Voti", "-1");

                final DatabaseReference r = ref.child(nomePercorso);
                r.setValue(mappa)
                .addOnSuccessListener(snap -> Log.v("FIREBASE_JSON_IMPORT", "Dati del Percorso aggiunti su firebase correttamente"))
                .addOnFailureListener(err -> Log.e("FIREBASE_JSON_IMPORT", err.getMessage()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

}
