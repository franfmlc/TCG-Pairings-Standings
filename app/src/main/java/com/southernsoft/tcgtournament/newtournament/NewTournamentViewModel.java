package com.southernsoft.tcgtournament.newtournament;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import com.southernsoft.tcgtournament.entity.Round;
import com.southernsoft.tcgtournament.entity.Standing;
import com.southernsoft.tcgtournament.entity.Tournament;
import com.southernsoft.tcgtournament.service.StandingService;
import dagger.hilt.android.lifecycle.HiltViewModel;

import com.southernsoft.tcgtournament.Repository;

@HiltViewModel
public class NewTournamentViewModel extends ViewModel {
    private Repository repository;
    private StandingService standingService;
    private int numberOfPlayers = 0;
    private MutableLiveData<Integer> numberRounds = new MutableLiveData<>();
    private MutableLiveData<Integer> roundTime = new MutableLiveData<>();
    private Tournament tournament;
    private Round round;

    @Inject
    public NewTournamentViewModel(Repository repository, StandingService standingService) {
        this.repository = repository;
        this.standingService = standingService;
        numberRounds.setValue(0);
        roundTime.setValue(50);
    }

    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
        numberRounds.setValue(calculateNumberOfRounds(numberOfPlayers));
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public LiveData<Integer> getNumberOfRounds() {
        return numberRounds;
    }

    public LiveData<Integer> getRoundTime() {
        return roundTime;
    }

    public void decrementRounds() {
        int currentVal = numberRounds.getValue();
        if (currentVal > 1)
            numberRounds.setValue(--currentVal);
    }

    public void incrementRounds() {
        int currentVal = numberRounds.getValue();
        if (currentVal < numberOfPlayers - 1)
            numberRounds.setValue(++currentVal);
    }

    public void decrementTime() {
        int currentVal = roundTime.getValue();
        if (currentVal > 30)
            roundTime.setValue(currentVal - 10);
    }

    public void incrementTime() {
        int currentVal = roundTime.getValue();
        if (currentVal < 90)
            roundTime.setValue(currentVal + 10);
    }

    public void createNewTournament() {
        int roundTimeSeconds = roundTime.getValue() * 60;
        tournament = repository.createTournament(numberOfPlayers, numberRounds.getValue(), roundTimeSeconds);
        round = repository.createRound(tournament.id, 1, roundTimeSeconds);

        Set<Integer> enrolledPlayers = repository.getEnrolledPlayerIds();
        repository.initializeOpponents(tournament.id, enrolledPlayers);

        List<Standing> standings = repository.createNewStandings(tournament.id, round.id, enrolledPlayers);
        Map<Integer, Standing> standingByPlayerId = standingService.mapStandingsByPlayerId(standings);

        repository.createNewPairings(tournament.id, round.id, standingByPlayerId);
    }

    public int getTournamentId() {
        if (tournament != null)
            return tournament.id;
        return 0;
    }

    public int getRoundId() {
        if (round != null)
            return round.id;
        return 0;
    }

    public void deleteEnrolledPlayers() {
        repository.deleteEnrolledPlayers();
    }

    public boolean validNumberOfPlayers() {
        return numberOfPlayers > 2;
    }

    private int calculateNumberOfRounds(int numberOfPlayers) {
        int numberOfRounds = 0;
        if (numberOfPlayers > 1)
            numberOfRounds = numberOfPlayers - 1;
        else if (numberOfPlayers == 1)
            numberOfRounds = 1;
        return numberOfRounds;
    }
}