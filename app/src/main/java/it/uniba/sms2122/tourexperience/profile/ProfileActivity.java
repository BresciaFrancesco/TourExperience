package it.uniba.sms2122.tourexperience.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.Toast;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import it.uniba.sms2122.tourexperience.MainActivity;
import it.uniba.sms2122.tourexperience.R;

import it.uniba.sms2122.tourexperience.holders.UserHolder;
import it.uniba.sms2122.tourexperience.model.User;

public class ProfileActivity extends AppCompatActivity {
    private UserHolder userHolder;
    private User userIstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        //prendo l'utente attualmente loggato
        userHolder = UserHolder.getInstance();
        userHolder.getUser(
                (user) -> {
                    userIstance = user;
                },
                () -> {});


        saveUserPassword();

        setDynamicUserData();
        setClickListenerOnProfileDataModifyButton();
        setClickListenerOnCalendarIcon();
        setClickListenerOnLogoutButton();
    }

    /**
     * Funzione che setta i valori dinamici reali dell'utente da far visualizzare sull'activity profilo
     */
    public void setDynamicUserData() {

        ((EditText) findViewById(R.id.editFieldEmail)).setText(userIstance.getEmail());
        ((EditText) findViewById(R.id.editFieldName)).setText(userIstance.getName());
        ((EditText) findViewById(R.id.editFieldSurname)).setText(userIstance.getSurname());
        ((EditText) findViewById(R.id.editFieldBirth)).setText(userIstance.getDateBirth());

    }


    /**
     * funzione per triggerare il pulsante per far apparire il dataPicker
     */
    public void setClickListenerOnCalendarIcon() {

        ImageView profileDataPickerBtn = findViewById(R.id.profileDataPickerBtn);

        profileDataPickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogFragment datePicker = new ProfileDataPicker();
                datePicker.show(getFragmentManager(), "datePicker");
            }
        });

    }

    /**
     * funzione per triggerare il pulsante per fare il logout
     */
    private void setClickListenerOnLogoutButton() {

        findViewById(R.id.btnLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userHolder.logout();
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                finish();
            }
        });
    }


    /**
     * funzione per settare il click sul pulsante di modifica profilo
     */
    public void setClickListenerOnProfileDataModifyButton() {

        //disabilito il pulsante per il datePicker poiche da xml l'attributo non viene correttamente letto
        ImageView profileDataPickerBtn = findViewById(R.id.profileDataPickerBtn);
        profileDataPickerBtn.setEnabled(false);

        Button ProfileDataModifyButton = findViewById(R.id.btnModify);

        ProfileDataModifyButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (ProfileDataModifyButton.getText() == getString(R.string.modifyProfile)) {

                    ProfileDataModifyButton.setText(getString(R.string.confirmModifyProfile));
                    setProfileDataFieldEnable();

                } else if (ProfileDataModifyButton.getText() == getString(R.string.confirmModifyProfile)) {

                    if (validateChangedData()) {
                        ProfileDataModifyButton.setText(getString(R.string.modifyProfile));
                        updateProfileData(getNewProfileData());
                        setProfileDataFieldDisabled();
                    }


                }


            }
        });
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

        //abilito l'input per la data di nascita
        ImageView profileDataPickerBtn = findViewById(R.id.profileDataPickerBtn);
        profileDataPickerBtn.setEnabled(true);
    }

    /**
     * funzione che si occupa di validare le modifiche effettuate dall'utente
     *
     * @return true se la validazione di tutti i campi è andata a buon fine, false altrimenti
     */
    public boolean validateChangedData() {

        boolean flag;

        EditText emailEditField = findViewById(R.id.editFieldEmail);
        flag = ProfileDataChangeValidation.validateEmail(emailEditField, getString(R.string.email_not_valid));
        if (flag == false) {
            return false;
        }

        EditText nameEditField = findViewById(R.id.editFieldName);
        flag = ProfileDataChangeValidation.validateGenericText(nameEditField, getString(R.string.name_not_valid));
        if (flag == false) {
            return false;
        }

        EditText surnameEditField = findViewById(R.id.editFieldSurname);
        flag = ProfileDataChangeValidation.validateGenericText(surnameEditField, getString(R.string.surname_not_valid));
        if (flag == false) {
            return false;
        }

        EditText birthDateEditField = findViewById(R.id.editFieldBirth);
        flag = ProfileDataChangeValidation.validateDate(birthDateEditField, getString(R.string.bithDate_not_valid));
        if (flag == false) {
            return false;
        }

        return true;
    }

    /**
     * funzione che si occupa di leggere i dati che sono stati modificati dall'utente
     *
     * @return i nuovi dati modificati dall'utent
     */
    private HashMap<String, String> getNewProfileData() {

        Map<String, String> newData = new HashMap<String, String>();

        newData.put("email", ((EditText) findViewById(R.id.editFieldEmail)).getText().toString());
        newData.put("name", ((EditText) findViewById(R.id.editFieldName)).getText().toString());
        newData.put("surname", ((EditText) findViewById(R.id.editFieldSurname)).getText().toString());
        newData.put("birthDate", ((EditText) findViewById(R.id.editFieldBirth)).getText().toString());

        return (HashMap<String, String>) newData;

    }

    /**
     * funzione che si occupa di aggiornare sul db i dati madoficati dall'utente
     *
     * @param newProfileData
     */
    private void updateProfileData(Map<String, String> newProfileData) {

        userIstance.setEmail(newProfileData.get("email"));
        userIstance.setName(newProfileData.get("name"));
        userIstance.setSurname(newProfileData.get("surname"));
        userIstance.setDateBirth(newProfileData.get("birthDate"));

        //aggiorno il db
        userHolder.updateIfDirty(
                () -> {
                    Toast.makeText(this, R.string.profile_change_success, Toast.LENGTH_LONG);
                },
                (errorMsg) -> {


                    Toast.makeText(this, R.string.profile_change_error, Toast.LENGTH_LONG);

                }
        );
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

        //disabilito anche il pulsante per il datePicker
        ImageView profileDataPickerBtn = findViewById(R.id.profileDataPickerBtn);
        profileDataPickerBtn.setEnabled(false);
    }

    void saveUserPassword(){

        this.userIstance.setPassword(SaveUserPasswordMiddleClass.getSavedUserPassword());
    }
}