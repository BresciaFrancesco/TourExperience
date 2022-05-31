package it.uniba.sms2122.tourexperience;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import it.uniba.sms2122.tourexperience.main.MainActivity;
import it.uniba.sms2122.tourexperience.registration.RegistrationActivity;

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

        btnLogin = findViewById(R.id.idBtnMainLogin);
        btnRegistration = findViewById(R.id.idBtnMainRegistration);
        textViewGuest = findViewById(R.id.idTextViewGuest);

        setOnClickListenerBtnLogin();
        setOnClickListenerBtnRegistration();
        setOnClickListenerTextViewGuest();

        options = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_left);
        actionBar = getSupportActionBar();
        assert actionBar != null;
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
            LoginActivity.addNewSessionUid(getApplicationContext(), null);
            Intent intent = new Intent(FirstActivity.this, MainActivity.class);
            startActivity(intent, options.toBundle());
            finish();
        });
    }
}