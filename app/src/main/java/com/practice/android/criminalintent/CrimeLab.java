package com.practice.android.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.text.format.DateFormat;

import com.practice.android.criminalintent.database.CrimeBaseHelper;
import com.practice.android.criminalintent.database.CrimeDbSchema;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.practice.android.criminalintent.database.CrimeDbSchema.*;

/**
 * Created by Joseph on 6/6/16.
 */

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    // Constructor
    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext)
                .getWritableDatabase();
    }

    /************************** Static Methods ************************/

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }

        return sCrimeLab;
    }

    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved());
        values.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());

        return values;
    }

    public static String getDateFormat(Context context, Date date) {
        return DateFormat.getMediumDateFormat(context).format(date);
    }

    public static String getTimeFormat(Context context, Date date) {
        return DateFormat.getTimeFormat(context).format(date);
    }

    /***************************** Public Methods *****************************/

    public void addCrime(Crime c) {
        // Convert Crime to ContentValue
        ContentValues values = getContentValues(c);

        // Store ContentValue in database
        mDatabase.insert(CrimeTable.NAME, null,values);
    }

    public void updateCrime(Crime crime) {
        // Convert crime to CV
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);

        // Update the specific Crime in the database
        mDatabase.update(
                CrimeTable.NAME,
                values,
                CrimeTable.Cols.UUID + " = ?",
                new String[] {uuidString});
    }


    public void deleteCrime(Crime c) {
        // Delete the crime from database
        mDatabase.delete(
                CrimeTable.NAME,
                CrimeTable.Cols.UUID + " = ?",
                new String[] {c.getId().toString()}
        );
    }

    public Crime getCrime(UUID uuid) {
        // Return a cursor to the row of the Crime
        CrimeCursorWrapper cursor = queryCrimes(
                CrimeTable.Cols.UUID + " = ?",
                new String[] {uuid.toString()}
        );

        // Check if any rows were returned
        try {
            if (cursor.getCount() == 0) {
                return null;
            } else {
                cursor.moveToFirst();
                return cursor.getCrime();
            }
        } finally {
            cursor.close();
        }
    }

    public List<Crime> getCrimeList() {
        List<Crime> crimes = new ArrayList<>();

        // Iterate cursor over database and initialize each saved Crime
        CrimeCursorWrapper cursor = queryCrimes(null, null);
        try {
            cursor.moveToFirst();
            // Add crime to list
            while (!cursor.isAfterLast()) {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } finally {
            // Closes the cursor to prevent overflow
            cursor.close();
        }

        return crimes;
    }

    public File getPhotoFile(Crime crime) {
        File externalFileDir = mContext
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (externalFileDir == null) {
            return null;
        }

        return new File(externalFileDir, crime.getPhotoFilename());
    }

    /*********************** Private Methods **********************/

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        // Return a Cursor to the database
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME, // Table
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null); // OrderBy

        return new CrimeCursorWrapper(cursor);
    }
}
