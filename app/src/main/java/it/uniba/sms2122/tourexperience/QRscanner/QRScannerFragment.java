package it.uniba.sms2122.tourexperience.QRscanner;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;

import it.uniba.sms2122.tourexperience.R;

public class QRScannerFragment extends Fragment {

    private CodeScanner mCodeScanner;

    QRscannerDataManager scannerDataManager;

    public QRScannerFragment(){}

    /**
     * Funzione per settare l'oggetto che gestisce i dati letti
     * @param scannerDataManager un oggetto da instanziare tramite lambda expression per decidere che fare con i dati letto dallo scanner dei qr
     */
    public void setScannerDataManager(QRscannerDataManager scannerDataManager) {
        this.scannerDataManager = scannerDataManager;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(scannerDataManager == null){//è avvenuto un cambio di configurazione

            //chiudo lo scanner perche ho bisogno di farlo riaprire per caricare l'ggetto che gestisce la lettura del qr
            requireActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        }

        Fragment thisFragment = this;
        final Activity parentActivity = requireActivity();
        View root = inflater.inflate(R.layout.qr_scanner_fragment, container, false);
        CodeScannerView scannerView = root.findViewById(R.id.scanner_view);

        triggerCloseScannerButton(root);

        assert parentActivity != null;
        mCodeScanner = new CodeScanner(parentActivity, scannerView);
        mCodeScanner.setDecodeCallback(result -> {

            parentActivity.runOnUiThread(() -> {
                scannerDataManager.onScannerDataRead(result.getText());//eseguo le operazione richieste una volta letto un codice qr
            });
            requireActivity().getSupportFragmentManager().beginTransaction().remove(thisFragment).commit();
        });
        scannerView.setOnClickListener(view -> mCodeScanner.startPreview());
        return root;
    }

    private void triggerCloseScannerButton(View root) {

        Fragment thisFragment = this;
        Button closeScanneButton = root.findViewById(R.id.closeScannerButton);

        closeScanneButton.setOnClickListener(view -> thisFragment.requireActivity().getSupportFragmentManager().beginTransaction().remove(thisFragment).commit());
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
