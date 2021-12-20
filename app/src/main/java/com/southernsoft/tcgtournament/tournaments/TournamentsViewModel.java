package com.southernsoft.tcgtournament.tournaments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import com.southernsoft.tcgtournament.Repository;
import com.southernsoft.tcgtournament.pojo.TournamentAndRound;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TournamentsViewModel extends ViewModel {
    private Repository repository;
    private LiveData<List<TournamentAndRound>> tournaments;

    @Inject
    public TournamentsViewModel(Repository repository) {
        this.repository = repository;
    }

    public LiveData<List<TournamentAndRound>> getTournaments() {
        if (tournaments == null)
            tournaments = repository.getTournamentsWithLastRound();

        return tournaments;
    }

    public void removeTournament(int tournamentId) {
        repository.deleteTournament(tournamentId);
        repository.deleteTournamentFile(tournamentId);
    }
}