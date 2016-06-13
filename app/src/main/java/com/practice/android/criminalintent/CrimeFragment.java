package com.practice.android.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
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
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Joseph on 6/3/16.
 */

public class CrimeFragment extends Fragment {
    // Tags for extras and arguments
    private static String ARG_CRIME_ID = "com.android.practice.arg_crime_id";
    private static String TAG_DATE_PICKER_DIALOG = "Dialog Date";
    private static String TAG_TIME_PICKER_DIALOG = "Dialog Time";

    // Request codes
    private static final int REQUEST_DATE_PICKER = 0;
    private static final int REQUEST_TIME_PICKER = 1;
    private static final int REQUEST_CONTACT = 2;
    private static final int REQUEST_PHOTO = 3;

    // Inflated views
    private EditText mCrimeTitle;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mTimeButton;
    private Button mDeleteButton;
    private Button mChooseSuspectButton;
    private Button mCallSuspectButton;
    private Button mSendCrimeReportButton;
    private ImageButton mCrimePhotoButton;

    // Other variables
    private Crime mCrime;
    private File mPhotoFile;

    /*********************
     * Static Methods
     ******************/

    public static CrimeFragment newInstance(UUID uuid) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, uuid);

        CrimeFragment cf = new CrimeFragment();
        cf.setArguments(args);
        return cf;
    }

    /*********************
     * Override Methods
     **********************/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the crime with the id
        UUID id = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        if (id != null)
            mCrime = CrimeLab.get(getActivity()).getCrime(id);
        else
            mCrime = new Crime("Default Crime");

        // Get the file location for photos
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mCrimeTitle = (EditText) v.findViewById(R.id.crime_title_editText);
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

        final Intent pickContact =
                new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        // Check to make sure Contacts app exists and is accessible
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mChooseSuspectButton.setEnabled(false);
        }

        mChooseSuspectButton = (Button) v.findViewById(R.id.choose_suspect_button);
        mChooseSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open the Contacts app and choose suspect
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        // Set the suspect button with the name of the suspect
        if (!mCrime.getSuspect().equals("unknown")) {
            mChooseSuspectButton.setText(mCrime.getSuspect());
        } else {
            mChooseSuspectButton.setText("Choose Suspect");
        }

        mSendCrimeReportButton = (Button) v.findViewById(R.id.send_crime_report_button);
        mSendCrimeReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start an activity that can send text
                Intent i = ShareCompat.IntentBuilder.from(getActivity())
                        .setText(getCrimeReport())
                        .setSubject(getString(R.string.crime_report_subject))
                        .setType("text/plain")
                        .setChooserTitle(R.string.send_report)
                        .createChooserIntent();
                startActivity(i);
            }
        });

        mCallSuspectButton = (Button) v.findViewById(R.id.call_suspect_button);
        mCallSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Calls the suspect if there is one
                if (!mCrime.getSuspect().equals("unknown")) {
                    // Get the phone number of the contact
                    Uri callUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                    String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
                    String selection =
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
                    String[] selectionArgs = {Long.toString(mCrime.getSuspectId())};
                    // Query the Contacts database
                    Cursor c = getActivity().getContentResolver().query(
                            callUri, projection, selection, selectionArgs, null);
                    if (c.getCount() != 0) {
                        // Dial the number
                        try {
                            c.moveToFirst();
                            String suspectNumber = c.getString(0);
                            Intent i = new Intent(Intent.ACTION_DIAL,
                                    Uri.parse("tel:" + suspectNumber));
                            startActivity(i);
                        } finally {
                            c.close();
                        }
                    }

                } else {
                    Toast.makeText(getActivity(), "No Suspect found.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Checks if phone can take pictures
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = Uri.fromFile(mPhotoFile);
        captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        boolean canTakePhoto = (mPhotoFile != null) &&
                (captureImage.resolveActivity(packageManager) != null);

        mCrimePhotoButton = (ImageButton) v.findViewById(R.id.crime_photo_imageView);
        mCrimePhotoButton.setEnabled(canTakePhoto);
        mCrimePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Takes a picture
                startActivityForResult(captureImage, REQUEST_PHOTO);
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
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            // Specify which fields you want your query to get values from
            String[] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts._ID
            };

            // Perform query - contactUri is the "where" clause
            Cursor c = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);

            // Double check for results
            if (c.getCount() == 0) {
                return;
            }

            try {
                // Get suspect name from 1st column
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                mChooseSuspectButton.setText(suspect);

                // Get suspect contact id from 2nd column
                Long contactId = c.getLong(1);
                mCrime.setSuspectId(contactId);
            } finally {
                c.close();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // Update the crime in the database
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    /****************************
     * Private Methods
     **************************/

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
        if (mCrime.isSolved()) {
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
