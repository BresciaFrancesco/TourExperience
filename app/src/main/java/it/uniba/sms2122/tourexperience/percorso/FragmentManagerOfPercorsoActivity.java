package it.uniba.sms2122.tourexperience.percorso;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import it.uniba.sms2122.tourexperience.QRscanner.QRScannerFragment;
import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.graph.exception.GraphException;
import it.uniba.sms2122.tourexperience.percorso.OverviewPath.OverviewPathFragment;
import it.uniba.sms2122.tourexperience.percorso.pagina_opera.OperaFragment;
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
    public void nextPercorsoFragment(Bundle bundle) {
        //TODO instanziare il fragment contenente l'immagine e descrizione del percorso
        try {
            Fragment secondPage = new OverviewPathFragment();
            percorsoActivity.setNomePercorso(bundle.getString("nome_percorso"));
            secondPage.setArguments(bundle);

            percorsoActivity.setValuePath();

            createFragment(secondPage, "overviewFragment");
        } catch (IllegalArgumentException e) {
            Log.e("PercorsoActivity.class", "nextPercorsoFragment -> IllegalArgumentException sollevata.");
            e.printStackTrace();
        }
    }

    /**
     * Funzione che serve a sostituire il precedente fragment con SceltaStanzeFragment
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
    public void nextQRScannerFragmentOfRoomSelection(String idClickedRoom) {

        percorsoActivity.setActionPerfom(() -> {
            percorsoActivity.getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.scannerFrag, new QRScannerFragment(
                            (scanResult) -> {//i dati letti dallo scannere qr verranno gestiti come segue

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
                                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    alert.show();
                                }
                            }
                    ), null)
                    .commit();
        });

        percorsoActivity.checkCameraPermission();
    }

    /**
     * Funzione che si occupa si aprire il fragment per scannerrizare il qr code di un opera,
     * quindi settare cosa fare una volta che il qr è stato letto
     */
    public void nextQRScannerFragmentOfSingleRomm() {

        percorsoActivity.setActionPerfom( () -> {
            percorsoActivity.getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.scannerFrag, new QRScannerFragment(
                            (scanResult) -> {//i dati letti dallo scannere qr verranno gestiti come segue

                                Bundle bundle = new Bundle();
                                bundle.putString("idOpera", scanResult);
                                nextOperaFragment(bundle);
                            }
                    ), null)
                    .commit();
        });

        percorsoActivity.checkCameraPermission();
    }


    /**
     * Funzione che serve a sostituire il precedente fragment con StanzaFragment
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
     * Riuso del codice per creare ed istanziare un fragment
     *
     * @param fragment
     * @param fragmentName
     */
    protected void createFragment(Fragment fragment, String fragmentName) {
        FragmentTransaction transaction = percorsoActivity.getSupportFragmentManager().beginTransaction();
        transaction.setReorderingAllowed(true);
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.container_fragments_route, fragment);
        transaction.addToBackStack(fragmentName);
        transaction.commit();
    }
}
