package com.practice.android.criminalintent.activity;

import android.support.v4.app.Fragment;

import com.practice.android.criminalintent.fragment.CrimeListFragment;

public class CrimeListActivity extends SingleFragmentActivity {

    public Fragment createFragment() {
        return new CrimeListFragment();
    }
}
