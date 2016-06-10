package com.practice.android.criminalintent;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CrimeListActivity extends SingleFragmentActivity {

    public Fragment createFragment() {
        return new CrimeListFragment();
    }
}
