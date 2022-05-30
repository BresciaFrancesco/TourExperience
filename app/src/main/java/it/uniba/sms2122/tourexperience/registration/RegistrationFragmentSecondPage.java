package it.uniba.sms2122.tourexperience.registration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import it.uniba.sms2122.tourexperience.R;

public class RegistrationFragmentSecondPage extends Fragment {

    private TextInputEditText name;
    private TextInputEditText surname;
    private TextInputEditText date;
    private ImageView imgDate;
    private Button btnEnd;
    private ProgressBar progressBar;
    private RegistrationActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration_second_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        name = view.findViewById(R.id.idEdtRegName);
        surname = view.findViewById(R.id.idEdtRegSurname);
        date = view.findViewById(R.id.idEdtRegDateBirth);
        imgDate = view.findViewById(R.id.idImgSetDate);
        progressBar = view.findViewById(R.id.idProgressBarReg);
        btnEnd = view.findViewById(R.id.idBtnRegSecondPage);
        mainActivity = (RegistrationActivity) requireActivity();

        imgDate.setOnClickListener(view1 -> {
            DialogFragment dialogFragment = new DatePickerDialogTheme();
            dialogFragment.show(getChildFragmentManager(), "MyTheme");
        });

        btnEnd.setOnClickListener(this::registration);
    }

    /**
     * Funzione che invoca la funzione registration al fine di effettuare la registazione del nuovo utente su firebase
     * @param view
     */
    private void registration(View view) {
        String txtName = Objects.requireNonNull(name.getText()).toString();
        String txtSurname = Objects.requireNonNull(surname.getText()).toString();
        String txtDate = Objects.requireNonNull(date.getText()).toString();

        //Controllo su quello che è stato digitato dall'utente per nome, cognome e data di nascita
        CheckCredentials checker = mainActivity.getChecker();
        if (!checker.checkGenericStringGeneral("name", name,30,txtName,mainActivity)) return;

        if (!checker.checkGenericStringGeneral("surname", surname,30,txtSurname,mainActivity)) return;

        if (!checker.checkGenericStringGeneral("date", date,30,txtDate,mainActivity)) return;

        Bundle bundle = getArguments();
        assert bundle != null;
        bundle.putString("name", txtName);
        bundle.putString("surname", txtSurname);
        bundle.putString("dateBirth", txtDate);
        progressBar.setVisibility(View.VISIBLE);
        mainActivity.registration(bundle);

    }
}