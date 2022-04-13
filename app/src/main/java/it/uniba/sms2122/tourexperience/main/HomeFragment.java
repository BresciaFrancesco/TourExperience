package it.uniba.sms2122.tourexperience.main;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.model.Museo;
import it.uniba.sms2122.tourexperience.musei.SceltaMusei;

/**
 * Fragment per la schermata Home
 * @author Catignano Francesco
 */
public class HomeFragment extends Fragment {

    private AutoCompleteTextView autoCompleteTextView;
    private static DatabaseReference reference;
    private static final String TABLE_NAME = "Museums";

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO Qui bisogna prendere i dati sui percorsi preferiti
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        autoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.autocomplete);
        // setThreshold() is used to specify the number of characters after which
        // the dropdown with the autocomplete suggestions list would be displayed.
        autoCompleteTextView.setThreshold(1);
        reference = FirebaseDatabase.getInstance().getReference(TABLE_NAME);

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                museumsSearch(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        reference.addListenerForSingleValueEvent(eventListener);
    }

    private void museumsSearch(DataSnapshot snapshot) {
        ArrayList<String> arrayList = new ArrayList<>();
        if(snapshot.exists()){
            for(DataSnapshot ds : snapshot.getChildren()){
                String nome = ds.child("nome").getValue(String.class);
                arrayList.add(nome);
                String citta = ds.child("citta").getValue(String.class);
                arrayList.add(citta);
                String tipologia = ds.child("tipologia").getValue(String.class);
                arrayList.add(tipologia);
            }

            ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,arrayList);

            autoCompleteTextView.setAdapter(adapter);

            autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String string = autoCompleteTextView.getText().toString();

                    Intent intent = new Intent(getContext(), SceltaMusei.class);
                    intent.putExtra("search",string);
                    startActivity(intent);
                }
            });
        }
    }
}