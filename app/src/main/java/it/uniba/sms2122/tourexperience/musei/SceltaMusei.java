package it.uniba.sms2122.tourexperience.musei;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import it.uniba.sms2122.tourexperience.R;

public class SceltaMusei extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scelta_musei);

        recyclerView = findViewById(R.id.recyclerView);

        // Setting the layout as linear
        // layout for vertical orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        // Sending reference and data to Adapter
        //MuseiAdapter adapter = new MuseiAdapter(this, courseImg, courseName);

        // Setting Adapter to RecyclerView
        //recyclerView.setAdapter(adapter);
    }


    @Override
    protected void onStart() {
        super.onStart();

    }
}