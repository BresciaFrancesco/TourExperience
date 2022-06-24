package it.uniba.sms2122.tourexperience;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import it.uniba.sms2122.tourexperience.main.MainActivity;
import it.uniba.sms2122.tourexperience.registration.RegistrationActivity;
import it.uniba.sms2122.tourexperience.utility.connection.NetworkConnectivity;

public class FirstActivity extends AppCompatActivity {

    private Button btnLogin;
    private Button btnRegistration;
    private Button textViewGuest;
    private ActionBar actionBar;
    private Button btnLoginTest;
    private ProgressBar progressBarLoginTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        btnLogin = findViewById(R.id.idBtnMainLogin);
        btnRegistration = findViewById(R.id.idBtnMainRegistration);
        textViewGuest = findViewById(R.id.idTextViewGuest);
        btnLoginTest = findViewById(R.id.idBtnLogin_test);
        progressBarLoginTest = findViewById(R.id.pb_login_test);

        setOnClickListenerBtnLogin();
        setOnClickListenerBtnRegistration();
        setOnClickListenerTextViewGuest();
        setOnClickListenerBtnLoginTest();

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
            startActivity(intent);
        });
    }

    /**
     * Funzione che fa partire l'activity RegistrationActivity
     */
    private void setOnClickListenerBtnRegistration() {
        btnRegistration.setOnClickListener(view -> {
            Intent intent = new Intent(FirstActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Funzione che fa partire l'activity MainActivity
     */
    private void setOnClickListenerTextViewGuest() {
        textViewGuest.setOnClickListener(view -> {
            LoginActivity.addNewSessionUid(getApplicationContext(), null);
            Intent intent = new Intent(FirstActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Funzione che fa partire il Login Automatico con Credenziali di Test
     */
    private void setOnClickListenerBtnLoginTest() {
        btnLoginTest.setOnClickListener(view -> {
            final String email = "utenteditest@email.com";
            final String password = "Test123456";
            if (!NetworkConnectivity.check(getApplicationContext())) {
                Toast.makeText(FirstActivity.this, R.string.no_connection, Toast.LENGTH_SHORT).show();
                return;
            }
            final FirebaseAuth fAuth = FirebaseAuth.getInstance();
            progressBarLoginTest.setVisibility(View.VISIBLE);
            fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    progressBarLoginTest.setVisibility(View.GONE);
                    Toast.makeText(FirstActivity.this, R.string.logged_in, Toast.LENGTH_SHORT).show();

                    LoginActivity.addNewSessionUid(getApplicationContext(), fAuth.getUid());
                    Intent i = new Intent(FirstActivity.this, MainActivity.class);
                    startActivity(i);
                    finishAffinity(); // Non si può tornare indietro con il pulsane Back
                }
                else {
                    progressBarLoginTest.setVisibility(View.GONE);
                    Toast.makeText(FirstActivity.this, R.string.failed_to_log, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}