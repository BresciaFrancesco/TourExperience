package it.uniba.sms2122.tourexperience.registration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import it.uniba.sms2122.tourexperience.R;

public class RegistrationFragmentFirstPage extends Fragment {

    private TextInputEditText email;
    private TextInputEditText psw;
    private TextInputEditText rPsw;
    private Button btnNextPage;
    private RegistrationActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration_first_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        email = view.findViewById(R.id.idEdtRegEmail);
        psw = view.findViewById(R.id.idEdtRegPassword);
        rPsw = view.findViewById(R.id.idEdtRegPasswordTwo);
        btnNextPage = view.findViewById(R.id.idBtnRegFirstPage);
        mainActivity = (RegistrationActivity) requireActivity();

        btnNextPage.setOnClickListener(this::nextFragment);
    }

    /**
     * Funzione che invoca la funzione nextFragment al fine di passare al secondo fragment della registrazione
     * @param view
     */
    private void nextFragment(View view) {
        String txtEmail = Objects.requireNonNull(email.getText()).toString();
        String txtPsw = Objects.requireNonNull(psw.getText()).toString();

        //Controllo su quello che è stato digitato dall'utente per email e password
        CheckCredentials checker = mainActivity.getChecker();
        if (!checker.checkEmail(this.email, mainActivity)) return;
        if (!checker.checkPassword(psw, rPsw, mainActivity)) return;

        Bundle bundle = new Bundle();
        bundle.putString("email", txtEmail);
        bundle.putString("password", txtPsw);
        mainActivity.nextFragment(bundle);
    }
}