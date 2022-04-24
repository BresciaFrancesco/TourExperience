package it.uniba.sms2122.tourexperience.pagina_museo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.TextView;

import it.uniba.sms2122.tourexperience.R;

public class MuseoActivity extends AppCompatActivity {

    ViewPager viewPager;
    TextView textView;
    RecyclerView recycleView;

    // Info items
    String[] names = {"percorso1","percorso2"};
    int[] images = {R.drawable.ic_launcher_background,R.drawable.ic_launcher_background};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager)findViewById(R.id.museum_viewpager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

        textView = findViewById(R.id.museum_description);
        textView.setText("Descrizione museo...");

        recycleView = findViewById(R.id.routes_recycle_view);
        RecycleViewAdapter adapter = new RecycleViewAdapter(this,names,images);
        recycleView.setAdapter(adapter);
    }
}