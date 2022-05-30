package it.uniba.sms2122.tourexperience.utility;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

/**
 * Classe per richiedere e gestire permessi in modo generico
 */
public class Permesso {

    /* Defining Permission codes.
     * We can give any value but unique for each permission. */
    public static final int CAMERA_PERMISSION_CODE = 100;
    public static final int BLUETOOTH_PERMISSION_CODE = 101;

    // Activity generica nella quale chiedo il permesso
    private AppCompatActivity main;


    // Costruttore
    public Permesso(AppCompatActivity main) {
        this.main = main;
    }

    // Richiedo in modo completo un generico permesso.
    public boolean getPermission(final String permissions[], final int permissionCode,
                                 final String dialogTitle, final String dialogBody) {
        if (!hasPermissions(permissions)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(main, permissions[0])) {
                // Mostro all'utente una spiegazione in modo "asincrono"
                showRationaleDialog(dialogTitle, dialogBody,
                        (dialogInterface, i) -> ActivityCompat.requestPermissions(main, permissions, permissionCode));
            }
            else {
                // Non serve una spiegazione per l'utente, richiedo permesso direttamente
                ActivityCompat.requestPermissions(main, permissions, permissionCode);
            }
        }
        else {
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

    /*
     * Controlla tutti i permessi
     */
    public boolean hasPermissions(String[] permissions) {
        if (permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(main, permission) != PackageManager.PERMISSION_GRANTED)
                    return false;
            }
            return true;
        }
        return false;
    }
}
