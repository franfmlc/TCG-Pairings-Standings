package com.southernsoft.tcgtournament.pairings.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import javax.inject.Inject;

import com.southernsoft.tcgtournament.R;
import com.southernsoft.tcgtournament.pairings.PairingsViewModel;
import com.southernsoft.tcgtournament.pojo.StandingRow;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StandingsFragment extends Fragment {
    @Inject StandingsAdapter standingsAdapter;
    @Inject TiebreakersAdapter tiebreakersAdapter;
    private int touchedListTag;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View layout = inflater.inflate(R.layout.fragment_standings, container, false);

        RecyclerView standingsView = layout.findViewById(R.id.standings_list);
        standingsView.setLayoutManager(new LinearLayoutManager(getContext()));

        RecyclerView tiebreakersView = layout.findViewById(R.id.tiebreakers_list);
        tiebreakersView.setLayoutManager(new LinearLayoutManager(getContext()));

        standingsView.setTag(0);
        tiebreakersView.setTag(1);

        standingsView.addOnItemTouchListener(touchListener);
        tiebreakersView.addOnItemTouchListener(touchListener);

        standingsView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                boolean hasBeenTouched = touchedListTag == (int) recyclerView.getTag();
                if (hasBeenTouched)
                    tiebreakersView.scrollBy(dx, dy);
            }
        });

        tiebreakersView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                boolean hasBeenTouched = touchedListTag == (int) recyclerView.getTag();
                if (hasBeenTouched)
                    standingsView.scrollBy(dx, dy);
            }
        });

        final PairingsViewModel viewModel = new ViewModelProvider(requireActivity()).get(PairingsViewModel.class);
        viewModel.getStandingsForCurrentRound().observe(getViewLifecycleOwner(), this::showStandings);

        standingsView.setAdapter(standingsAdapter);
        standingsView.addItemDecoration(new DividerItemDecoration(standingsView.getContext(), DividerItemDecoration.VERTICAL));

        tiebreakersView.setAdapter(tiebreakersAdapter);
        tiebreakersView.addItemDecoration(new DividerItemDecoration(standingsView.getContext(), DividerItemDecoration.VERTICAL));

        return layout;
    }

    private RecyclerView.OnItemTouchListener touchListener = new RecyclerView.OnItemTouchListener() {
        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
            touchedListTag = (int) rv.getTag();
            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {}

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}

    };

    private void showStandings(List<StandingRow> standings) {
        standingsAdapter.replaceData(standings);
        tiebreakersAdapter.replaceData(standings);
    }
}
