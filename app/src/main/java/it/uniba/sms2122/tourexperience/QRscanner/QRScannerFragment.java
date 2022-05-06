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
import it.uniba.sms2122.tourexperience.percorso.pagina_opera.OperaActivity;
import it.uniba.sms2122.tourexperience.percorso.pagina_stanza.StanzaFragment;

public class QRScannerFragment extends Fragment {

    private CodeScanner mCodeScanner;
    private Class fragmentClassToOpen;

    public QRScannerFragment(Class<StanzaFragment> fragmentClassToOpen){
        this.fragmentClassToOpen = fragmentClassToOpen;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final Activity activity = getActivity();
        View root = inflater.inflate(R.layout.qr_scanner_fragment, container, false);
        CodeScannerView scannerView = root.findViewById(R.id.scanner_view);

        mCodeScanner = new CodeScanner(activity, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {

                try {


                    Log.e("porcodio",((PercorsoActivity)getActivity()).getPath().moveTo(result.getText()).getNome());
                    /*openNextFragment();*/
                } catch (GraphException e) {
                    Log.e("porcamadonna",e.getMessage());
                }

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

    public void openNextFragment(){

        if(fragmentClassToOpen == StanzaFragment.class)

            ((PercorsoActivity)getActivity()).nextStanzaFragment();

        else

            Log.e("to implemetn", "toimplement");

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

}
