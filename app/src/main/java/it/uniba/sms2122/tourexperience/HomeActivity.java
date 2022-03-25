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

import it.uniba.sms2122.tourexperience.profile.ProfileActivity;
import it.uniba.sms2122.tourexperience.welcome.WelcomeActivity;

public class HomeActivity extends AppCompatActivity {
    RecyclerView recyclerView;

    private static final String SHARED_PREFS = "shared_preferences";
    private static final String FIRST_OPENING_KEY = "isFirstOpening";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check sulla prima apertura
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        if(!prefs.contains(FIRST_OPENING_KEY)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(FIRST_OPENING_KEY, true);
            editor.apply();
        }
        if(prefs.getBoolean(FIRST_OPENING_KEY, true)) {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
            return;
        }

        String title = getString(R.string.hello, "Francesco");  //TODO inserire nome vero
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);

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