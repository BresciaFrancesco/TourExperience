package it.uniba.sms2122.tourexperience.profile;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import it.uniba.sms2122.tourexperience.FirstActivity;
import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.holders.UserHolder;
import it.uniba.sms2122.tourexperience.model.User;

public class ProfileActivity extends AppCompatActivity {
    private UserHolder userHolder;
    private User userIstance;
    Map<String, String> oldDataGui;
    Button profileDataModifyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initUserProfileData();

        setClickListenerOnProfileDataModifyButton();
        setClickListenerOnCalendarIcon();
        setClickListenerOnLogoutButton();
    }


    /**
     * funzione per inizializzare i reali dati utente quindi farli visualizzare sull'activity
     */
    public void initUserProfileData() {
        //prendo l'utente attualmente loggato
        userHolder = UserHolder.getInstance();
        userHolder.getUser(
                (user) -> userIstance = user,
                (String errorMsg) -> {}
        );

        setDynamicUserData();
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

        profileDataPickerBtn.setOnClickListener(view -> {

            DialogFragment datePicker = new ProfileDataPicker();
            datePicker.show(getFragmentManager(), "datePicker");
        });

    }

    /**
     * funzione per triggerare il pulsante per fare il logout
     */
    private void setClickListenerOnLogoutButton() {

        findViewById(R.id.btnLogout).setOnClickListener(view -> {
            userHolder.logout();
            startActivity(new Intent(ProfileActivity.this, FirstActivity.class));
            finish();
        });
    }


    /**
     * funzione per settare il click sul pulsante di modifica profilo
     */
    public void setClickListenerOnProfileDataModifyButton() {

        //disabilito il pulsante per il datePicker poiche da xml l'attributo non viene correttamente letto
        ImageView profileDataPickerBtn = findViewById(R.id.profileDataPickerBtn);
        profileDataPickerBtn.setEnabled(false);

        profileDataModifyButton = findViewById(R.id.btnModify);

        profileDataModifyButton.setOnClickListener(view -> {

            if (profileDataModifyButton.getText() == getString(R.string.modifyProfile)) {//click per modificare

                saveOldProfileDataGui();
                profileDataModifyButton.setText(getString(R.string.confirmModifyProfile));
                setProfileDataFieldEnable();

            } else if (profileDataModifyButton.getText() == getString(R.string.confirmModifyProfile)) {//click per confermare le modifiche

                if (validateChangedData()) {

                    showConfirmPasswordAlertDialog(getString(R.string.insert_password_for_confirm_title), getString(R.string.insert_password_for_confirm_body));
                    profileDataModifyButton.setText(getString(R.string.modifyProfile));
                    setProfileDataFieldDisabled();
                }


            }


        });
    }


    /**
     * funzione che si occupa di leggere i dati attualmente presenti sul profilo cosi de eventualemente ripristinari in seguito e erroir di modifica
     */
    private void saveOldProfileDataGui() {

        oldDataGui = new HashMap<>();

        oldDataGui.put("email", ((EditText) findViewById(R.id.editFieldEmail)).getText().toString());
        oldDataGui.put("name", ((EditText) findViewById(R.id.editFieldName)).getText().toString());
        oldDataGui.put("surname", ((EditText) findViewById(R.id.editFieldSurname)).getText().toString());
        oldDataGui.put("birthDate", ((EditText) findViewById(R.id.editFieldBirth)).getText().toString());
    }


    /**
     * funzione che si occupa re ripristinare i vacchi dati utente sull'activity profilo
     */
    public void restoreOldDataGui() {

        String mail = oldDataGui.get("email");
        ((EditText)findViewById(R.id.editFieldEmail)).setText(mail);

        String name = oldDataGui.get("name");
        ((EditText)findViewById(R.id.editFieldName)).setText(name);

        String surname = oldDataGui.get("surname");
        ((EditText)findViewById(R.id.editFieldSurname)).setText(surname);

        String birthDate = oldDataGui.get("birthDate");
        ((EditText)findViewById(R.id.editFieldBirth)).setText(birthDate);
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
        if (!flag) {
            return false;
        }

        EditText nameEditField = findViewById(R.id.editFieldName);
        flag = ProfileDataChangeValidation.validateGenericText(nameEditField, getString(R.string.name_not_valid));
        if (!flag) {
            return false;
        }

        EditText surnameEditField = findViewById(R.id.editFieldSurname);
        flag = ProfileDataChangeValidation.validateGenericText(surnameEditField, getString(R.string.surname_not_valid));
        if (!flag) {
            return false;
        }

        EditText birthDateEditField = findViewById(R.id.editFieldBirth);
        flag = ProfileDataChangeValidation.validateDate(birthDateEditField, getString(R.string.bithDate_not_valid));
        if (!flag) {
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

        HashMap<String, String> newData = new HashMap<>();

        newData.put("email", ((EditText) findViewById(R.id.editFieldEmail)).getText().toString());
        newData.put("name", ((EditText) findViewById(R.id.editFieldName)).getText().toString());
        newData.put("surname", ((EditText) findViewById(R.id.editFieldSurname)).getText().toString());
        newData.put("birthDate", ((EditText) findViewById(R.id.editFieldBirth)).getText().toString());

        return newData;

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
                    Toast t = Toast.makeText(this, R.string.profile_change_success, Toast.LENGTH_LONG);
                    t.setGravity(Gravity.CENTER, 0, 0);
                    t.show();
                },
                (errorMsg) -> {

                    //messaggio di errore
                    Toast t = Toast.makeText(this, R.string.profile_change_error, Toast.LENGTH_LONG);
                    t.setGravity(Gravity.CENTER, 0, 0);
                    t.show();

                    //ripristino grafico dei vecchi dati
                    restoreOldDataGui();


                }
        );
    }

    /**
     * funzione per mostrare un alertdialog per far inserire la password al fine di confermare i cambiamenti
     *
     * @param title
     * @param bodyText
     */
    public void showConfirmPasswordAlertDialog(String title, String bodyText) {

        final EditText inputPassword = new EditText(this);
        inputPassword.setTransformationMethod(PasswordTransformationMethod.getInstance()); //metodo per nascondere la password inserita
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        inputPassword.setLayoutParams(lp);
        inputPassword.setPadding(15, 0, 5, 20);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(title);
        alert.setMessage(bodyText);
        alert.setView(inputPassword);

        alert.setView(inputPassword);

        alert.setPositiveButton(R.string.insert_password_for_confirm_button, (dialog, whichButton) -> {

            if (!TextUtils.isEmpty(inputPassword.getText())) {
                UserPasswordManager.setPassword(inputPassword.getText().toString());
                updateProfileData(getNewProfileData());
            } else {
                showConfirmPasswordAlertDialog(getString(R.string.password_required), getString(R.string.insert_password_for_confirm_body));

            }
        });


        alert.show();
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

}