package com.practice.android.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Joseph on 6/3/16.
 */

public class CrimeFragment extends Fragment {
    private Crime mCrime;

    // Tags for extras and arguments
    private static String ARG_CRIME_ID = "com.android.practice.arg_crime_id";
    private static String TAG_DATE_PICKER_DIALOG = "Dialog Date";
    private static String TAG_TIME_PICKER_DIALOG = "Dialog Time";

    // Request codes
    private static int REQUEST_DATE_PICKER = 10;
    private static int REQUEST_TIME_PICKER = 5;

    // Inflated views
    private EditText mCrimeTitle;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mTimeButton;
    private Button mDeleteButton;
    private Button mChooseSuspectButton;
    private Button mSendCrimeReportButton;

    // Other variables
    private int mPosition;

    /********************* Static Methods ******************/

    public static CrimeFragment newInstance(UUID uuid) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, uuid);

        CrimeFragment cf = new CrimeFragment();
        cf.setArguments(args);
        return cf;
    }

    /********************* Override Methods **********************/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the crime with the id
        UUID id = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        if (id != null)
            mCrime = CrimeLab.get(getActivity()).getCrime(id);
        else
            mCrime = new Crime("Default Crime");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mCrimeTitle = (EditText) v.findViewById(R.id.crime_title);
        mCrimeTitle.setText(mCrime.getTitle());
        mCrimeTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Empty
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mCrime.setTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Empty
            }
        });
        mDateButton = (Button) v.findViewById(R.id.date_button);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                DatePickerFragment dpf = DatePickerFragment.newInstance(mCrime.getDate());
                // Set the target fragment to receive result
                dpf.setTargetFragment(CrimeFragment.this, REQUEST_DATE_PICKER);
                dpf.show(fm, TAG_DATE_PICKER_DIALOG);
            }
        });

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.solved_checkbox);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mCrime.setSolved(b);
            }
        });

        mTimeButton = (Button) v.findViewById(R.id.time_picker_button);
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                TimePickerFragment timePicker = TimePickerFragment.newInstance(mCrime.getDate());
                timePicker.setTargetFragment(CrimeFragment.this, REQUEST_TIME_PICKER);
                timePicker.show(fm, TAG_TIME_PICKER_DIALOG);
            }
        });

        mDeleteButton = (Button) v.findViewById(R.id.crime_delete_button);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Delete crime and close activity
                CrimeLab.get(getActivity()).deleteCrime(mCrime);
                getActivity().finish();
            }
        });

        mChooseSuspectButton = (Button) v.findViewById(R.id.choose_suspect_button);
        mChooseSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mSendCrimeReportButton = (Button) v.findViewById(R.id.send_crime_report_button);
        mSendCrimeReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start an activity that can send text
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.crime_report_subject));
                // Always lets the user choose application
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });

        // Sets the time
        updateDate();

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_DATE_PICKER) {
            if (data != null) {
                Date date = (Date) data
                        .getSerializableExtra(DatePickerFragment.EXTRA_CRIME_DATE);
                mCrime.setDate(date);
                updateDate();
            }
        } else if (requestCode == REQUEST_TIME_PICKER) {
            if (data != null) {
                Date date = (Date) data
                        .getSerializableExtra(TimePickerFragment.EXTRA_CRIME_TIME);
                mCrime.setDate(date);
                updateDate();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // Update the crime in the database
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    /**************************** Private Methods **************************/

    private void updateDate() {
        // Update month/day/year
        String dateString = CrimeLab.getDateFormat(getActivity(), mCrime.getDate());
        mDateButton.setText(dateString);
        // Update time
        String time = CrimeLab.getTimeFormat(getActivity(), mCrime.getDate());
        mTimeButton.setText(time);
    }

    // Generates Crime Report
    private String getCrimeReport() {
        String solvedString = "";
        if(mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = getString(R.string.crime_report_suspect, mCrime.getSuspect());
        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);
        return report;
    }
}
