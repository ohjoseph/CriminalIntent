package com.practice.android.criminalintent.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.practice.android.criminalintent.data.Crime;
import com.practice.android.criminalintent.data.CrimeLab;
import com.practice.android.criminalintent.activity.CrimePagerActivity;
import com.practice.android.criminalintent.R;

import java.util.List;

/**
 * Created by Joseph on 6/6/16.
 */

public class CrimeListFragment extends Fragment {

    private static final int REQUEST_CODE_CRIME_PAGER_ACTIVITY = 100;

    // Position of the last clicked Crime
    private int mLastPos = 0;
    private boolean mSubtitleVisible;

    // Inflated Views
    private RecyclerView mRecyclerView;
    private LinearLayout mEmptyView;
    private CrimeAdapter mAdapter;
    private Button mEmptyAddButton;

    // Other variables
    private Callbacks mCallbacks;

    /********************
     * Callbacks Interface
     ********************/

    public interface Callbacks {
        void onCrimeSelected(Crime crime);
    }

    /********************
     * Override Methods
     ********************/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create menu
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime_list, container, false);

        // Find the RecyclerView and set manager
        mRecyclerView = (RecyclerView) v.findViewById(R.id.crime_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Find the empty view
        mEmptyView = (LinearLayout) v.findViewById(R.id.crime_list_empty);
        mEmptyAddButton = (Button) v.findViewById(R.id.add_crime_empty);
        mEmptyAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewCrime();
            }
        });

        // Update the list of Crimes
        updateUI();

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle("Hide Subtitle");
        } else {
            subtitleItem.setTitle("Show Subtitle");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_crime:
                // Make new Crime
                Crime c = addNewCrime();
                updateUI();
                mCallbacks.onCrimeSelected(c);
                return true;
            case R.id.menu_item_show_subtitle:
                // Recreate toolbar to update subtitle button
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload the list
        updateUI();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    /******************
     * RecyclerView Private Classes
     *****************/

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimeList) {
            mCrimes = crimeList;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.list_item_crime, parent, false);
            return new CrimeHolder(view);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            holder.bindCrimeHolder(mCrimes.get(position), position);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        public void setCrimes(List<Crime> crimes) {
            mCrimes.clear();
            mCrimes.addAll(crimes);
            notifyDataSetChanged();
        }
    }

    private class CrimeHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private Crime mCrime;
        private int mPosition;
        private TextView mTitleTextView;
        private CheckBox mCheckBox;
        private TextView mDateTextView;

        public CrimeHolder(View itemView) {
            // Initialize CrimeHolder
            super(itemView);
            itemView.setOnClickListener(this);

            // Finds the views instantiated in each ViewHolder
            mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title_tv);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.crime_checked_box);
            mDateTextView = (TextView) itemView.findViewById(R.id.crime_date_tv);
        }

        // Binds the specific views to a specific Crime
        public void bindCrimeHolder(Crime c, int position) {
            mCrime = c;
            mPosition = position;
            mTitleTextView.setText(c.getTitle());
            mCheckBox.setChecked(c.isSolved());
            mDateTextView.setText(CrimeLab.getDateFormat(getActivity(), c.getDate()));

            mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    // Save value and update database
                    mCrime.setSolved(b);
                    CrimeLab.get(getActivity()).updateCrime(mCrime);
                }
            });
        }

        // Starts the CrimePagerActivity when clicked
        @Override
        public void onClick(View v) {
            mCallbacks.onCrimeSelected(mCrime);

            // Save last position clicked
            mLastPos = mPosition;
            Log.e("tag", "List onClick: " + mPosition);
        }
    }

    /*********************
     * Helper Methods
     **********************/

    public void updateUI() {
        List<Crime> crimes = CrimeLab.get(getActivity()).getCrimeList();
        if (crimes.isEmpty()) {
            // Show the empty view
            mRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            // Hide the empty view
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }

        // Create new adapter or update existing
        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            // Refreshes the list of crimes from the database
            mAdapter.setCrimes(crimes);
        }

        updateSubtitle();
    }

    private void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimeList().size();
        String subtitle = getResources()
                .getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeCount);

        // Don't show subtitles if not visible
        if (!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private Crime addNewCrime() {
        // Create new crime and open CrimePagerActivity
        CrimeLab cL = CrimeLab.get(getActivity());
        Crime c = new Crime();
        // Adds the crime
        cL.addCrime(c);
        return c;
    }
}
