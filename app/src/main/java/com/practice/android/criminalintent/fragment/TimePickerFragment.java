package com.practice.android.criminalintent.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import com.practice.android.criminalintent.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Joseph on 6/8/16.
 */

public class TimePickerFragment extends DialogFragment {

    public static final String ARG_TIME = "com.android.practice.arg_time";
    public static final String EXTRA_CRIME_TIME = "extra_crime_time";

    private TimePicker mTimePicker;
    private Calendar mCalendar;
    private Date mDate;

    public static TimePickerFragment newInstance(Date date) {
        // Create arguments
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, date);
        // Set fragment arguments
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get date
        mDate = (Date) getArguments().getSerializable(ARG_TIME);
        // Set timePicker's time
        mCalendar = new GregorianCalendar();
        mCalendar.setTime(mDate);
        int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = mCalendar.get(Calendar.MINUTE);
        // Inflate timePicker view
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);
        mTimePicker = (TimePicker) v.findViewById(R.id.dialog_time_picker);
        mTimePicker.setCurrentHour(hour);
        mTimePicker.setCurrentMinute(minute);
        // Return dialog and set onClicklistener
        return new android.support.v7.app.AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle("Time of the Crime")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int hour = mTimePicker.getCurrentHour();
                        int minute = mTimePicker.getCurrentMinute();
                        mCalendar.set(Calendar.HOUR, hour);
                        mCalendar.set(Calendar.MINUTE, minute);
                        setTimeResult(Activity.RESULT_OK, mCalendar.getTime());
                    }
                })
                .create();
    }

    private void setTimeResult(int resultCode, Date time) {
        Intent i = new Intent();
        i.putExtra(EXTRA_CRIME_TIME, time);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, i);
    }
}
