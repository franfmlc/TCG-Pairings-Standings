package com.southernsoft.tcgtournament.pairings.fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import javax.inject.Inject;

public class FragmentAdapter extends FragmentStateAdapter {
    public final int TOTAL_TABS = 2;

    @Inject
    public FragmentAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0)
            return new PairingsFragment();
        return new StandingsFragment();
    }

    @Override
    public int getItemCount() {
        return TOTAL_TABS;
    }
}