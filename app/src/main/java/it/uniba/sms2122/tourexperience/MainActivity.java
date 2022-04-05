package it.uniba.sms2122.tourexperience;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import it.uniba.sms2122.tourexperience.registration.RegistrationActivity;
import it.uniba.sms2122.tourexperience.welcome.WelcomeActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnLogin;
    private Button btnRegistration;
    private TextView textViewGuest;
    private ActionBar actionBar;
    private FirebaseAuth fAuth;
    private ActivityOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        automaticLogin();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Check sulla prima apertura
        //TODO aggiungeere le shared preference per salvare email e password una volta fatto il login ed non passare più dalla main activity
        SharedPreferences prefs = getSharedPreferences(BuildConfig.SHARED_PREFS, MODE_PRIVATE);
        if(!prefs.contains(BuildConfig.SP_FIRST_OPENING)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(BuildConfig.SP_FIRST_OPENING, true);
            editor.apply();
        }
        if(prefs.getBoolean(BuildConfig.SP_FIRST_OPENING, true)) {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
            return;
        }

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

    // Controlla se l'utente è già autenticato quando si apre l'app
    private void automaticLogin() {
        fAuth = FirebaseAuth.getInstance();
        SharedPreferences prefs = getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
        String email = prefs.getString("EMAIL",null);
        String password = prefs.getString("PASSWORD",null);

        if (email != null && password != null) {
            fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //TODO fare i controlli che l'account sia effettivamente registrato sul database
                    if(task.isSuccessful()) {
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(intent, options.toBundle());
                        finish(); // Non si può tornare indietro con il pulsane Back
                    }
                }
            });
        }


    }

    private void setOnClickListenerBtnLogin() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent, options.toBundle());
            }
        });
    }

    private void setOnClickListenerBtnRegistration() {
        btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(intent, options.toBundle());
            }
        });
    }

    private void setOnClickListenerTextViewGuest() {
        textViewGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent, options.toBundle());
            }
        });
    }
}