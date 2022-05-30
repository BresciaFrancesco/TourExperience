package it.uniba.sms2122.tourexperience.percorso;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import it.uniba.sms2122.tourexperience.QRscanner.QRScannerFragment;
import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.graph.exception.GraphException;
import it.uniba.sms2122.tourexperience.model.Stanza;
import it.uniba.sms2122.tourexperience.percorso.OverviewPath.OverviewPathFragment;
import it.uniba.sms2122.tourexperience.percorso.fine_percorso.FinePercorsoFragment;
import it.uniba.sms2122.tourexperience.percorso.pagina_opera.OperaFragment;
import it.uniba.sms2122.tourexperience.percorso.pagina_opera.QuizFragment;
import it.uniba.sms2122.tourexperience.percorso.pagina_stanza.StanzaFragment;
import it.uniba.sms2122.tourexperience.percorso.stanze.SceltaStanzeFragment;
import it.uniba.sms2122.tourexperience.utility.Permesso;

public class FragmentManagerOfPercorsoActivity {

    PercorsoActivity percorsoActivity;

    public FragmentManagerOfPercorsoActivity(PercorsoActivity percorsoActivity) {

        this.percorsoActivity = percorsoActivity;
    }

    /**
     * Funzione che serve a sostituire il precedente fragment con OverviewPathFragment
     *
     * @param bundle, contiene il nome del percorso di cui si deve visualizzare la descrizione
     */
    public void nextPercorsoFragment() {
        //TODO instanziare il fragment contenente l'immagine e descrizione del percorso
        try {
            Fragment secondPage = new OverviewPathFragment();
            createFragment(secondPage, "overviewFragment");
        } catch (IllegalArgumentException e) {
            Log.e("PercorsoActivity.class", "nextPercorsoFragment -> IllegalArgumentException sollevata.");
            e.printStackTrace();
        }
    }

    /**
     * Funzione che serve a sostituire l'attuale fragment con FinePercorsoFragment
     */
    public void nextFinePercorsoFragment() {
        FinePercorsoFragment finePercorsoFragment = new FinePercorsoFragment();
        createFragment(finePercorsoFragment, "finePercorsoFragment");
    }

    /**
     * Funzione che serve a sostituire l'attuale fragment con SceltaStanzeFragment
     */
    public void nextSceltaStanzeFragment() {
        //TODO instanziare il fragment contenente l'immagine e descrizione del percorso
        Fragment thirdPage = new SceltaStanzeFragment();

        createFragment(thirdPage, "sceltaStanzeFragment");
    }

    /**
     * Crea il fragment per visualizzare un'opera.
     *
     * @param bundle contiene la stringa json dell'oggetto opera da istanziare.
     */
    public void nextOperaFragment(Bundle bundle) {
        Fragment operaFragment = new OperaFragment();
        operaFragment.setArguments(bundle);

        createFragment(operaFragment, "operaFragment");
    }


    /**
     * Funzione che si occupa si aprire il fragment per scannerrizare il qr code di una stanza,
     * quindi settare cosa fare una volta che il qr è stato letto
     *
     * @param idClickedRoom, l'id della stanza che è stata clicccata
     */
    public void openQRScannerFragmentOfRoomSelection(String idClickedRoom) {

        QRScannerFragment qrScanner = new QRScannerFragment();
        qrScanner.setScannerDataManager((scanResult) -> {//i dati letti dallo scannere qr verranno gestiti come segue

            if (scanResult.equals(idClickedRoom)) {
                try {

                    if (idClickedRoom.equals(percorsoActivity.getPath().getIdStanzaIniziale())) {
                        //path.setIdStanzaCorrente(idClickedRoom);
                        percorsoActivity.getPath().moveTo(idClickedRoom);//aggiorno il grafo sull'id della stanza in cui si sta entrando
                        nextStanzaFragment();
                    } else {
                        percorsoActivity.getPath().moveTo(idClickedRoom);//aggiorno il grafo sull'id della stanza in cui si sta entrando
                        nextStanzaFragment();
                    }

                } catch (GraphException e) {
                    Log.e("excpetion", e.getMessage());
                }
            } else {

                AlertDialog.Builder alert = new AlertDialog.Builder(percorsoActivity);
                alert.setTitle(percorsoActivity.getString(R.string.error_room_title));
                alert.setMessage(percorsoActivity.getString(R.string.error_room_body));
                alert.setCancelable(true);
                alert.setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss());
                alert.show();
            }
        });

