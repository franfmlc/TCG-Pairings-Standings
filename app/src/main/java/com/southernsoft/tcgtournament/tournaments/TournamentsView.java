package com.southernsoft.tcgtournament.tournaments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import javax.inject.Inject;

import com.southernsoft.tcgtournament.R;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TournamentsView extends AppCompatActivity {
    private TournamentsViewModel viewModel;
    private View tournamentsMsg;
    @Inject TournamentsListAdapter tournamentAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournaments_view);

        tournamentsMsg = findViewById(R.id.no_tournaments_found);
        RecyclerView recyclerView = findViewById(R.id.tournaments_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(tournamentAdapter);

        viewModel = new ViewModelProvider(this).get(TournamentsViewModel.class);
        viewModel.getTournaments().observe(this, list -> tournamentAdapter.submitList(list));

        RecyclerView.AdapterDataObserver dataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                if (tournamentAdapter.getItemCount() > 0)
                    tournamentsMsg.setVisibility(View.GONE);
            }
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                if (tournamentAdapter.getItemCount() == 0)
                    tournamentsMsg.setVisibility(View.VISIBLE);
            }
        };
        tournamentAdapter.registerAdapterDataObserver(dataObserver);
    }

    public void deleteTournament(int tournamentId) {
        viewModel.removeTournament(tournamentId);
    }
}