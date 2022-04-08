package it.uniba.sms2122.tourexperience.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.holders.UserHolder;
import it.uniba.sms2122.tourexperience.musei.SceltaMusei;
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
            switch(item.getItemId()) {
                case R.id.home:
                    fragmentManager.beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.content_fragment_container_view, HomeFragment.class, null)
                            .commit();
                    return true;
                case R.id.history:

                    return true;
                case R.id.museums:
                    startActivity(new Intent(this, SceltaMusei.class));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}