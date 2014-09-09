package com.stereo23.slideshow.utilities;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Username on 05.09.2014.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {
    private int hour = -1;
    private int minute = -1;
    private int pickerType;
    // 0 - startTimePicker; 1 - stopTimePicker.
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle  = this.getArguments();
        if (bundle != null){
            pickerType = bundle.getInt("pickerType");
        }
        if (pickerType == 0){
            hour = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("start_hour", -1);
            minute = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("start_minute", -1);
        }else{
            hour = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("stop_hour", -1);
            minute = PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("stop_minute", -1);
        }
        if (hour==-1||minute==-1){
            final Calendar c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
        }

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (pickerType == 0){
            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putInt("start_hour", hourOfDay).commit();
            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putInt("start_minute", minute).commit();

        } else {
            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putInt("stop_hour", hourOfDay).commit();
            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putInt("stop_minute", minute).commit();
        }
    }
}
