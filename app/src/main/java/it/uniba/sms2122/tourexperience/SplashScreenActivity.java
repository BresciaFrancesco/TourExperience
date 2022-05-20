package it.uniba.sms2122.tourexperience;

import static it.uniba.sms2122.tourexperience.utility.filesystem.LocalFileManager.createLocalDirectoryIfNotExists;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.atomic.AtomicBoolean;

import it.uniba.sms2122.tourexperience.holders.UserHolder;
import it.uniba.sms2122.tourexperience.main.MainActivity;
import it.uniba.sms2122.tourexperience.percorso.PercorsoActivity;
import it.uniba.sms2122.tourexperience.welcome.WelcomeActivity;

public class SplashScreenActivity extends AppCompatActivity {

    private UserHolder userHolder;
    private ActivityOptions options;
    private final String mainDirectory = "Museums";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        System.out.println("SEI DENTRO");

        createLocalDirectoryIfNotExists(getFilesDir(), mainDirectory);
        options = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_left);
        checkConnectivity();
    }

    public void checkConnectivity() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(SplashScreenActivity.this.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        AtomicBoolean check = new AtomicBoolean(true);
        if (ni != null && ni.isAvailable() && ni.isConnected()) {
            FirebaseAuth.getInstance().getCurrentUser();
            userHolder = UserHolder.getInstance();
            userHolder.getUser(
                    //Caso: Utente è loggato
                    (user) -> {
                        System.out.println("CIao");
                        check.set(false);
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Non si può tornare indietro con il pulsane Back
                    },
                    //Caso: Utente non è loggato
                    () -> {
                        System.out.println("CIao1");
                        // Verifica se si tratta della prima apertura o no
                        SharedPreferences prefs = getSharedPreferences(BuildConfig.SHARED_PREFS, MODE_PRIVATE);
                        if(!prefs.contains(BuildConfig.SP_FIRST_OPENING)) {
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean(BuildConfig.SP_FIRST_OPENING, true);
                            editor.apply();
                            System.out.println("2");
                        }

                        if(prefs.getBoolean(BuildConfig.SP_FIRST_OPENING, true)) {
                            System.out.println("3");
                            startActivity(new Intent(this, WelcomeActivity.class));
                            finish();
                        } else {
                            System.out.println("4");
                            startActivity(new Intent(this, FirstActivity.class));
                            finish();
                        }
                    }
            );
        } else {
            Toast.makeText(SplashScreenActivity.this, getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }
    }
}