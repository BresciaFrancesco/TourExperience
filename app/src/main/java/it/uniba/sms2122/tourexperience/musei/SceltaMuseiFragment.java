package it.uniba.sms2122.tourexperience.musei;

import android.app.ActionBar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.main.MainActivity;
import it.uniba.sms2122.tourexperience.model.Museo;
import it.uniba.sms2122.tourexperience.utility.LocalFileMuseoManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SceltaMuseiFragment} factory method to
 * create an instance of this fragment.
 */
public class SceltaMuseiFragment extends Fragment {

    private SearchView searchView;
    private ActionBar actionBar;
    private RecyclerView recyclerView;
    private List<Museo> listaMusei;
    private LocalFileMuseoManager localFileManager;

    @Override
    public void onResume() {
        super.onResume();

        try {
            listaMusei = localFileManager.getListMusei();

            // TEST
            for (Museo museo : listaMusei) {
                Log.v("Info Museo", museo.toString());
            }

            Bundle bundle = this.getArguments();

            if ((bundle != null)) {
                listaMusei = searchData(listaMusei, bundle.getString("search"));
            }
        } catch (IOException e) {
            Log.e("SCELTA_MUSEI_ERROR", "Lista musei non caricata.");
            listaMusei = new ArrayList<>();
            e.printStackTrace();
        }

        if (listaMusei.isEmpty()) {
            listaMusei.add(new Museo(getContext().getResources().getString((R.string.no_result)), "","",""));
        }

        // Sending reference and data to Adapter
        MuseiAdapter adapter = new MuseiAdapter(getContext(), listaMusei);
        // Setting Adapter to RecyclerView
        recyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.hideKeyboard(getContext());

        return inflater.inflate(R.layout.fragment_scelta_musei, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        createLocalDirectory();

        // METODO DI TEST, USARE SOLO UNA VOLTA E POI ELIMINARE
        //test_downloadImageAndSaveInLocalStorage();

        searchView = view.findViewById(R.id.searchviewMusei);

        recyclerView = view.findViewById(R.id.recyclerViewMusei);
        // Setting the layout as linear layout for vertical orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        localFileManager = new LocalFileMuseoManager(getContext().getFilesDir().toString());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        ArrayList<String> nomiMusei = new ArrayList<>();
        ArrayList<String> cittaMusei = new ArrayList<>();
        ArrayList<String> tipologieMusei = new ArrayList<>();
        ArrayList<String> uriImmagini = new ArrayList<>();
        for (int i = 0; i < listaMusei.size(); i++) {
            nomiMusei.add(listaMusei.get(i).getNome());
            cittaMusei.add(listaMusei.get(i).getCitta());
            tipologieMusei.add(listaMusei.get(i).getTipologia());
            uriImmagini.add(listaMusei.get(i).getFileUri().toString());
        }
        outState.putStringArrayList("nomi_musei", nomiMusei);
        outState.putStringArrayList("citta_musei", cittaMusei);
        outState.putStringArrayList("tipologie_musei", tipologieMusei);
        outState.putStringArrayList("immagini_musei", uriImmagini);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@NonNull Bundle savedInstanceState) {
        listaMusei = new ArrayList<>();

        if(savedInstanceState != null)
        {
            ArrayList<String> nomiMusei = savedInstanceState.getStringArrayList("nomi_musei");
            ArrayList<String> cittaMusei = savedInstanceState.getStringArrayList("citta_musei");
            ArrayList<String> tipologieMusei = savedInstanceState.getStringArrayList("tipologie_musei");
            ArrayList<String> uriImmagini = savedInstanceState.getStringArrayList("immagini_musei");
            for (int i = 0; i < nomiMusei.size(); i++) {
                listaMusei.add(new Museo(
                        nomiMusei.get(i),
                        cittaMusei.get(i),
                        tipologieMusei.get(i),
                        uriImmagini.get(i)
                ));
            }
        }

        super.onViewStateRestored(savedInstanceState);
    }

    /**
     * Metodo di test per effettuare il download la prima volta di
     * due musei da firebase e salvarli in locale.
     */
    private void test_downloadImageAndSaveInLocalStorage() {
        ArrayList<String> musei = new ArrayList<>();
        musei.add("Louvre");
        musei.add("Hermitage");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        File fullPath = new File(getContext().getFilesDir() + "/Museums");

        for (String museo : musei) {
            String filePathImmagine = museo+"/"+museo+".png";
            String filePathInfo = museo+"/"+"Info.json";

            // Ottengo il riferimento su Firebase dell'immagine del museo e del file Info.json
            StorageReference rifImmagine = storage.getReference("Museums").child(filePathImmagine);
            StorageReference rifInfo = storage.getReference("Museums").child(filePathInfo);

            File dir = new File(fullPath, museo);
            if (!dir.exists())
                dir.mkdir();


            File localFileImmagine = new File(fullPath, filePathImmagine);
            File localFileInfo = new File(fullPath, filePathInfo);

            rifImmagine.getFile(localFileImmagine).addOnSuccessListener(taskSnapshot -> {
                Log.v("CARICAMENTO IMMAGINE", filePathImmagine+" caricato!");
            }).addOnFailureListener(exception -> {
                Log.e("CARICAMENTO IMMAGINE", filePathImmagine+" NON caricato.");
            });

            rifInfo.getFile(localFileInfo).addOnSuccessListener(taskSnapshot -> {
                Log.v("CARICAMENTO INFO", filePathInfo+" caricato!");
            }).addOnFailureListener(exception -> {
                Log.e("CARICAMENTO INFO", filePathInfo+" NON caricato.");
            });
        }
    }

    private void createLocalDirectory() {
        File directory = new File(getContext().getFilesDir(), "Museums");
        if (directory == null || !directory.exists()) {
            if (directory.mkdir())
                Log.v("CREATE_DIRECTORY_Museums", "Created now!");
            else
                Log.e("CREATE_DIRECTORY_Museums", "Error!");
        }
    }

    private List<Museo> searchData(List<Museo> museums, String string) {
        List<Museo> returnList = new ArrayList<>();

        for(Museo museum : museums){
            if(museum.getNome().equals(string) || museum.getCitta().equals(string) || museum.getTipologia().equals(string))
                returnList.add(museum);
        }

        return returnList;
    }
}