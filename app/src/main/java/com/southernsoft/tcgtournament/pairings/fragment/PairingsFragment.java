package com.southernsoft.tcgtournament.pairings.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import java.util.List;

import javax.inject.Inject;

import com.southernsoft.tcgtournament.R;
import com.southernsoft.tcgtournament.pairings.PairingsAdapter;
import com.southernsoft.tcgtournament.pairings.PairingsView;
import com.southernsoft.tcgtournament.pairings.PairingsViewModel;
import com.southernsoft.tcgtournament.pairings.dialog.DeleteTournamentDialog;
import com.southernsoft.tcgtournament.pairings.dialog.DeleteTournamentListener;
import com.southernsoft.tcgtournament.pairings.dialog.DialogListener;
import com.southernsoft.tcgtournament.pairings.dialog.NoPlayersMatchedDialog;
import com.southernsoft.tcgtournament.pairings.dialog.PauseTournamentDialog;
import com.southernsoft.tcgtournament.pairings.dialog.UnfinishedMatchesDialog;
import com.southernsoft.tcgtournament.pojo.PairingTuple;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PairingsFragment extends Fragment implements DialogListener, DeleteTournamentListener {
    private PairingsViewModel viewModel;
    @Inject PairingsAdapter pairingsAdapter;
    private Button startRound;
    private Button pause;
    private Button finishRound;
    private Button finishTournament;
    private PairingsView pairingsView;

    enum RoundState {
        READY,
        INPROGRESS,
        LASTROUND
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_pairings, container, false);

        pairingsView = ((PairingsView) getActivity());
        pairingsView.showLoadingDialog();

        RecyclerView mRecyclerView = layout.findViewById(R.id.pairings_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel = new ViewModelProvider(requireActivity()).get(PairingsViewModel.class);
        viewModel.getPairingsForCurrentRound().observe(getViewLifecycleOwner(), this::showPairings);
        viewModel.getPlayersMatched().observe(getViewLifecycleOwner(), this::showPlayersMatchedDialog);

        mRecyclerView.setAdapter(pairingsAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        startRound = layout.findViewById(R.id.button_start_round);
        pause = layout.findViewById(R.id.button_pause_round);
        finishRound = layout.findViewById(R.id.button_finish_round);
        finishTournament = layout.findViewById(R.id.button_finish_tournament);

        startRound.setOnClickListener(view -> {
            viewModel.startCountDown();
            viewModel.setRoundStarted(true);
            updateButtonsVisibility(RoundState.INPROGRESS);
        });

        pause.setOnClickListener(view -> new PauseTournamentDialog().show(getChildFragmentManager(), "dialog"));

        finishRound.setOnClickListener(view -> {
            if (viewModel.matchesUnfinished())
                new UnfinishedMatchesDialog().show(getChildFragmentManager(), "dialog");
            else
                finishCurrentRound();
        });

        finishTournament.setOnClickListener(view -> showDeleteTournamentDialog());

        if (viewModel.hasRoundStarted())
            updateButtonsVisibility(RoundState.INPROGRESS);
        else if (viewModel.hasTournamentFinished())
            updateButtonsVisibility(RoundState.LASTROUND);

        return layout;
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.savePairings(pairingsAdapter.getPairingsToUpdate());
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) { finishCurrentRound(); }

    @Override
    public void onDialogContinue(DialogFragment dialog) {
        viewModel.rematchPlayers();
    }

    @Override
    public void onDialogDeleteTournament() {
        viewModel.removeTournament();
        pairingsView.goToMainActivity();
    }

    @Override
    public void onDialogKeepTournament() {
        pairingsView.goToMainActivity();
    }

    private void showPairings(List<PairingTuple> pairings) {
        pairingsAdapter.replaceData(pairings);
        pairingsView.hideLoadingDialog();
    }

    private void showPlayersMatchedDialog(boolean playersMatched) {
        if (!playersMatched) {
            NoPlayersMatchedDialog dialog = new NoPlayersMatchedDialog();
            dialog.setCancelable(false);
            dialog.show(getChildFragmentManager(), "dialog");
        }
    }

    private void updateButtonsVisibility(RoundState roundState) {
        switch(roundState) {
            case READY:
                startRound.setVisibility(View.VISIBLE);
                pause.setVisibility(View.GONE);
                finishRound.setVisibility(View.GONE);
                break;
            case INPROGRESS:
                startRound.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
                finishRound.setVisibility(View.VISIBLE);
                break;
            case LASTROUND:
                startRound.setVisibility(View.GONE);
                pause.setVisibility(View.GONE);
                finishRound.setVisibility(View.GONE);
                finishTournament.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void finishCurrentRound() {
        if (viewModel.hasNextRound()) {
            updateButtonsVisibility(RoundState.READY);
            pairingsView.showLoadingDialog();
        } else {
            updateButtonsVisibility(RoundState.LASTROUND);
            pairingsView.goToStandingsTab();
        }

        viewModel.stopCountDown();
        viewModel.setRoundStarted(false);
        viewModel.manageNextRound();
    }

    private void showDeleteTournamentDialog() {
        DeleteTournamentDialog dialog = new DeleteTournamentDialog();
        dialog.show(getChildFragmentManager(), "dialog");
    }
}