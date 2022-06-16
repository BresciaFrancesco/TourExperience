package it.uniba.sms2122.tourexperience.welcome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

import it.uniba.sms2122.tourexperience.BuildConfig;
import it.uniba.sms2122.tourexperience.FirstActivity;
import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.ThreadManager;

public class WelcomeActivity extends FragmentActivity implements BottomWelcomeFragment.OnChangePageListener {
    private static final int NUM_PAGES = 4; // numero di pagine del welcome
    private ViewPager2 viewPager;
    private final ArrayList<Integer> images = new ArrayList<>(); // Le immagini da mostrare
    private String[] descriptions;
    private BottomWelcomeFragment bottomWelcomeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Inizializzazione del view pager
        viewPager = findViewById(R.id.welcome_view_pager);
        viewPager.setAdapter(new WelcomePageAdapter(this)); // Fornisce le pagine al view pager
        viewPager.registerOnPageChangeCallback(new WelcomeOnPageChangeCallback());  // Registra il cambiamento di pagina
        TabLayout tabLayout = findViewById(R.id.welcome_tab_layout);    // Layout per i punti
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> { }).attach();   // Collega il tab layout con il view pager
        bottomWelcomeFragment = (BottomWelcomeFragment) getSupportFragmentManager().findFragmentById(R.id.bottom_welcome_fragment_container);   // Fragment contenente i bottoni di navigazione

        // Inizializzazione delle risorse
        descriptions = getResources().getStringArray(R.array.welcome_descriptions);
        images.add(R.drawable.ic_tour); // Page 1
        images.add(R.drawable.ic_qr);   // Page 2
        images.add(R.drawable.ic_puzzle);   // Page 3
        images.add(R.drawable.ic_museum);   // Page 4
    }

    @Override
    public void onBackPressed() {
        if(viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem()-1);
        }
    }

    @Override
    public void nextPage() {
        int actualPage = viewPager.getCurrentItem()+1;
        viewPager.setCurrentItem(actualPage);
        bottomWelcomeFragment.setStateByPage(actualPage, NUM_PAGES-1);
    }

    @Override
    public void lastPage() {
        viewPager.setCurrentItem(NUM_PAGES-1);
        bottomWelcomeFragment.setStateByPage(viewPager.getCurrentItem(), NUM_PAGES-1);
    }

    @Override
    public void goToMain() {
        changeSharedPrefs();
        try {
            // join del thread che effettua l'unzip della directory di TEST
            ThreadManager.joinThread();
        } catch (InterruptedException e) {
            Log.e("joinThread", "Thread interrotto");
            e.printStackTrace();
        }
        Intent toLogin = new Intent(this, FirstActivity.class);
        startActivity(toLogin);
        finish();
    }

    private void changeSharedPrefs() {
        SharedPreferences prefs = getSharedPreferences(BuildConfig.SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(BuildConfig.SP_FIRST_OPENING, false);
        editor.apply();
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
            return WelcomeFragment.newInstance(images.get(position), descriptions[position]);
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }

    /**
     * Classe che estende ViewPager2.OnPageChangeCallback e si occupa di cambiare le pagine
     */
    private class WelcomeOnPageChangeCallback extends ViewPager2.OnPageChangeCallback {
        public WelcomeOnPageChangeCallback() {
            super();
        }

        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            bottomWelcomeFragment.setStateByPage(position, NUM_PAGES-1);
        }
    }
}