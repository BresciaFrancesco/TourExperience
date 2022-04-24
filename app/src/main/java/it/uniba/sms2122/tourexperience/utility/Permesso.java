package it.uniba.sms2122.tourexperience.utility;

import android.app.AlertDialog; // è importante che sia android.app.AlertDialog e non androix, perché altrimenti non funziona il codice
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Classe per richiedere e gestire permessi in modo generico
 */
public class Permesso {

    /* Defining Permission codes.
     * We can give any value but unique for each permission. */
    public static final int CAMERA_PERMISSION_CODE = 100;

    // Activity generica nella quale chiedo il permesso
    private AppCompatActivity main;


    // Costruttore
    public Permesso(AppCompatActivity main) {
        this.main = main;
    }

    // Richiedo in modo completo un generico permesso.
    public boolean getPermission(final String permission, final int permissionCode,
                                 final String dialogTitle, final String dialogBody) {
        if (ContextCompat.checkSelfPermission(main, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(main, permission)) {
                // Mostro all'utente una spiegazione in modo "asincrono"
                showRationaleDialog(dialogTitle, dialogBody,
                        (dialogInterface, i) -> requestPermission(permission, permissionCode));
            }
            else {
                // Non serve una spiegazione per l'utente, richiedo permesso direttamente
                requestPermission(permission, permissionCode);
            }
        }
        else {
            Toast.makeText(main, "Permission already granted", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    // Richiede un permesso.
    private void requestPermission(final String permission, final int permissionCode) {
        ActivityCompat.requestPermissions(main,
                new String[] { permission },
                permissionCode);
    }

    /* Mostra un messaggio di spiegazione del permesso e,
     * quando chiuso cliccando su "Ok", richiede il permesso.
     * Se "listener" è null, non fa nulla. */
    public void showRationaleDialog(final String title, final String body,
                                    DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(main).
                setTitle(title).
                setMessage(body)
                .setPositiveButton("Ok", listener)
                .show();
    }
}
