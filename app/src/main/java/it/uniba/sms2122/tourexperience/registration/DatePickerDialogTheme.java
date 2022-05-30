package it.uniba.sms2122.tourexperience.registration;

import static android.widget.Toast.LENGTH_LONG;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;

import it.uniba.sms2122.tourexperience.R;


public class DatePickerDialogTheme extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private final Calendar calendar = Calendar.getInstance();
    private int year = calendar.get(Calendar.YEAR);
    private int month = calendar.get(Calendar.MONTH);
    private int day = calendar.get(Calendar.DAY_OF_MONTH);

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new DatePickerDialog(getActivity(),
                AlertDialog.THEME_HOLO_LIGHT, this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int yearInput, int monthInput, int dayInput) {
        TextInputEditText date = requireActivity().findViewById(R.id.idEdtRegDateBirth);

        Calendar calendarUser = Calendar.getInstance();
        calendarUser.set(yearInput,monthInput,dayInput);

        Calendar calendarOverTwelve = Calendar.getInstance();
        calendarOverTwelve.set((year-12), month, day);

        if(calendarOverTwelve.compareTo(calendarUser) >= 0){
            monthInput += 1;
            date.setText(dayInput + " / " + monthInput + " / " + yearInput );
        } else{
            Toast.makeText(getActivity(), R.string.unacceptable_date_birth, LENGTH_LONG).show();
            date.setText("");
        }

    }
}
