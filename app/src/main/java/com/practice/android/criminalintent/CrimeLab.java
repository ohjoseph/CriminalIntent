package com.practice.android.criminalintent;

import android.content.Context;
import android.text.format.DateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Joseph on 6/6/16.
 */

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private List<Crime> mCrimeList;

    private CrimeLab(Context context) {
        mCrimeList = new ArrayList<>(100);
    }

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }

        return sCrimeLab;
    }

    public static String getDateFormat(Context context, Date date) {
        return DateFormat.getMediumDateFormat(context).format(date);
    }

    public static String getTimeFormat(Context context, Date date) {
        return DateFormat.getTimeFormat(context).format(date);
    }

    public void addCrime(Crime c) {
        mCrimeList.add(c);
    }

    public void deleteCrime(Crime c) {
        for (int i = 0; i < mCrimeList.size(); i++) {
            if (c.getId().equals(mCrimeList.get(i).getId())) {
                mCrimeList.remove(i);
                return;
            }
        }
    }

    public Crime getCrime(UUID uuid) {
        for (Crime c: mCrimeList) {
            if (c.getId().equals(uuid)) {
                return c;
            }
        }
        return null;
    }

    public List<Crime> getCrimeList() {
        return mCrimeList;
    }

    public void setCrimeList(List<Crime> crimeList) {
        mCrimeList = crimeList;
    }
}
