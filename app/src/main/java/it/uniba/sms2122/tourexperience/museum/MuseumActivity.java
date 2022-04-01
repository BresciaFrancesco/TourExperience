package it.uniba.sms2122.tourexperience.museum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import it.uniba.sms2122.tourexperience.R;

public class MuseumActivity extends AppCompatActivity {

    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager)findViewById(R.id.museumViewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);
    }
}