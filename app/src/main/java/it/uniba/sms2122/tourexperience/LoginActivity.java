package it.uniba.sms2122.tourexperience;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.uniba.sms2122.tourexperience.model.User;
import it.uniba.sms2122.tourexperience.registration.CheckCredentials;

public class LoginActivity extends AppCompatActivity {

    private CheckCredentials checker;
    private TextInputEditText emailEdit,passwordEdit;
    private Button loginBtn;
    private ProgressBar progressBar;
    private ActionBar actionBar;

    private FirebaseAuth fAuth;
    private DatabaseReference dbReference;

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
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEdit.getText().toString();
                String password = passwordEdit.getText().toString();

                if(checker.checkEmail(emailEdit,LoginActivity.this) && checker.checkPassword(passwordEdit,LoginActivity.this)) {
                    progressBar.setVisibility(View.VISIBLE);
                    fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {

                                getUserFromFirebase(email, password);
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

    private void getUserFromFirebase(String email, String password) {

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                //Non faccio il controllo che esistano già per gestire il caso in cui faccia il login con altro account oltre a quello creato con la registrazione
                SharedPreferences prefs = getSharedPreferences("MySharedPreferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("EMAIL", email);
                editor.putString("PASSWORD", password);
                editor.putString("NAME", user.getName());
                editor.commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("loadUser:onCancelled", error.toException());
            }
        };

        final FirebaseUser fbUser = fAuth.getCurrentUser();
        String userID = fbUser.getUid();
        dbReference = FirebaseDatabase.getInstance("https://tour-experience-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users").child(userID);
        dbReference.addValueEventListener(listener);
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