package it.uniba.sms2122.tourexperience.musei;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.main.MainActivity;
import it.uniba.sms2122.tourexperience.model.Museo;
import it.uniba.sms2122.tourexperience.utility.filesystem.LocalFileMuseoManager;
import it.uniba.sms2122.tourexperience.utility.filesystem.zip.DTO.OpenFileAndroidStorageDTO;
import it.uniba.sms2122.tourexperience.utility.filesystem.zip.OpenFile;

import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.*;
import static it.uniba.sms2122.tourexperience.utility.GenericUtility.thereIsConnection;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SceltaMuseiFragment} factory method to
 * create an instance of this fragment.
 */
public class SceltaMuseiFragment extends Fragment {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private List<Museo> listaMusei;
    private LocalFileMuseoManager localFileManager;
    private FirebaseStorage firebaseStorage;
    private final int requestCodeGC = 10;

    // Make sure to use the FloatingActionButton for all the FABs
    private FloatingActionButton mAddFab, localStorageFab, cloudFab;
    // These are taken to make visible and invisible along with FABs
    private TextView localStorageTxtView, cloudTxtView;
    // to check whether sub FAB buttons are visible or not.
    private Boolean isAllFabsVisible;

    /**
     * Istanzia la lista dei musei, recuperando i musei dal filesystem
     * locale o dalla cache. Se recuperati dal filesystem, vengono
     * inseriti nella cache.
     * @throws IOException
     */
    private void createListMuseums() throws IOException {
        if (listaMusei == null || listaMusei.isEmpty()) {
            if (cacheMuseums.isEmpty()) {
                listaMusei = localFileManager.getListMusei();
                if (listaMusei.isEmpty()) {
                    Log.v("CACHE_MUSEI", "cache e lista musei vuoti");
                    listaMusei = new ArrayList<>();
                }
                else {
                    Log.v("CACHE_MUSEI", "musei recuperati da locale e inseriti nella cache");
                    replaceMuseumsInCache(listaMusei);
                }
            } else {
                Log.v("CACHE_MUSEI", "musei recuperati dalla cache");
                listaMusei = getAllCachedMuseums();
            }
        } else Log.v("CACHE_MUSEI", "musei giò presenti in memoria");
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            createListMuseums();

            Bundle bundle = this.getArguments();
            if (bundle != null) {
                listaMusei = searchData(listaMusei, bundle.getString("search"));
            }
        } catch (IOException e) {
            Log.e("SceltaMuseiFragment", "SCELTA_MUSEI_ERROR: Lista musei non caricata.");
            listaMusei = new ArrayList<>();
            e.printStackTrace();
        }

        if (listaMusei.isEmpty()) {
            listaMusei.add(new Museo(getContext().getResources().getString(R.string.no_result)));
        }

        // Sending reference and data to Adapter
        MuseiAdapter adapter = new MuseiAdapter((MainActivity) getActivity(), progressBar, listaMusei, true);
        // Setting Adapter to RecyclerView
        recyclerView.setAdapter(adapter);

