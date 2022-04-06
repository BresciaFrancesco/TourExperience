package it.uniba.sms2122.tourexperience.museum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.TextView;

import it.uniba.sms2122.tourexperience.DataBaseHelper;
import it.uniba.sms2122.tourexperience.R;

public class MuseumActivity extends AppCompatActivity {

    ViewPager viewPager;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager = (ViewPager)findViewById(R.id.museum_viewpager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

        textView = findViewById(R.id.museum_description);
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);

        textView.setText(dataBaseHelper.loadHandler());
    }
}