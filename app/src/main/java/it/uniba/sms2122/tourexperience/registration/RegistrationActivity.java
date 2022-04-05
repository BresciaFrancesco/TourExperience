package it.uniba.sms2122.tourexperience.registration;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import it.uniba.sms2122.tourexperience.LoginActivity;
import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.holders.UserHolder;
import it.uniba.sms2122.tourexperience.model.User;

public class RegistrationActivity extends AppCompatActivity {

    private CheckRegistration checker;
    private ActionBar actionBar;
    private FirebaseAuth fAuth;
    private DatabaseReference dbReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        checker = new CheckRegistration();
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true); // abilita il pulsante "back" nella action bar
        actionBar.setTitle(R.string.registration);

        fAuth = FirebaseAuth.getInstance();

        if (savedInstanceState == null) {
            Fragment firstPage = new RegistrationFragmentFirstPage();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setReorderingAllowed(true);  //ottimizza i cambiamenti di stato dei fragment in modo che le animazioni funzionino correttammente
            transaction.add(R.id.container_fragments_registration, firstPage);
            transaction.commit();
        }
    }

    // Gestisce il pulsante "back" nella action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // API 5+ solution
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void nextFragment(Bundle bundle) {
        Fragment secondPage = new RegistrationFragmentSecondPage();
        secondPage.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setReorderingAllowed(true);
        transaction.replace(R.id.container_fragments_registration, secondPage);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void registration(Bundle bundle) {
        /*fAuth.createUserWithEmailAndPassword(bundle.getString("email"), bundle.getString("password"))
                .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                final FirebaseUser fbUser = fAuth.getCurrentUser();
                String userID = fbUser.getUid();
                dbReference = FirebaseDatabase.getInstance("https://tour-experience-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users").child(userID);
                User user = new User(bundle.getString("email"), bundle.getString("name"), bundle.getString("surname"), bundle.getString("dateBirth"));
                dbReference.setValue(user).addOnCompleteListener(taskUserValuesSet -> {
                    if (taskUserValuesSet.isSuccessful()) {
                        Toast.makeText(this, R.string.success_registration, Toast.LENGTH_LONG).show();
                        fAuth.signOut();
                        Intent intent = new Intent(this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Toast.makeText(this, R.string.failed_registration, Toast.LENGTH_LONG).show();
                        fbUser.delete();
                    }
                });
            }
            else {
                if(task.getException().toString().contains("com.google.firebase.auth.FirebaseAuthInvalidCredentialsException")){
                    Toast.makeText(this, R.string.credentials_not_accepted, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, R.string.failed_registration, Toast.LENGTH_LONG).show();
                }
                findViewById(R.id.idProgressBarReg).setVisibility(View.GONE);
                onBackPressed();
            }
        });*/
        UserHolder.getInstance().register(bundle.getString("email"), bundle.getString("password"), bundle.getString("name"), bundle.getString("surname"), bundle.getString("dateBirth"),
                () -> {
                    Toast.makeText(this, R.string.success_registration, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                },
                (String errorMsg) -> {
                    if(errorMsg.contains("com.google.firebase.auth.FirebaseAuthInvalidCredentialsException")) {
                        Toast.makeText(this, R.string.credentials_not_accepted, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, R.string.failed_registration, Toast.LENGTH_LONG).show();
                    }
                    findViewById(R.id.idProgressBarReg).setVisibility(View.GONE);
                    onBackPressed();
                });

    }

    public CheckRegistration getChecker() {
        return checker;
    }
}