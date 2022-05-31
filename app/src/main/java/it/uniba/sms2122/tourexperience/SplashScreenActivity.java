package it.uniba.sms2122.tourexperience;

import static it.uniba.sms2122.tourexperience.utility.filesystem.LocalFileManager.createLocalDirectoryIfNotExists;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import it.uniba.sms2122.tourexperience.holders.UserHolder;
import it.uniba.sms2122.tourexperience.main.MainActivity;
import it.uniba.sms2122.tourexperience.utility.connection.NetworkConnectivity;
import it.uniba.sms2122.tourexperience.welcome.WelcomeActivity;

public class SplashScreenActivity extends AppCompatActivity {

    private UserHolder userHolder;
    private ActivityOptions options;
    private final String mainDirectory = "Museums";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        createLocalDirectoryIfNotExists(getFilesDir(), mainDirectory);
        options = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_left);
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
                    startActivity(new Intent(this, WelcomeActivity.class));
                } else {
                    startActivity(new Intent(this, FirstActivity.class));
                }
                finish();
            }
        );
    }
}