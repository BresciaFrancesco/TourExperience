package it.uniba.sms2122.tourexperience.profile;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;

import it.uniba.sms2122.tourexperience.R;

public class ProfileDataPicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    Calendar c;//calendario per la data massima

    static final int MAX_YEAR = 2005;//massimo anno di nascita per chi usa l'app

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //Setto il calendario del datePicker per prendere la data con il giorno e mese attuale ma anno settato al MAX_ANNO
        c = Calendar.getInstance();
        c.set(Calendar.YEAR, MAX_YEAR);//setto l'anno massimo nel calendario
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dp =  new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT, this, year, month, day);

        //setto effetivamente la data massima inseribile nel datePicker
        dp.getDatePicker().setMaxDate(c.getTimeInMillis());

        return dp;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {

        EditText dateBirthInputeditField = getActivity().findViewById(R.id.editFieldBirth);

        dateBirthInputeditField.setText(day + "/" + (month += 1) + "/" + year);

    }
}