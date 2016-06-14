package com.practice.android.criminalintent.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.practice.android.criminalintent.data.Crime;
import com.practice.android.criminalintent.data.CrimeLab;
import com.practice.android.criminalintent.R;
import com.practice.android.criminalintent.fragment.CrimeFragment;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity
        implements CrimeFragment.Callbacks {

    public static String EXTRA_CRIME_ID = "com.android.practice.crime_uuid_activity";

    private ViewPager mViewPager;
    private List<Crime> mCrimes;

    public static Intent newIntent(Context packageContext, UUID id) {
        Intent i = new Intent(packageContext, CrimePagerActivity.class);
        i.putExtra(EXTRA_CRIME_ID, id);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        // Get the list of crimes
        mCrimes = CrimeLab.get(this).getCrimeList();
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);

        // Set the viewpager and its adapter
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager = (ViewPager) findViewById(R.id.crime_view_pager);
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                // Get the associated Crime
                UUID id = mCrimes.get(position).getId();
                return CrimeFragment.newInstance(id);
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        // Show the specific Crime
        for (int i = 0; i < mCrimes.size(); i++) {
            if (crimeId.equals(mCrimes.get(i).getId())) {
                mViewPager.setCurrentItem(i);
            }
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        // Do nothing
    }
}