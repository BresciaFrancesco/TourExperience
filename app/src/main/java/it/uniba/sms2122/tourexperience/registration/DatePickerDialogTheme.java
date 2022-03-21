package it.uniba.sms2122.tourexperience.registration;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.widget.DatePicker;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.textfield.TextInputEditText;

import it.uniba.sms2122.tourexperience.R;


public class DatePickerDialogTheme extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private TextInputEditText date;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(),
                AlertDialog.THEME_HOLO_LIGHT, this, year, month, day);

        return datepickerdialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        date = getActivity().findViewById(R.id.idEdtRegDateBirth);
        date.setText(day + " / " + month + " / " + year );
    }
}
