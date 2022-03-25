package it.uniba.sms2122.tourexperience;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import it.uniba.sms2122.tourexperience.profile.ProfileActivity;
import it.uniba.sms2122.tourexperience.registration.RegistrationActivity;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText usernameEdit,passwordEdit;
    private Button loginBtn;
    private ProgressBar progressBar;
    private TextView registerTxt;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameEdit = findViewById(R.id.idEdtUserName);
        passwordEdit = findViewById(R.id.idEdtPassword);
        loginBtn = findViewById(R.id.idBtnLogin);
        progressBar = findViewById(R.id.idPBLoading);
        registerTxt = findViewById(R.id.idTVNewUser);
        fAuth = FirebaseAuth.getInstance();

        // Apre RegistrationActivity
        registerTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(i);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String username = usernameEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                if(checkCredentials(username,password)) {
                    fAuth.signInWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(LoginActivity.this, R.string.logged_in, Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(i);
                                finish(); // Non si può tornare indietro con il pulsane Back
                            }
                            else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(LoginActivity.this, R.string.failed_to_log, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private boolean checkCredentials(String username, String password) {
        if(username.isEmpty()) {
            String username_required = Integer.toString(R.string.username_required);
            usernameEdit.setError(username_required);
            usernameEdit.requestFocus();
            progressBar.setVisibility(View.GONE);
            return false;
        }
        if(password.isEmpty()) {
            String password_required = Integer.toString(R.string.password_required);
            passwordEdit.setError(password_required);
            passwordEdit.requestFocus();
            progressBar.setVisibility(View.GONE);
            return false;
        }
        return true;
    }

    // Controlla se l'utente è già autenticato quando si apre l'app
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = fAuth.getCurrentUser();
        if(user != null) {
            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(i);
            this.finish(); // Non posso tornare all'intent precedente
        }
    }
}