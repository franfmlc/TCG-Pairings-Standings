package com.southernsoft.tcgtournament.pairings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import com.southernsoft.tcgtournament.Repository;
import com.southernsoft.tcgtournament.entity.Pairing;
import com.southernsoft.tcgtournament.entity.Round;
import com.southernsoft.tcgtournament.entity.Standing;
import com.southernsoft.tcgtournament.entity.Tournament;
import com.southernsoft.tcgtournament.pojo.PairingTuple;
import com.southernsoft.tcgtournament.pojo.StandingRow;
import com.southernsoft.tcgtournament.service.PairingService;
import com.southernsoft.tcgtournament.service.StandingService;
import com.southernsoft.tcgtournament.util.SingleLiveEvent;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PairingsViewModel extends ViewModel {
    /* Dependencies */
    private final Repository repository;
    private final PairingService pairingService;
    private final StandingService standingService;

    /* Exposed LiveData */
    private MutableLiveData<Integer> roundNumber;
    private MutableLiveData<Integer> roundTime;
    private LiveData<List<PairingTuple>> pairings;
    private LiveData<List<StandingRow>> standings;
    private SingleLiveEvent<Boolean> playersMatched;

    private RoundTimer timer;
    private Tournament tournament;
    private Round round;
    private boolean roundStarted;
    private long endTimeInMillis;

    @Inject
    public PairingsViewModel(Repository repository, PairingService pairingService, StandingService standingService, RoundTimer timer) {
        this.repository = repository;
        this.pairingService = pairingService;
        this.standingService = standingService;
        this.timer = timer;
        roundStarted = false;
    }

    public LiveData<Integer> getCurrentRoundNumber() {
        return roundNumber;
    }

    public LiveData<Integer> getCurrentRoundTime() {
        return roundTime;
    }

    public LiveData<List<PairingTuple>> getPairingsForCurrentRound() {
        if (pairings == null)
            pairings = repository.getLastPairingsForTournament(tournament.id);
        return pairings;
    }

    public LiveData<List<StandingRow>> getStandingsForCurrentRound() {
        if (standings == null) {
            standings = repository.getLastStandingsForTournament(tournament.id);
        }
        return standings;
    }

    public LiveData<Boolean> getPlayersMatched() {
        if (playersMatched == null) {
            playersMatched = new SingleLiveEvent<>();
        }
        return playersMatched;
    }

    public void setTournament(int tournamentId) {
        if (tournament == null)
            tournament = repository.getTournament(tournamentId);
    }

    public void setRound(int roundId) {
        if (round == null) {
            round = repository.getRound(roundId);
            roundNumber = new MutableLiveData<>();
            roundTime = new MutableLiveData<>();

            if (round.roundNumber <= tournament.numberRounds)
                roundNumber.setValue(round.roundNumber);
            else
                roundNumber.setValue(round.roundNumber - 1);

            roundTime.setValue(round.roundTime);
        }
    }

    public boolean hasRoundStarted() {
        return roundStarted;
    }

    public boolean hasNextRound() {
        return round.roundNumber < tournament.numberRounds;
    }

    public boolean hasTournamentFinished() {
        return round.roundNumber > tournament.numberRounds;
    }

    public boolean matchesUnfinished() {
        boolean matchesUnfinished = false;

        for (PairingTuple tuple : pairings.getValue()) {
            if (tuple.pairing.firstPlayerResult != 2 && tuple.pairing.secondPlayerResult != 2) {
                matchesUnfinished = true;
                break;
            }
        }
        return matchesUnfinished;
    }

    public void startCountDown() {
        Integer roundTimeInSeconds = roundTime.getValue();
        endTimeInMillis = System.currentTimeMillis() + (roundTimeInSeconds * 1000);
        timer.start(roundTime, roundTimeInSeconds, 1000);
    }

    public void resumeCountDown() {
        long remainingTime = endTimeInMillis - System.currentTimeMillis();
        if (remainingTime > 1000) {
            int remainingTimeInSeconds = (int) remainingTime / 1000;
            timer.start(roundTime, remainingTimeInSeconds, 1000);
        } else {
            roundTime.setValue(0);
        }
    }

    public void stopCountDown() {
        timer.cancel();
    }

    public void setRoundStarted(boolean hasRoundStarted) {
        roundStarted = hasRoundStarted;
    }

    public void savePairings(List<Pairing> pairingsToUpdate) {
        if (pairingsToUpdate.size() > 0 && !hasTournamentFinished()) {
            repository.updatePairings(pairingsToUpdate);
        }
    }

    public void saveRoundTime() {
        if (roundStarted) {
            round.roundTime = roundTime.getValue();
            repository.updateRound(round);
        }
    }

    public void manageNextRound() {
        round = repository.createRound(tournament.id, ++round.roundNumber, tournament.roundTime);

        if (round.roundNumber <= tournament.numberRounds)
            roundNumber.setValue(round.roundNumber);

        roundTime.setValue(round.roundTime);

        List<Standing> newStandings = standingService.createStandingsForNextRound(getCurrentStandings(), round.id);

        Map<Integer, Standing> standingsByPlayerId = standingService.mapStandingsByPlayerId(newStandings);
        updateStandingsPoints(standingsByPlayerId);

        repository.insertStandings(newStandings);

        if (shouldCreatePairings()) {
            try {
                repository.createNewPairings(tournament.id, round.id, standingsByPlayerId);
            } catch (IllegalArgumentException ex) {
                if (playersMatched == null)
                    playersMatched = new SingleLiveEvent<>();
                playersMatched.setValue(false);
            }
        }
    }

    public void rematchPlayers() {
        Map<Integer, Standing> standingsByPlayerId = standingService.mapStandingRowsByPlayerId(getCurrentStandings());

        /* Reset matched opponents */
        repository.initializeOpponents(tournament.id, standingsByPlayerId.keySet());

        repository.createNewPairings(tournament.id, round.id, standingsByPlayerId);
    }

    public void removeTournament() {
        repository.deleteTournament(tournament.id);
        repository.deleteTournamentFile(tournament.id);
    }

    private boolean shouldCreatePairings() {
        return round.roundNumber <= tournament.numberRounds;
    }

    private void updateStandingsPoints(Map<Integer, Standing> standingsByPlayerId ) {
        Map<Integer, Set<Integer>> opponentsByPlayerId = repository.getOpponentsForTournament(tournament.id);
        List<Pairing> currentPairings = pairingService.getPairingsFromTuples(getCurrentPairings());

        standingService.updateStandingsPoints(standingsByPlayerId, currentPairings);
        standingService.updateStandingsTiebreakers(standingsByPlayerId, opponentsByPlayerId);
    }

    // These method allows mocking calls that access to current data by passing a null reference
    private List<PairingTuple> getCurrentPairings() {
        // This method allows mocking calls that access to current Standings by passing a null reference
        if (pairings != null)
            return pairings.getValue();
        return null;
    }

    private List<StandingRow> getCurrentStandings() {
        if (standings != null)
            return standings.getValue();
        return null;
    }
}