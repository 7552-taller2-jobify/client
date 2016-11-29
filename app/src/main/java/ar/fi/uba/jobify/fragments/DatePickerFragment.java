package ar.fi.uba.jobify.fragments;



import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Date;

import ar.fi.uba.jobify.utils.DateUtils;
import fi.uba.ar.jobify.R;

/**
 * Created by smpiano on 11/27/16.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private Calendar cal;
    private int id = 0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments()!=null) id = getArguments().getInt("id");
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR)-18; //must be a professional
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        cal = Calendar.getInstance();
        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog datePicker = new DatePickerDialog(getActivity(), this, year, month, day);
        datePicker.getDatePicker().setMaxDate(c.getTime().getTime());
        return datePicker;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        EditText e = (EditText) getActivity().findViewById(id);
        e.setText(DateUtils.formatShortDateArg2(cal.getTime()));
    }

    public Calendar getCal() {
        return cal;
    }
}
