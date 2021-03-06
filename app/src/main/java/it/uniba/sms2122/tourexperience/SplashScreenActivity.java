package it.uniba.sms2122.tourexperience;

import static it.uniba.sms2122.tourexperience.utility.filesystem.LocalFileManager.createLocalDirectoryIfNotExists;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.io.InputStream;

import it.uniba.sms2122.tourexperience.holders.UserHolder;
import it.uniba.sms2122.tourexperience.main.MainActivity;
import it.uniba.sms2122.tourexperience.utility.connection.NetworkConnectivity;
import it.uniba.sms2122.tourexperience.utility.filesystem.LocalFileMuseoManager;
import it.uniba.sms2122.tourexperience.utility.filesystem.zip.Zip;
import it.uniba.sms2122.tourexperience.welcome.WelcomeActivity;

public class SplashScreenActivity extends AppCompatActivity {

    private UserHolder userHolder;
    private static final String MAIN_DIRECTORY = "Museums";
    private static final String ASSET_FILE = "Louvre";
    private static final String ASSET_FILE_EXTENSION = ".zip";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        createLocalDirectoryIfNotExists(getFilesDir(), MAIN_DIRECTORY);
        manageLogicSplashScreenActivity();
    }

    public void manageLogicSplashScreenActivity() {
        if (!NetworkConnectivity.check(getApplicationContext())) {
            Toast.makeText(SplashScreenActivity.this, getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, FirstActivity.class));
            finish();
            return;
        }
        userHolder = UserHolder.getInstance();
        userHolder.getUser(
            //Caso: Utente è loggato
            (user) -> {
                LoginActivity.addNewSessionUid(getApplicationContext(), FirebaseAuth.getInstance().getUid());
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish(); // Non si può tornare indietro con il pulsane Back
            },
            //Caso: Utente non è loggato
            (String errorMsg) -> {
                // Verifica se si tratta della prima apertura o no
                SharedPreferences prefs = getSharedPreferences(BuildConfig.SHARED_PREFS, MODE_PRIVATE);
                if(!prefs.contains(BuildConfig.SP_FIRST_OPENING)) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(BuildConfig.SP_FIRST_OPENING, true);
                    editor.apply();
                }

                if(prefs.getBoolean(BuildConfig.SP_FIRST_OPENING, true)) {
                    ThreadManager.setThread(() -> {
                        try {
                            getAssetZipMuseum();
                        }
                        catch (IOException | IllegalArgumentException e) {
                            Log.e("getAssetZipMuseum", "Metodo fallito");
                            e.printStackTrace();
                        }
                    });
                    ThreadManager.startThread();
                    startActivity(new Intent(this, WelcomeActivity.class));
                } else {
                    startActivity(new Intent(this, FirstActivity.class));
                }
                finish();
            }
        );
    }

    /**
     * Recupera il museo zip di test dalla cartella di android asset, effettua l'unzip
     * e salva il risultato all'interno del filesystem principale dell'app.
     * @throws IOException
     * @throws IllegalArgumentException
     */
    private void getAssetZipMuseum() throws IOException, IllegalArgumentException {
        final InputStream is = getAssets().open(ASSET_FILE + ASSET_FILE_EXTENSION);
        if (is != null) {
            final LocalFileMuseoManager fileManager = new LocalFileMuseoManager(getFilesDir().toString());
            final Zip zipManager = new Zip(fileManager);
            zipManager.unzip(() -> is);
            if (fileManager.existsMuseo(ASSET_FILE))
                Log.v("getAssetZipMuseum", "Unzip avvenuto con successo");
            else
                Log.e("getAssetZipMuseum", "Unzip fallito");
        }
        else {
            Log.e("getAssetZipMuseum", "Asset Zip non trovato");
        }
    }

}