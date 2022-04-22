package it.uniba.sms2122.tourexperience.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Objects;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.holders.UserHolder;
import it.uniba.sms2122.tourexperience.musei.SceltaMuseiFragment;
import it.uniba.sms2122.tourexperience.profile.ProfileActivity;

public class MainActivity extends AppCompatActivity {
    private UserHolder userHolder;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userHolder = UserHolder.getInstance();
        userHolder.getUser(
                (user) -> {
                    String title = getString(R.string.hello, user.getName());
                    Objects.requireNonNull(getSupportActionBar()).setTitle(title);
                },
                () -> {}
        );
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    @Override
    protected void onStart() {
        super.onStart();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    bottomNavigationView.setItemActiveIndicatorEnabled(true);
                    fragmentManager.beginTransaction()
                            .setReorderingAllowed(true)
                            .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                            .replace(R.id.content_fragment_container_view, HomeFragment.class, null)
                            .commit();
                    return true;
                case R.id.history:
                    return true;
                case R.id.museums:
                    fragmentManager.beginTransaction()
                            .setReorderingAllowed(true)
                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                            .replace(R.id.content_fragment_container_view, SceltaMuseiFragment.class, null)
                            .addToBackStack("SceltaMuseiFragment")
                            .commit();
                    Objects.requireNonNull(MainActivity.this.getSupportActionBar()).setTitle(R.string.museums);
                    return true;
                case R.id.game_statistics:
                    return true;
                default:
                    return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId){

            case R.id.profile_pic:

                Intent openProfileIntent = new Intent(this, ProfileActivity.class);
                startActivity(openProfileIntent);
                return  true;

            case R.id.language:

                //code for language setting

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Funzione utile a gestire la pressione del pulsante back in modo tale che quando viene premuto il pulsante non venga chiusa l'app ma si scorra a ritroso
     * scorrendo tra i vari fragment visitati dall'ultimo sino alla home
     */
    @Override
    public void onBackPressed() {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        if(!getTopFragment())
            super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    /**
     * Funzione untile a individuare se ci sono entry all'interno del backstack
     * Se c'è solo una entry setta il focus sull'icona della home
     * Altrimenti verifica quale sia il penultimo fragment attivato e ne attiva l'icona corrispondente
     * @return Restuisce false se non ci sono entry, altrimenti esegue le varie operazioni e restituisce true
     */
    private boolean getTopFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            return false;
        }

        if(getSupportFragmentManager().getBackStackEntryCount() == 1){
            bottomNavigationView.getMenu().getItem(0).setChecked(true);
        } else{
            String fragmentTag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 2).getName();
            switch (fragmentTag){
                case "HomeFragment":
                    bottomNavigationView.getMenu().getItem(0).setChecked(true);
                    break;
                case "SceltaMuseiFragment":
                    bottomNavigationView.getMenu().getItem(2).setChecked(true);
                    break;

                    //TODO da aggiornare lo switch con casi per i fragment riguardanti la history e le statistiche e verificare che funziona
            }
        }

        return true;

    }

    public void replaceSceltaMuseiFragment(Bundle bundle){
        SceltaMuseiFragment sceltaMuseiFragment = new SceltaMuseiFragment();
        sceltaMuseiFragment.setArguments(bundle);

        bottomNavigationView.getMenu().getItem(2).setChecked(true);
        //passare al SceltaMuseiFragment
        fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right)
                .replace(R.id.content_fragment_container_view, sceltaMuseiFragment)
                .addToBackStack(null)
                .commit();
    }

    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}