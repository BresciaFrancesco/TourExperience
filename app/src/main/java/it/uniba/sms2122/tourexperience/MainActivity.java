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

import java.util.Objects;

import it.uniba.sms2122.tourexperience.holders.UserHolder;
import it.uniba.sms2122.tourexperience.model.User;
import it.uniba.sms2122.tourexperience.registration.RegistrationActivity;
import it.uniba.sms2122.tourexperience.welcome.WelcomeActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnLogin;
    private Button btnRegistration;
    private TextView textViewGuest;
    private ActionBar actionBar;

    private UserHolder userHolder;
    private ActivityOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userHolder = UserHolder.getInstance();
        userHolder.getUser(
                (user) -> {
                    getSupportActionBar().hide();

                    Intent intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                    supportFinishAfterTransition(); // Non si può tornare indietro con il pulsane Back
                },
                () -> {
                    // Check sulla prima apertura
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

                    setContentView(R.layout.activity_main);

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
        );
    }

    private void setOnClickListenerBtnLogin() {
        btnLogin.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent, options.toBundle());
        });
    }

    private void setOnClickListenerBtnRegistration() {
        btnRegistration.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
            startActivity(intent, options.toBundle());
        });
    }

    private void setOnClickListenerTextViewGuest() {
        textViewGuest.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent, options.toBundle());
            finish();
        });
    }
}