        attachQueryTextListener(adapter);
    }

    /**
     * Collega un listener alla barra di ricerca. In particolare
     * il listener che collega permette di filtrare la lista
     * presente nella recyclerView.
     * @param adapter adapter da utilizzate per il filtraggio.
     */
    public void attachQueryTextListener(MuseiAdapter adapter) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {return false;}
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

        File filesDir = view.getContext().getFilesDir();
        localFileManager = new LocalFileMuseoManager(filesDir.toString());

        firebaseStorage = FirebaseStorage.getInstance();
        searchView = view.findViewById(R.id.searchviewMusei);

        recyclerView = view.findViewById(R.id.recyclerViewMusei);
        progressBar = view.findViewById(R.id.idPBLoading);
        // Setting the layout as linear layout for vertical orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        setAllTheReference(view);

        // We will make all the FABs and action name texts
        // visible only when Parent FAB button is clicked So
        // we have to handle the Parent FAB button first, by
        // using setOnClickListener you can see below
        mAddFab.setOnClickListener(this::listenerFabMusei);

        localStorageFab.setOnClickListener(view2 -> {
            final Runnable openFileExplorer = () -> {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, requestCodeGC);
                hideFabOptions();
            };
            final SharedPreferences sp = ((MainActivity)requireActivity())
                    .getPreferences(Context.MODE_PRIVATE);
            final String spKey = getString(R.string.do_not_show_again_local_import);
            boolean doNotShowAgain = sp.getBoolean(spKey, false);
            if (!doNotShowAgain) {
                new AlertDialog.Builder(view2.getContext())
                .setTitle(getString(R.string.local_import_dialog_title))
                .setMessage(getString(R.string.local_import_message))
                .setPositiveButton("OK", (dialog, whichButton) -> openFileExplorer.run())
                .setNeutralButton(getString(R.string.do_not_show_again), (dialog, whichButton) -> {
                    dialog.dismiss();
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean(spKey, true);
                    editor.apply();
                    openFileExplorer.run();
                })
                .show();
            } else openFileExplorer.run();
        });

        cloudFab.setOnClickListener(view2 -> {
            if (!thereIsConnection(() -> Toast.makeText(
                getContext(),
                getContext().getString(R.string.no_connection),
                Toast.LENGTH_SHORT).show())) return;
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(null);
            hideFabOptions();
            mAddFab.setImageResource(R.drawable.ic_baseline_close_24);
            mAddFab.setBackgroundTintList(
                    ColorStateList.valueOf(
                            getResources().getColor(R.color.main_red, null))
            );
            MainActivity activity = (MainActivity) getActivity();
            activity.getSupportActionBar().setTitle(R.string.museums_cloud_import);
            Back backToMuseumsList = new BackToMuseumsList(
                    activity, this, recyclerView, mAddFab, progressBar);
            // Il FAB torna allo stato iniziale e la lista di musei torna a contenere i musei presenti in cache
            mAddFab.setOnClickListener((view3) -> {
                searchView.setQueryHint(getString(R.string.search_museums));
                backToMuseumsList.back(view3);
            });

            // Impostando questo oggetto in ImportPercorsi, potrò evocare il suo metodo back
            // per tornare allo stato precedente come se avessi cliccato il pulsante
            ImportPercorsi.setBackToMuseumsList(backToMuseumsList);
            // Ottiene da firebase tutti i percorsi
            getListaPercorsiFromCloudStorage();
            searchView.setQueryHint(getString(R.string.search_paths));
        });
    }

    /**
     * Permette di aprire e chiudere i FAB opzionali.
     * @param view
     */
    public void listenerFabMusei(View view) {
        if (!isAllFabsVisible) {
            // when isAllFabsVisible becomes true make all the action name
            // texts and FABs VISIBLE.
            localStorageFab.show();
            cloudFab.show();
            cloudTxtView.setVisibility(View.VISIBLE);
            localStorageTxtView.setVisibility(View.VISIBLE);
            // make the boolean variable true as we have set the sub FABs
            // visibility to GONE
            isAllFabsVisible = true;
        } else {
            hideFabOptions();
        }
    }

    /**
     * Nasconde i pulsanti FAB opzionali.
     */
    private void hideFabOptions() {
        localStorageFab.hide();
        cloudFab.hide();
        cloudTxtView.setVisibility(View.GONE);
        localStorageTxtView.setVisibility(View.GONE);
        isAllFabsVisible = false;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Gestisce solo l'ottenimento del file .zip / .json, avvenuto tramite Intent implicito.
        if (requestCode == requestCodeGC) {
            if (resultCode == MainActivity.RESULT_OK) {
                Uri returnUri = data.getData();
                String mimeType = getActivity().getContentResolver().getType(returnUri);
                String fileName = DocumentFile.fromSingleUri(getContext(), returnUri).getName();
                OpenFile dto = new OpenFileAndroidStorageDTO(getContext(), returnUri);
                boolean res = localFileManager.save(fileName, mimeType, dto);
                Log.v("RISULTATO", String.valueOf(res));
            } else {
                Log.e("SceltaMuseiFragment.onActivityResult", "resultCode " + resultCode);
            }
        }
    }

    /**
     * Ritorna la lista dei percorsi presenti in cloud su Firebase.
     */
    private void getListaPercorsiFromCloudStorage() {
        MuseiAdapter adapterPercorsi = new MuseiAdapter(null, progressBar,
                new ArrayList<>(), false);
        Log.v("IMPORT_CLOUD", "start download...");
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Museums_v2");
        ValueEventListener listener = new
            ListaPercorsiFromCloud(adapterPercorsi, this, progressBar, recyclerView);
        db.addValueEventListener(listener);
    }


    /**
     * Imposta solo i riferimenti per il fragment.
     * @param view
     */
    private void setAllTheReference(View view) {
        // Register all the FABs with their IDs
        // This FAB button is the Parent
        mAddFab = view.findViewById(R.id.add_fab);
        // FAB button
        localStorageFab = view.findViewById(R.id.fab_import_from_localstorage);
        cloudFab = view.findViewById(R.id.fab_import_from_cloud);

        // Also register the action name text, of all the FABs.
        localStorageTxtView = view.findViewById(R.id.txtview_import_from_localstorage);
        cloudTxtView = view.findViewById(R.id.txtview_download_from_cloud);

        // Now set all the FABs and all the action name texts as GONE
        localStorageFab.setVisibility(View.GONE);
        cloudFab.setVisibility(View.GONE);
        localStorageTxtView.setVisibility(View.GONE);
        cloudTxtView.setVisibility(View.GONE);

        // make the boolean variable as false, as all the
        // action name texts and all the sub FABs are invisible
        isAllFabsVisible = false;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.v("SceltaMuseiFragment", "chiamato onSaveInstanceState()");
        ArrayList<String> nomiMusei = new ArrayList<>();
        ArrayList<String> cittaMusei = new ArrayList<>();
        ArrayList<String> descrizioneMusei = new ArrayList<>();
        ArrayList<String> tipologieMusei = new ArrayList<>();
        ArrayList<String> uriImmagini = new ArrayList<>();
        for (int i = 0; i < listaMusei.size(); i++) {
            nomiMusei.add(listaMusei.get(i).getNome());
            cittaMusei.add(listaMusei.get(i).getCitta());
            descrizioneMusei.add(listaMusei.get(i).getDescrizione());
            tipologieMusei.add(listaMusei.get(i).getTipologia());
            uriImmagini.add(listaMusei.get(i).getFileUri());
        }
        outState.putStringArrayList("nomi_musei", nomiMusei);
        outState.putStringArrayList("citta_musei", cittaMusei);
        outState.putStringArrayList("descrizione_musei", descrizioneMusei);
        outState.putStringArrayList("tipologie_musei", tipologieMusei);
        outState.putStringArrayList("immagini_musei", uriImmagini);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@NonNull Bundle savedInstanceState) {
        Log.v("SceltaMuseiFragment", "chiamato onViewStateRestored()");
        listaMusei = new ArrayList<>();
        if(savedInstanceState != null && !savedInstanceState.isEmpty())
        {
            Log.v("SceltaMuseiFragment", "chiamato onViewStateRestored() -> savedInstanceState != null");
            ArrayList<String> nomiMusei = savedInstanceState.getStringArrayList("nomi_musei");
            ArrayList<String> cittaMusei = savedInstanceState.getStringArrayList("citta_musei");
            ArrayList<String> descrizioneMusei = savedInstanceState.getStringArrayList("descrizione_musei");
            ArrayList<String> tipologieMusei = savedInstanceState.getStringArrayList("tipologie_musei");
            ArrayList<String> uriImmagini = savedInstanceState.getStringArrayList("immagini_musei");
            for (int i = 0; i < nomiMusei.size(); i++) {
                listaMusei.add(new Museo(
                    nomiMusei.get(i),
                    cittaMusei.get(i),
                    descrizioneMusei.get(i),
                    tipologieMusei.get(i),
                    uriImmagini.get(i)
                ));
            }
        }
        super.onViewStateRestored(savedInstanceState);
    }


    private List<Museo> searchData(List<Museo> museums, String string) {
        List<Museo> returnList = new ArrayList<>();

        for(Museo museum : museums){
            if(museum.getNome().equals(string) || museum.getCitta().equals(string) || museum.getTipologia().equals(string)){
                returnList.add(museum);
            }
        }

        return returnList;
    }

    public void setListaMusei(List<Museo> listaMusei) {
        this.listaMusei = listaMusei;
    }

    public List<Museo> getListaMusei() {
        return listaMusei;
    }
}