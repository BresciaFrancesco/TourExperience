package it.uniba.sms2122.tourexperience.registration;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import it.uniba.sms2122.tourexperience.R;
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
            transaction.setReorderingAllowed(true);
            transaction.add(R.id.container_fragments_registration, firstPage);
            transaction.commit();
        }
    }

    // Gestisce il pulsante "back" nella action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // gestisce il pulsante di back
                // API 5+ solution
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        fAuth.createUserWithEmailAndPassword(bundle.getString("email"), bundle.getString("password"))
                .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                final FirebaseUser fbUser = fAuth.getCurrentUser();
                String userID = fbUser.getUid();
                dbReference = FirebaseDatabase.getInstance("https://tour-experience-default-rtdb.europe-west1.firebasedatabase.app").getReference("Users").child(userID);
                User user = new User(bundle.getString("name"), bundle.getString("surname"), bundle.getString("dateBirth"));
                dbReference.setValue(user).addOnCompleteListener(taskUserValuesSet -> {
                    if (taskUserValuesSet.isSuccessful()) {
                        Toast.makeText(this, R.string.success_registration, Toast.LENGTH_LONG).show();
                        fAuth.signOut(); // sembra che l'utente effettui il login automaticamente dopo la registrazione
                        finish();
                    }
                    else {
                        Toast.makeText(this, "come stai", Toast.LENGTH_LONG).show();
                        fbUser.delete();
                    }
                });
            }
            else {
                Toast.makeText(this, "ciao", Toast.LENGTH_LONG).show();
            }
        });
    }

    public CheckRegistration getChecker() {
        return checker;
    }
}