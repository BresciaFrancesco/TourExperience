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
import it.uniba.sms2122.tourexperience.welcome.WelcomeActivity;

public class FirstActivity extends AppCompatActivity {

    private Button btnLogin;
    private Button btnRegistration;
    private TextView textViewGuest;
    private ActionBar actionBar;

    private UserHolder userHolder;
    private ActivityOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        btnLogin = (Button) findViewById(R.id.idBtnMainLogin);
        btnRegistration = (Button) findViewById(R.id.idBtnMainRegistration);
        textViewGuest = (TextView) findViewById(R.id.idTextViewGuest);

        userHolder = UserHolder.getInstance();
        userHolder.getUser(
                (user) -> {
                    btnLogin.setVisibility(View.GONE);
                    btnRegistration.setVisibility(View.GONE);
                    textViewGuest.setVisibility(View.GONE);
                    findViewById(R.id.idImgLogo).setVisibility(View.GONE);

                    Intent intent = new Intent(this, MainActivity.class);
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
            Intent intent = new Intent(FirstActivity.this, LoginActivity.class);
            startActivity(intent, options.toBundle());
        });
    }

    private void setOnClickListenerBtnRegistration() {
        btnRegistration.setOnClickListener(view -> {
            Intent intent = new Intent(FirstActivity.this, RegistrationActivity.class);
            startActivity(intent, options.toBundle());
        });
    }

    private void setOnClickListenerTextViewGuest() {
        textViewGuest.setOnClickListener(view -> {
            Intent intent = new Intent(FirstActivity.this, MainActivity.class);
            startActivity(intent, options.toBundle());
            finish();
        });
    }
}