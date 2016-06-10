package com.practice.android.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity {

    public static String EXTRA_CRIME_ID = "com.android.practice.crime_uuid_activity";
    public static String EXTRA_CRIME_POSITION = "com.android.practice.crime_position";

    private ViewPager mViewPager;
    private List<Crime> mCrimes;

    private int mPosition;

    public static Intent newIntent(Context packageContext, UUID id, int position) {
        Intent i = new Intent(packageContext, CrimePagerActivity.class);
        i.putExtra(EXTRA_CRIME_ID, id);
        i.putExtra(EXTRA_CRIME_POSITION, position);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        // Get the list of crimes
        mCrimes = CrimeLab.get(this).getCrimeList();
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);

        // Get Intent Extras
        mPosition = getIntent().getIntExtra(EXTRA_CRIME_POSITION, 0);

        // Set the viewpager and its adapter
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager = (ViewPager) findViewById(R.id.crime_view_pager);
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Log.e("tag", "GetItem: " + position);
                // Save the position of last crime edited
                setCrimeResult(mPosition);
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
        mViewPager.setCurrentItem(mPosition);
    }

    private void setCrimeResult(int position) {
        // Send the position of Crime back to CLF to be updated
        Intent i = new Intent();
        i.putExtra(CrimeListFragment.EXTRA_LAST_CRIME_POS, position);
        setResult(CrimePagerActivity.RESULT_OK, i);
    }

}
