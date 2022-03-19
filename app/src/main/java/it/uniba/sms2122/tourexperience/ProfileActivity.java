package it.uniba.sms2122.tourexperience;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String title = getString(R.string.profile);  //TODO inserire nome vero
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }
}