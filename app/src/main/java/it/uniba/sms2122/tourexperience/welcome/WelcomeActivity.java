package it.uniba.sms2122.tourexperience.welcome;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import it.uniba.sms2122.tourexperience.LoginActivity;
import it.uniba.sms2122.tourexperience.R;

public class WelcomeActivity extends FragmentActivity {
    private static final int NUM_PAGES = 4; // numero di pagine del welcome
    private ViewPager2 viewPager;
    private FragmentStateAdapter pageAdapter;   // Fornisce le pagine al view pager
    private final ArrayList<Drawable> images = new ArrayList<Drawable>(); // Le immagini da mostrare
    private String[] descriptions;
    private BottomWelcomeFragment bottomWelcomeFragment;
    private static final String SHARED_PREFS = "shared_preferences";
    private static final String FIRST_OPENING_KEY = "isFirstOpening";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        viewPager = findViewById(R.id.welcome_view_pager);
        pageAdapter = new WelcomePageAdapter(this);
        viewPager.setAdapter(pageAdapter);
        descriptions = getResources().getStringArray(R.array.welcome_descriptions);
        bottomWelcomeFragment = (BottomWelcomeFragment) getSupportFragmentManager().findFragmentById(R.id.bottom_welcome_fragment_container);

        // Inserimento delle immagini
        images.add((Drawable) ContextCompat.getDrawable(this, R.drawable.ic_tour)); // Page 1
        images.add((Drawable) ContextCompat.getDrawable(this, R.drawable.ic_qr));   // Page 2
        images.add((Drawable) ContextCompat.getDrawable(this, R.drawable.ic_puzzle));   // Page 3
        images.add((Drawable) ContextCompat.getDrawable(this, R.drawable.ic_museum));   // Page 4
    }

    @Override
    public void onBackPressed() {
        if(viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem()-1);
        }
    }

    public void nextPage() {
        if(viewPager.getCurrentItem() < (NUM_PAGES-1)) {
            viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
        }
        if(viewPager.getCurrentItem() == (NUM_PAGES-1)) {
            bottomWelcomeFragment.setLastPage();
        }
    }

    public void lastPage() {
        viewPager.setCurrentItem(NUM_PAGES-1);
        bottomWelcomeFragment.setLastPage();
    }

    public void goToLogin() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(FIRST_OPENING_KEY, false);
        editor.apply();
        Intent toLogin = new Intent(this, LoginActivity.class);
        startActivity(toLogin);
        finish();
    }

    /**
     * Classe che estende FragmentStateAdapter e si occupa di creare i fragment da mostrare
     */
    private class WelcomePageAdapter extends FragmentStateAdapter {
        public WelcomePageAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return new WelcomeFragment(images.get(position), descriptions[position]);
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }
}