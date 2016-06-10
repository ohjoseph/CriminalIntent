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

    public Crime() {
        mId = UUID.randomUUID();
        mTitle = "New Crime";
        mDate = new Date();
        mSolved = false;
    }

    public Crime(String title) {
        this();
        mTitle = title;
    }

    public Crime(UUID id) {
        this();
        mId = id;
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
