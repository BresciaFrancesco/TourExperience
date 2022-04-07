package it.uniba.sms2122.tourexperience;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import it.uniba.sms2122.tourexperience.registration.CheckCredentials;

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
        setContentView(R.layout.activity_login);
        emailEdit = findViewById(R.id.idEdtUserName);
        passwordEdit = findViewById(R.id.idEdtPassword);
        loginBtn = findViewById(R.id.idBtnLogin);
        progressBar = findViewById(R.id.idPBLoading);
        fAuth = FirebaseAuth.getInstance();

        checker = new CheckCredentials();

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.login);

        setOnClickListenerLoginBtn();
    }

    private void setOnClickListenerLoginBtn() {
        loginBtn.setOnClickListener(view -> {
            String email = emailEdit.getText().toString();
            String password = passwordEdit.getText().toString();

            if(checker.checkEmail(emailEdit,LoginActivity.this) && checker.checkPassword(passwordEdit,LoginActivity.this)) {
                progressBar.setVisibility(View.VISIBLE);
                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {

                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, R.string.logged_in, Toast.LENGTH_SHORT).show();

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
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}