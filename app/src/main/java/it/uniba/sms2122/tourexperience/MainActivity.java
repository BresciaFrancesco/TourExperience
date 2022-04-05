package it.uniba.sms2122.tourexperience;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.uniba.sms2122.tourexperience.model.User;
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
        super.onCreate(savedInstanceState);

        fAuth = FirebaseAuth.getInstance();
        FirebaseUser fbUser = fAuth.getCurrentUser();

        if (fbUser != null) {
            String userID = fbUser.getUid();
            DatabaseReference dbReference = FirebaseDatabase.getInstance("https://tour-experience-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users").child(userID);
            dbReference.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.putExtra("nameUser", task.getResult().getValue(User.class).getName());
                    finish(); // Non si può tornare indietro con il pulsane Back
                    startActivity(intent);
                }
            });

        }else{
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