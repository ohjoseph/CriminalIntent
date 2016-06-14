package com.practice.android.criminalintent.data;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

import static com.practice.android.criminalintent.database.CrimeDbSchema.*;

/**
 * Created by Joseph on 6/10/16.
 */

public class CrimeCursorWrapper extends CursorWrapper {
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        // Retrieve information from cursor
        String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        String suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        Long suspect_id = getLong(getColumnIndex(CrimeTable.Cols.SUSPECT_ID));

        // Initalize new Crime from saved info
        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setSolved(isSolved != 0);
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSuspect(suspect);
        crime.setSuspectId(suspect_id);
        return crime;
    }
}
