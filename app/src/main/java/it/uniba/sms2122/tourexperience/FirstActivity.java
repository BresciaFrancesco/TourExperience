package it.uniba.sms2122.tourexperience;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import it.uniba.sms2122.tourexperience.holders.UserHolder;
import it.uniba.sms2122.tourexperience.main.MainActivity;
import it.uniba.sms2122.tourexperience.registration.RegistrationActivity;
import static it.uniba.sms2122.tourexperience.utility.filesystem.LocalFileManager.createLocalDirectoryIfNotExists;
import it.uniba.sms2122.tourexperience.welcome.WelcomeActivity;

public class FirstActivity extends AppCompatActivity {

    private Button btnLogin;
    private Button btnRegistration;
    private TextView textViewGuest;
    private ActionBar actionBar;

    private ActivityOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        btnLogin = (Button) findViewById(R.id.idBtnMainLogin);
        btnRegistration = (Button) findViewById(R.id.idBtnMainRegistration);
        textViewGuest = (TextView) findViewById(R.id.idTextViewGuest);

        setOnClickListenerBtnLogin();
        setOnClickListenerBtnRegistration();
        setOnClickListenerTextViewGuest();

        options = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_left);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Tour Experience");
    }

    /**
     * Funzione che fa partire l'activity LoginActivity
     */
    private void setOnClickListenerBtnLogin() {
        btnLogin.setOnClickListener(view -> {
            Intent intent = new Intent(FirstActivity.this, LoginActivity.class);
            startActivity(intent, options.toBundle());
        });
    }

    /**
     * Funzione che fa partire l'activity RegistrationActivity
     */
    private void setOnClickListenerBtnRegistration() {
        btnRegistration.setOnClickListener(view -> {
            Intent intent = new Intent(FirstActivity.this, RegistrationActivity.class);
            startActivity(intent, options.toBundle());
        });
    }

    /**
     * Funzione che fa partire l'activity MainActivity
     */
    private void setOnClickListenerTextViewGuest() {
        textViewGuest.setOnClickListener(view -> {
            Intent intent = new Intent(FirstActivity.this, MainActivity.class);
            startActivity(intent, options.toBundle());
            finish();
        });
    }
}