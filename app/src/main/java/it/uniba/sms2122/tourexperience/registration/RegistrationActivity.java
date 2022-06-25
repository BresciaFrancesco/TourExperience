package it.uniba.sms2122.tourexperience.registration;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import it.uniba.sms2122.tourexperience.LoginActivity;
import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.holders.UserHolder;

public class RegistrationActivity extends AppCompatActivity {

    private CheckCredentials checker;
    private ActionBar actionBar;
    private FirebaseAuth fAuth;
    private DatabaseReference dbReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.activity_registration);

        checker = new CheckCredentials();
        actionBar = getSupportActionBar();
        assert actionBar != null;
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

    /**
     * Funzione che serve a sostituire il precedente fragment con StanzaFragment
     * @param bundle, email e password inseriti nel fragment precedente
     */
    public void nextFragment(Bundle bundle) {
        Fragment secondPage = new RegistrationFragmentSecondPage();

        secondPage.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setReorderingAllowed(true);
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.container_fragments_registration, secondPage);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    /**
     * Funzione che si occupa della registrazione di un nuovo utente su firebase e poi fa partire LoginActivity
     * @param bundle, email, password, nome, cognome e data di nascita del nuovo utente che si vuole registrare
     */
    public void registration(Bundle bundle) {
        UserHolder userHolder = UserHolder.getInstance();
        userHolder.register(bundle.getString("email"), bundle.getString("password"), bundle.getString("name"), bundle.getString("surname"), bundle.getString("dateBirth"),
                () -> {
                    Toast.makeText(this, R.string.success_registration, Toast.LENGTH_LONG).show();
                    fAuth.signOut();
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                },
                (String errorMsg) -> {
                    if(errorMsg.contains("com.google.firebase.auth.FirebaseAuthInvalidCredentialsException")) {
                        Toast.makeText(this, R.string.credentials_not_accepted, Toast.LENGTH_LONG).show();
                    }
                    else if(errorMsg.contains("com.google.firebase.auth.FirebaseAuthUserCollisionException")) {
                        Toast.makeText(this, R.string.email_already_use, Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(this, R.string.failed_registration, Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public CheckCredentials getChecker() {
        return checker;
    }
}