        percorsoActivity.setActionPerfom(() -> percorsoActivity.getSupportFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.scannerFrag, qrScanner, null)
                .commit());

        percorsoActivity.checkCameraPermission();
    }

    /**
     *
     * Funzione che si occupa si aprire il fragment per scannerrizare il QR code di un'opera,
     * poi legge il risultato del QR e se tutto è corretto, ottiene e serializza l'oggetto opera
     * corrispondente e infine lo passa tramite bundle al metodo per creare il fragment dell'opera.
     * @param stanza stanza da cui parte la scannerizzazione del QR code e che dovrebbe contenere
     *               l'oggetto opera che serve.
     * @param nomeMuseo nome del museo.
     */
    public void openQRScannerFragmentOfForOpera(final Stanza stanza, final String nomeMuseo) {

        QRScannerFragment qrScanner = new QRScannerFragment();
        qrScanner.setScannerDataManager((scanResult) -> {
            try {
                String operaJson = new Gson().toJson(stanza.getOperaByID(scanResult));
                Bundle bundle = new Bundle();
                bundle.putString(OperaFragment.OPERA_JSON, operaJson);
                bundle.putString(OperaFragment.NOME_STANZA, stanza.getNome());
                bundle.putString(OperaFragment.NOME_MUSEO, nomeMuseo);
                nextOperaFragment(bundle);
            }
            catch (IllegalArgumentException | NullPointerException | JsonParseException e) {
                exceptionScanQROpera(e);
            }
        });

        percorsoActivity.setActionPerfom( () ->
            percorsoActivity.getSupportFragmentManager().beginTransaction()
            .setReorderingAllowed(true)
            .add(R.id.scannerFrag, qrScanner, null).commit());

        percorsoActivity.checkCameraPermission();
    }

    /**
     * Permette di non ripetere il codice da inserire nel cathc di un'eccezione.
     * Crea un AlertDialog per l'utente in cui spiega il problema avvenuto nello
     * scan del QR per un'opera.
     * @param e eccezione.
     */
    private void exceptionScanQROpera(Exception e) {
        new AlertDialog.Builder(percorsoActivity)
        .setTitle(percorsoActivity.getString(R.string.error_opera_title))
        .setMessage(percorsoActivity.getString(R.string.error_opera_body))
        .setCancelable(true)
        .setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss())
        .show();
        e.printStackTrace();
    }


    /**
     * Funzione che serve a sostituire l'attuale fragment con StanzaFragment
     */
    public void nextStanzaFragment() {
        // Controllo dei permessi
        if (percorsoActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            String[] permissions;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {    // Se la versione dell'sdk è maggiore a 31
                permissions = new String[]{Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.ACCESS_FINE_LOCATION};
            } else {
                permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
            }

            percorsoActivity.getPermission()
                    .getPermission(permissions, Permesso.BLUETOOTH_PERMISSION_CODE,
                            percorsoActivity.getString(R.string.bluetooth_permission_title),
                            percorsoActivity.getString(R.string.bluetooth_permission_body));
        }

        //TODO instanziare il fragment contenente l'immagine e descrizione del percorso
        createFragment(new StanzaFragment(), "stanzaFragment");
    }

    /**
     * Crea il fragment per il quiz di un'opera.
     * @param json stringa json del quiz da passare al fragment.
     * @param nomeOpera nome dell'opera.
     */
    public void nextFragmentQuiz(final String json, final String nomeOpera) {
        createFragment(QuizFragment.newInstance(json, nomeOpera), "quizFragment");
    }

    /**
     * Riuso del codice per creare ed istanziare un fragment
     *
     * @param fragment, nuovo fragment da inserire all'0interno dell'activity al posto del precedente
     * @param fragmentName, tag relativo al nuovo fragment
     */
    protected void createFragment(Fragment fragment, String fragmentName) {
        FragmentTransaction transaction = percorsoActivity.getSupportFragmentManager().beginTransaction();
        transaction.setReorderingAllowed(true);
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.container_fragments_route, fragment, fragmentName);
        transaction.addToBackStack(fragmentName);
        transaction.commit();
    }
}
