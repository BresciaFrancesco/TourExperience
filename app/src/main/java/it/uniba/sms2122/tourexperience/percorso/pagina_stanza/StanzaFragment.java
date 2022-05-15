package it.uniba.sms2122.tourexperience.percorso.pagina_stanza;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.graph.Percorso;
import it.uniba.sms2122.tourexperience.model.Opera;
import it.uniba.sms2122.tourexperience.model.Stanza;
import it.uniba.sms2122.tourexperience.operadetection.BleService;
import it.uniba.sms2122.tourexperience.percorso.PercorsoActivity;
import it.uniba.sms2122.tourexperience.utility.Permesso;
import it.uniba.sms2122.tourexperience.utility.filesystem.LocalFilePercorsoManager;

public class StanzaFragment extends Fragment {
    TextView textView;
    RecyclerView recycleView;
    private NearbyOperasAdapter adapter;
    private PercorsoActivity percorsoActivity;
    private LocalFilePercorsoManager localFilePercorsoManager;
    private Percorso path;
    private Stanza stanza;
    private Map<String, Opera> opereInStanza;
    private BleService service;
    private boolean bounded = false;
    private boolean canScanWithBluetooh = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            BleService.LocalBinder binder = (BleService.LocalBinder) iBinder;
            service = binder.getService();
            bounded = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bounded = false;
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    View inflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater =  inflater.inflate(R.layout.fragment_stanza, container, false);
        return this.inflater;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        percorsoActivity = (PercorsoActivity) getActivity();
        path = percorsoActivity.getPath();
        localFilePercorsoManager = new LocalFilePercorsoManager(requireContext().getFilesDir().toString());

        textView = view.findViewById(R.id.stanza_description);
        recycleView = view.findViewById(R.id.opere_recycle_view);

        /* Caricamento delle opere */
        String idStanzaCorrente = path.getIdStanzaCorrente();
        if(idStanzaCorrente.equals(path.getIdStanzaIniziale())) {   // Se la stanza attuale è la prima del percorso...
            // Caricamento delle opere nelle stanze adiacenti
            localFilePercorsoManager.createStanzeAndOpereInThisAndNextStanze(path);
        } else {
            // Caricamento delle opere nelle stanze adiacenti
            new Thread(() -> localFilePercorsoManager.createStanzeAndOpereOnlyInNextStanze(path)).start();
        }

        stanza = path.getStanzaCorrente();
        opereInStanza = stanza.getOpere();
        textView.setText(stanza.getDescrizione());

        adapter = new NearbyOperasAdapter(getContext());
        recycleView.setAdapter(adapter);

        /* TODO: implementare bundle per opera_activity
        adapter.notifyDataSetChanged();
        adapter.setOnItemClickListener(str -> {
            Bundle bundle = new Bundle();
            bundle.putString("nome_percorso", nomiPercorsi.get(Integer.parseInt(str)));
            ((PercorsoActivity) getActivity()).nextPercorsoFragment(bundle);
        }); */

        triggerOperaScanButton();
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(requireContext(), BleService.class);
        intent.putExtra("opere", (Serializable) opereInStanza);
        percorsoActivity.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        // Thread per prendere le opere
        new Thread(() -> {
            do {
                try {
                    Thread.sleep(BleService.SECONDS_FOR_DETECTION * 1000);
                }
                catch(InterruptedException e) {
                    e.printStackTrace();
                }

                if(service != null) {
                    percorsoActivity.runOnUiThread(() -> {
                        adapter.addOperas(service.getNearbyOperas());
                        adapter.notifyDataSetChanged();
                    });
                }
            } while(bounded);
        }).start();
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        percorsoActivity.unbindService(serviceConnection);
    }

    /**
     * funzione per triggerare il click sul pulsante scansione un opera
     */
    private void triggerOperaScanButton() {

        ConstraintLayout operaScanButton = inflater.findViewById(R.id.operaScannButton);

        operaScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((PercorsoActivity)getActivity()).nextQRScannerFragmentOfSingleRomm();
            }
        });

    }

    /**
     * Resituisce lo stato del service.
     * @return bounded
     */
    public boolean isBounded() {
        return bounded;
    }

    /**
     * Chiama unBindService()
     */
    public void unBindService() {
        percorsoActivity.unbindService(serviceConnection);
    }
}