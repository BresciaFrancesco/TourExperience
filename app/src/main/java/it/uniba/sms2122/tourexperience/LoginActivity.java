package it.uniba.sms2122.tourexperience;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import it.uniba.sms2122.tourexperience.main.MainActivity;
import it.uniba.sms2122.tourexperience.registration.CheckCredentials;
import it.uniba.sms2122.tourexperience.utility.connection.NetworkConnectivity;

public class LoginActivity extends AppCompatActivity {

    private CheckCredentials checker;
    private TextInputEditText emailEdit,passwordEdit;
    private Button loginBtn;
    private ProgressBar progressBar;
    private ActionBar actionBar;

    private FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.activity_login);
        emailEdit = findViewById(R.id.idEdtUserName);
        passwordEdit = findViewById(R.id.idEdtPassword);
        loginBtn = findViewById(R.id.idBtnLogin);
        progressBar = findViewById(R.id.idPBLoading);
        fAuth = FirebaseAuth.getInstance();

        checker = new CheckCredentials();

        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.login);

        setOnClickListenerLoginBtn();
    }

    private void setOnClickListenerLoginBtn() {
        loginBtn.setOnClickListener(view -> {
            String email = Objects.requireNonNull(emailEdit.getText()).toString();
            String password = Objects.requireNonNull(passwordEdit.getText()).toString();

            if(checker.checkEmail(emailEdit,LoginActivity.this) && checker.checkPassword(passwordEdit,LoginActivity.this)) {
                if (!NetworkConnectivity.check(getApplicationContext())) {
                    Toast.makeText(LoginActivity.this, R.string.no_connection, Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, R.string.logged_in, Toast.LENGTH_SHORT).show();

                        addNewSessionUid(getApplicationContext(), FirebaseAuth.getInstance().getUid());
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(i);
                        finishAffinity(); // Non si può tornare indietro con il pulsane Back
                    }
                    else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, R.string.failed_to_log, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // API 5+ solution
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    /**
     * Aggiunge al file sharedPreferences l'uid cloud dell'utente loggato.
     * @param context contesto app android.
     * @param uid user id da inserire.
     */
    public static void addNewSessionUid(final Context context, final String uid) {
        final SharedPreferences.Editor editor = context.getSharedPreferences(BuildConfig.SHARED_PREFS, MODE_PRIVATE).edit();
        editor.putString(context.getString(R.string.uid_preferences), uid);
        editor.apply();
    }
}