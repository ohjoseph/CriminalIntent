package com.practice.android.criminalintent.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.practice.android.criminalintent.R;
import com.practice.android.criminalintent.data.Crime;
import com.practice.android.criminalintent.fragment.CrimeFragment;
import com.practice.android.criminalintent.fragment.CrimeListFragment;

public class CrimeListActivity extends SingleFragmentActivity
        implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks{

    public Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            // Start ViewPager
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        } else {
            // Update the master-detail view
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        CrimeListFragment listFragment = (CrimeListFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }
}
