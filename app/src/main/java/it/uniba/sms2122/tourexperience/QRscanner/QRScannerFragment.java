package it.uniba.sms2122.tourexperience.QRscanner;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.graph.exception.GraphException;
import it.uniba.sms2122.tourexperience.percorso.PercorsoActivity;
import it.uniba.sms2122.tourexperience.percorso.pagina_stanza.StanzaFragment;

public class QRScannerFragment extends Fragment {

    private CodeScanner mCodeScanner;
    QRscannerDataManager scannerDataManager;

    /**
     * Costruttore
     * @param scannerDataManager, un oggetto da instanziare tramite lambda expression per decidere che fare con i dati letto dallo scanner dei qr
     */
    public QRScannerFragment(QRscannerDataManager scannerDataManager) {
        this.scannerDataManager = scannerDataManager;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Fragment thisFragment = this;
        final Activity parentActivity = getActivity();
        View root = inflater.inflate(R.layout.qr_scanner_fragment, container, false);
        CodeScannerView scannerView = root.findViewById(R.id.scanner_view);

        mCodeScanner = new CodeScanner(parentActivity, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {

                parentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        scannerDataManager.onScannerDataRead(result.getText());//eseguo le operazione richieste una volta letto un codice qr
                    }
                });
                getActivity().getSupportFragmentManager().beginTransaction().remove(thisFragment).commit();
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
        return root;
    }


    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    public void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    /**
     * Classe che viene instanziata per rendere dinamiche le operazioni che il programma deve fare quando lo scanner dei qr ha letto un qr
     */
    public interface QRscannerDataManager {
        void onScannerDataRead(String scanResult);
    }

}
