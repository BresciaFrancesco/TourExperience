package it.uniba.sms2122.tourexperience;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.Objects;
import java.util.Set;

import it.uniba.sms2122.tourexperience.holders.UserHolder;
import it.uniba.sms2122.tourexperience.model.User;
import it.uniba.sms2122.tourexperience.profile.ProfileActivity;
import it.uniba.sms2122.tourexperience.welcome.WelcomeActivity;

public class HomeActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        // Prendo l'utente per il nome
        UserHolder userHolder = UserHolder.getInstance();
        userHolder.getUser(
                (user) -> {
                    String title = getString(R.string.hello, user.getName());
                    Objects.requireNonNull(getSupportActionBar()).setTitle(title);
                },
                () -> { }
        );
        getString(R.string.hello, "Francesco");

        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.favorites_recycle_view);

        // TODO Da qui vanno prese le info da mostrare sui percorsi favoriti
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
                return super.onOptionsItemSelected(item);}
        }

}