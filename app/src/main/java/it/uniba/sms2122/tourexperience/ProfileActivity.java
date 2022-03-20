package it.uniba.sms2122.tourexperience;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String title = getString(R.string.profile);  //TODO inserire nome vero
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);

        setClickListenerOnProfileDataModifyButton();
    }

    /**
     * Funzione che rende editabili i campi per la modifica dei dati utente
     */
    public void setProfileDataFieldEnable() {

        TableLayout tableLayoutProfileActivity = findViewById(R.id.tableLayoutProfileActivity);//selezione il table layout

        for (int i = 0; i < tableLayoutProfileActivity.getChildCount(); i++) {//per ogni riga nel layout

            ViewGroup row = (ViewGroup) tableLayoutProfileActivity.getChildAt(i); //seleziono la singola riga interne alla riga

            for (int j = 0; j < row.getChildCount(); j++) {//per ogni riga all'interno dell tablelayout

                View v = row.getChildAt(j);

                if (v instanceof EditText) {

                    v.setEnabled(true);
                }
            }
        }
    }

    /**
     * Funzione che rende non editabili i campi per la modifica dei dati utente
     */
    public void setProfileDataFieldDisabled() {

        TableLayout tableLayoutProfileActivity = findViewById(R.id.tableLayoutProfileActivity);//selezione il table layout

        for (int i = 0; i < tableLayoutProfileActivity.getChildCount(); i++) {//per ogni riga nel layout

            ViewGroup row = (ViewGroup) tableLayoutProfileActivity.getChildAt(i); //seleziono la singola riga interne alla riga

            for (int j = 0; j < row.getChildCount(); j++) {//per ogni riga all'interno dell tablelayout

                View v = row.getChildAt(j);

                if (v instanceof EditText) {

                    v.setEnabled(false);
                }
            }
        }
    }

    /**
     * funzione per settare il click sul pulsante di modifica profilo
     */
    public void setClickListenerOnProfileDataModifyButton() {

        Button ProfileDataModifyButton = findViewById(R.id.btnModify);

        ProfileDataModifyButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (ProfileDataModifyButton.getText() == getString(R.string.modifyProfile)) {

                    ProfileDataModifyButton.setText(getString(R.string.confirmModifyProfile));
                    setProfileDataFieldEnable();
                }else if (ProfileDataModifyButton.getText() == getString(R.string.confirmModifyProfile)) {

                    ProfileDataModifyButton.setText(getString(R.string.modifyProfile));
                }


            }
        });
    }
}