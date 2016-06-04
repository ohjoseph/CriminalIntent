package com.practice.android.criminalintent;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Joseph on 6/3/16.
 */

public class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;

    public Crime(String title) {
        mId = UUID.randomUUID();
        mTitle = title;
        mDate = new Date();
        mSolved = false;
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }
}
