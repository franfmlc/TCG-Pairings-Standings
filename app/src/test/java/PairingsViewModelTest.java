import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.southernsoft.tcgtournament.Repository;
import com.southernsoft.tcgtournament.entity.Pairing;
import com.southernsoft.tcgtournament.entity.Round;
import com.southernsoft.tcgtournament.entity.Standing;
import com.southernsoft.tcgtournament.entity.Tournament;
import com.southernsoft.tcgtournament.pairings.PairingsViewModel;
import com.southernsoft.tcgtournament.pairings.RoundTimer;
import com.southernsoft.tcgtournament.pojo.PairingTuple;
import com.southernsoft.tcgtournament.pojo.StandingRow;
import com.southernsoft.tcgtournament.service.PairingService;
import com.southernsoft.tcgtournament.service.StandingService;
import com.southernsoft.tcgtournament.util.Lists;
import util.LiveDataTestUtil;
import util.RandomIdGenerator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PairingsViewModelTest {
    @Rule public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Mock private Repository repository;
    @Mock private PairingService pairingService;
    @Mock private StandingService standingService;
    @Mock private RoundTimer roundTimer;

    private PairingsViewModel pairingsViewModel;
    private RandomIdGenerator idGenerator;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        pairingsViewModel = new PairingsViewModel(repository, pairingService, standingService, roundTimer);
        idGenerator = new RandomIdGenerator();
    }

    @Test
    public void getPairingsForCurrentRound_twoPairings_pairingsRetrieved() {
        Tournament tournament = new Tournament();
        tournament.id = idGenerator.getRandomId();
        String p1Name = "Alucard";
        String p2Name = "Richter";
        String p3Name = "Maria";
        String p4Name = "Dracula";
        PairingTuple pairingOne = new PairingTuple();
        PairingTuple pairingTwo = new PairingTuple();

        pairingOne.firstPlayerName = p1Name;
        pairingOne.secondPlayerName = p2Name;
        pairingTwo.firstPlayerName = p3Name;
        pairingTwo.secondPlayerName = p4Name;

        MutableLiveData<List<PairingTuple>> tuples = new MutableLiveData<>();
        tuples.setValue(Lists.newArrayList(pairingOne, pairingTwo));

        when(repository.getTournament(tournament.id)).thenReturn(tournament);
        when(repository.getLastPairingsForTournament(tournament.id)).thenReturn(tuples);

        pairingsViewModel.setTournament(tournament.id);

        // When
        List<PairingTuple> actualPairings = new ArrayList<>();
        try {
            actualPairings = LiveDataTestUtil.getOrAwaitValue(pairingsViewModel.getPairingsForCurrentRound());
        } catch (InterruptedException ex) {}

        // Then
        assertEquals(p1Name, actualPairings.get(0).firstPlayerName);
        assertEquals(p2Name, actualPairings.get(0).secondPlayerName);
        assertEquals(p3Name, actualPairings.get(1).firstPlayerName);
        assertEquals(p4Name, actualPairings.get(1).secondPlayerName);
        verify(repository, times(1)).getTournament(tournament.id);
        verify(repository, times(1)).getLastPairingsForTournament(tournament.id);
    }

    @Test
    public void getStandingsForCurrentRound_twoStandings_standingsRetrieved() {
        Tournament tournament = new Tournament();
        tournament.id = idGenerator.getRandomId();
        String p1Name = "Frederic";
        String p2Name = "Oddlopp";
        StandingRow standingOne = new StandingRow();
        StandingRow standingTwo = new StandingRow();

        standingOne.playerName = p1Name;
        standingTwo.playerName = p2Name;

        MutableLiveData<List<StandingRow>> rows = new MutableLiveData<>();
        rows.setValue(Lists.newArrayList(standingOne, standingTwo));

        when(repository.getTournament(tournament.id)).thenReturn(tournament);
        when(repository.getLastStandingsForTournament(tournament.id)).thenReturn(rows);

        pairingsViewModel.setTournament(tournament.id);

        // When
        List<StandingRow> actualStandings = new ArrayList<>();
        try {
            actualStandings = LiveDataTestUtil.getOrAwaitValue(pairingsViewModel.getStandingsForCurrentRound());
        } catch (InterruptedException ex) {}

        // Then
        assertEquals(p1Name, actualStandings.get(0).playerName);
        assertEquals(p2Name, actualStandings.get(1).playerName);
        verify(repository, times(1)).getTournament(tournament.id);
        verify(repository, times(1)).getLastStandingsForTournament(tournament.id);
    }

    @Test
    public void getCurrentRoundNumber_numberLessThanTotalRounds_returnsRoundNumber() {
        // Given
        Tournament tournament = new Tournament();
        tournament.id = idGenerator.getRandomId();
        tournament.numberRounds = 5;

        Round round = new Round();
        int roundNumber = 2;
        round.id = idGenerator.getRandomId();
        round.roundNumber = roundNumber;

        when(repository.getTournament(tournament.id)).thenReturn(tournament);
        when(repository.getRound(round.id)).thenReturn(round);
        pairingsViewModel.setTournament(tournament.id);
        pairingsViewModel.setRound(round.id);

        // When
        int actualRoundNumber = 0;
        try {
            actualRoundNumber = LiveDataTestUtil.getOrAwaitValue(pairingsViewModel.getCurrentRoundNumber());
        } catch (InterruptedException ex) {}

        // Then
        assertEquals(roundNumber, actualRoundNumber);
        verify(repository, times(1)).getTournament(tournament.id);
        verify(repository, times(1)).getRound(round.id);
    }

    @Test
    public void getCurrentRoundNumber_numberGreaterThanTotalRounds_returnsRoundNumberMinusOne() {
        // Given
        Tournament tournament = new Tournament();
        tournament.id = idGenerator.getRandomId();
        tournament.numberRounds = 5;

        Round round = new Round();
        int roundNumber = 6;
        round.id = idGenerator.getRandomId();
        round.roundNumber = roundNumber;

        when(repository.getTournament(tournament.id)).thenReturn(tournament);
        when(repository.getRound(round.id)).thenReturn(round);
        pairingsViewModel.setTournament(tournament.id);
        pairingsViewModel.setRound(round.id);

        // When
        int actualRoundNumber = 0;
        try {
            actualRoundNumber = LiveDataTestUtil.getOrAwaitValue(pairingsViewModel.getCurrentRoundNumber());
        } catch (InterruptedException ex) {}

        // Then
        assertEquals(roundNumber - 1, actualRoundNumber);
        verify(repository, times(1)).getTournament(tournament.id);
        verify(repository, times(1)).getRound(round.id);
    }

    @Test
    public void getCurrentRoundTime_roundTimeSet_returnsRoundTime() {
        // Given
        Tournament tournament = new Tournament();
        tournament.id = idGenerator.getRandomId();

        Round round = new Round();
        int roundTime = 6000;
        round.id = idGenerator.getRandomId();
        round.roundTime = roundTime;

        when(repository.getTournament(tournament.id)).thenReturn(tournament);
        when(repository.getRound(round.id)).thenReturn(round);
        pairingsViewModel.setTournament(tournament.id);
        pairingsViewModel.setRound(round.id);

        // When
        int actualRoundTime = 0;
        try {
            actualRoundTime = LiveDataTestUtil.getOrAwaitValue(pairingsViewModel.getCurrentRoundTime());
        } catch (InterruptedException ex) {}

        // Then
        assertEquals(roundTime, actualRoundTime);
        verify(repository, times(1)).getTournament(tournament.id);
        verify(repository, times(1)).getRound(round.id);
    }

    @Test
    public void hasNextRound_currentLessThanTotal_returnsTrue() {
        // Given
        Tournament tournament = new Tournament();
        tournament.id = idGenerator.getRandomId();
        tournament.numberRounds = 99;

        Round round = new Round();
        round.id = idGenerator.getRandomId();
        round.roundNumber = 98;

        when(repository.getTournament(tournament.id)).thenReturn(tournament);
        when(repository.getRound(round.id)).thenReturn(round);
        pairingsViewModel.setTournament(tournament.id);
        pairingsViewModel.setRound(round.id);

        // When
        boolean result = pairingsViewModel.hasNextRound();

        // Then
        assertTrue(result);
        verify(repository, times(1)).getTournament(tournament.id);
        verify(repository, times(1)).getRound(round.id);
    }

    @Test
    public void hasNextRound_currentSameAsTotal_returnsFalse() {
        // Given
        Tournament tournament = new Tournament();
        tournament.id = idGenerator.getRandomId();
        tournament.numberRounds = 99;

        Round round = new Round();
        round.id = idGenerator.getRandomId();
        round.roundNumber = 99;

        when(repository.getTournament(tournament.id)).thenReturn(tournament);
        when(repository.getRound(round.id)).thenReturn(round);
        pairingsViewModel.setTournament(tournament.id);
        pairingsViewModel.setRound(round.id);

        // When
        boolean result = pairingsViewModel.hasNextRound();

        // Then
        assertFalse(result);
        verify(repository, times(1)).getTournament(tournament.id);
        verify(repository, times(1)).getRound(round.id);
    }

    @Test
    public void hasTournamentFinished_currentRoundGreaterThanTotal_returnsTrue() {
        // Given
        Tournament tournament = new Tournament();
        tournament.id = idGenerator.getRandomId();
        tournament.numberRounds = 99;

        Round round = new Round();
        round.id = idGenerator.getRandomId();
        round.roundNumber = 100;

        when(repository.getTournament(tournament.id)).thenReturn(tournament);
        when(repository.getRound(round.id)).thenReturn(round);
        pairingsViewModel.setTournament(tournament.id);
        pairingsViewModel.setRound(round.id);

        // When
        boolean result = pairingsViewModel.hasTournamentFinished();

        // Then
        assertTrue(result);
        verify(repository, times(1)).getTournament(tournament.id);
        verify(repository, times(1)).getRound(round.id);
    }

    @Test
    public void matchesUnfinished_containsMatchesUnfinished_returnsTrue() {
        // Given
        Tournament tournament = new Tournament();
        tournament.id = idGenerator.getRandomId();

        Pairing pairingOne = new Pairing();
        pairingOne.firstPlayerResult = 2;
        pairingOne.secondPlayerResult = 0;

        Pairing pairingTwo = new Pairing();
        pairingTwo.firstPlayerResult = 1;
        pairingTwo.secondPlayerResult = 1;

        PairingTuple tupleOne = new PairingTuple();
        tupleOne.pairing = pairingOne;

        PairingTuple tupleTwo = new PairingTuple();
        tupleTwo.pairing = pairingTwo;

        MutableLiveData<List<PairingTuple>> liveDataTuples = new MutableLiveData<>();
        liveDataTuples.setValue(Lists.newArrayList(tupleOne, tupleTwo));

        when(repository.getTournament(tournament.id)).thenReturn(tournament);
        when(repository.getLastPairingsForTournament(tournament.id)).thenReturn(liveDataTuples);

        pairingsViewModel.setTournament(tournament.id);

        try {
            // Preload pairing tuples in ViewModel
            LiveDataTestUtil.getOrAwaitValue(pairingsViewModel.getPairingsForCurrentRound());
        } catch (InterruptedException ex) {}

        // When
        boolean matchesUnfinished = pairingsViewModel.matchesUnfinished();

        // Then
        assertTrue(matchesUnfinished);
        verify(repository, times(1)).getTournament(tournament.id);
        verify(repository, times(1)).getLastPairingsForTournament(tournament.id);
    }

    @Test
    public void savePairings_twoPairingsToUpdate_repositoryCalled() {
        // Given
        Tournament tournament = new Tournament();
        tournament.id = idGenerator.getRandomId();
        tournament.numberRounds = 10;

        Round round = new Round();
        round.id = idGenerator.getRandomId();
        round.roundNumber = 4;

        Pairing pairingOne = new Pairing();
        Pairing pairingTwo = new Pairing();
        List<Pairing> pairings = Lists.newArrayList(pairingOne, pairingTwo);

        when(repository.getTournament(tournament.id)).thenReturn(tournament);
        when(repository.getRound(round.id)).thenReturn(round);

        pairingsViewModel.setTournament(tournament.id);
        pairingsViewModel.setRound(round.id);

        // When
        pairingsViewModel.savePairings(pairings);

        // Then
        verify(repository, times(1)).updatePairings(pairings);
    }

    @Test
    public void saveRoundTime_roundStarted_repositoryCalled() {
        // Given
        Tournament tournament = new Tournament();
        tournament.id = idGenerator.getRandomId();

        Round round = new Round();
        round.id = idGenerator.getRandomId();

        when(repository.getTournament(tournament.id)).thenReturn(tournament);
        when(repository.getRound(round.id)).thenReturn(round);

        pairingsViewModel.setTournament(tournament.id);
        pairingsViewModel.setRound(round.id);
        pairingsViewModel.setRoundStarted(true);

        // When
        pairingsViewModel.saveRoundTime();

        // Then
        assertTrue(pairingsViewModel.hasRoundStarted());
        verify(repository, times(1)).updateRound(round);
    }

    @Test
    public void startCountDown_timerStartMethodCalled() {
        // Given
        int tournamentId = idGenerator.getRandomId();
        int roundId = idGenerator.getRandomId();

        when(repository.getTournament(tournamentId)).thenReturn(new Tournament());
        when(repository.getRound(roundId)).thenReturn(new Round());
        pairingsViewModel.setTournament(tournamentId);
        pairingsViewModel.setRound(roundId);

        // When
        pairingsViewModel.startCountDown();

        // Then
        verify(repository, times(1)).getTournament(tournamentId);
        verify(repository, times(1)).getRound(roundId);
        verify(roundTimer, times(1)).start(any(), anyInt(), anyInt());
    }

    @Test
    public void stopCountDown_timerCancelMethodCalled() {
        // Given
        int tournamentId = idGenerator.getRandomId();
        int roundId = idGenerator.getRandomId();

        when(repository.getTournament(tournamentId)).thenReturn(new Tournament());
        when(repository.getRound(roundId)).thenReturn(new Round());
        pairingsViewModel.setTournament(tournamentId);
        pairingsViewModel.setRound(roundId);

        // When
        pairingsViewModel.stopCountDown();

        // Then
        verify(repository, times(1)).getTournament(tournamentId);
        verify(repository, times(1)).getRound(roundId);
        verify(roundTimer, times(1)).cancel();
    }

    @Test
    public void setRoundStarted_roundStarted_returnsTrue() {
        // Given
        boolean roundStarted = true;

        // When
        pairingsViewModel.setRoundStarted(roundStarted);

        // Then
        assertTrue(pairingsViewModel.hasRoundStarted());
    }

    @Test
    public void rematchPlayers_repositoryCalled() {
        // Given
        Tournament tournament = new Tournament();
        tournament.id = idGenerator.getRandomId();

        Round round = new Round();
        round.id = idGenerator.getRandomId();

        Map<Integer, Standing> standingsByPlayerId = new HashMap<>();

        when(repository.getTournament(tournament.id)).thenReturn(tournament);
        when(repository.getRound(round.id)).thenReturn(round);
        when(standingService.mapStandingRowsByPlayerId(null)).thenReturn(standingsByPlayerId);

        pairingsViewModel.setTournament(tournament.id);
        pairingsViewModel.setRound(round.id);

        // When
        pairingsViewModel.rematchPlayers();

        // Then
        verify(standingService, times(1)).mapStandingRowsByPlayerId(null);
        verify(repository, times(1)).initializeOpponents(tournament.id, standingsByPlayerId.keySet());
        verify(repository, times(1)).createNewPairings(tournament.id, round.id, standingsByPlayerId);
    }

    @Test
    public void removeTournament_repositoryCalled() {
        // Given
        Tournament tournament = new Tournament();
        tournament.id = idGenerator.getRandomId();

        when(repository.getTournament(tournament.id)).thenReturn(tournament);

        pairingsViewModel.setTournament(tournament.id);

        // When
        pairingsViewModel.removeTournament();

        // Then
        verify(repository, times(1)).deleteTournament(tournament.id);
        verify(repository, times(1)).deleteTournamentFile(tournament.id);
    }

    @Test
    public void manageNextRound_newRoundRequired_standingsAndPairingsCreated() {
        // Given
        int roundTime = 5000;
        int currentRoundNumber = 2;

        Tournament tournament = new Tournament();
        tournament.id = idGenerator.getRandomId();
        tournament.numberRounds = 6;
        tournament.roundTime = roundTime;

        Round round = new Round();
        round.roundNumber = currentRoundNumber;

        Round newRound = new Round();
        newRound.id = idGenerator.getRandomId();
        newRound.roundNumber = ++currentRoundNumber;
        newRound.roundTime = roundTime;

        List<Standing> newStandings = new ArrayList<>();
        List<Pairing> pairings = new ArrayList<>();
        Map<Integer, Standing> standingsByPlayerId = new HashMap<>();
        Map<Integer, Set<Integer>> opponentsByPlayerId = new HashMap<>();

        when(repository.getTournament(tournament.id)).thenReturn(tournament);
        when(repository.getRound(round.id)).thenReturn(round);
        when(repository.createRound(tournament.id, currentRoundNumber, tournament.roundTime)).thenReturn(newRound);
        when(repository.getOpponentsForTournament(tournament.id)).thenReturn(opponentsByPlayerId);
        when(standingService.createStandingsForNextRound(null, newRound.id)).thenReturn(newStandings);
        when(standingService.mapStandingsByPlayerId(newStandings)).thenReturn(standingsByPlayerId);
        when(pairingService.getPairingsFromTuples(null)).thenReturn(pairings);

        pairingsViewModel.setTournament(tournament.id);
        pairingsViewModel.setRound(round.id);

        // When
        pairingsViewModel.manageNextRound();

        // Then
        verify(repository, times(1)).getTournament(tournament.id);
        verify(repository, times(1)).getRound(round.id);
        verify(repository, times(1)).createRound(tournament.id, currentRoundNumber, roundTime);
        verify(repository, times(1)).getOpponentsForTournament(tournament.id);
        verify(repository, times(1)).insertStandings(newStandings);
        verify(repository, times(1)).createNewPairings(tournament.id, newRound.id, standingsByPlayerId);
        verify(standingService, times(1)).createStandingsForNextRound(null, newRound.id);
        verify(standingService, times(1)).mapStandingsByPlayerId(newStandings);
        verify(standingService, times(1)).updateStandingsPoints(standingsByPlayerId, pairings);
        verify(standingService, times(1)).updateStandingsTiebreakers(standingsByPlayerId, opponentsByPlayerId);
        verify(pairingService, times(1)).getPairingsFromTuples(null);
    }

    @Test
    public void manageNextRound_errorCreatingPairings_exceptionCaught() {
        // Given
        int roundTime = 5000;
        int currentRoundNumber = 2;

        Tournament tournament = new Tournament();
        tournament.id = idGenerator.getRandomId();
        tournament.numberRounds = 6;
        tournament.roundTime = roundTime;

        Round round = new Round();
        round.roundNumber = currentRoundNumber;

        Round newRound = new Round();
        newRound.id = idGenerator.getRandomId();
        newRound.roundNumber = ++currentRoundNumber;
        newRound.roundTime = roundTime;

        List<Standing> newStandings = new ArrayList<>();
        List<Pairing> pairings = new ArrayList<>();
        Map<Integer, Standing> standingsByPlayerId = new HashMap<>();
        Map<Integer, Set<Integer>> opponentsByPlayerId = new HashMap<>();

        when(repository.getTournament(tournament.id)).thenReturn(tournament);
        when(repository.getRound(round.id)).thenReturn(round);
        when(repository.createRound(tournament.id, currentRoundNumber, tournament.roundTime)).thenReturn(newRound);
        when(repository.getOpponentsForTournament(tournament.id)).thenReturn(opponentsByPlayerId);
        when(standingService.createStandingsForNextRound(null, newRound.id)).thenReturn(newStandings);
        when(standingService.mapStandingsByPlayerId(newStandings)).thenReturn(standingsByPlayerId);
        when(pairingService.getPairingsFromTuples(null)).thenReturn(pairings);
        doThrow(IllegalArgumentException.class).when(repository).createNewPairings(tournament.id, newRound.id, standingsByPlayerId);

        pairingsViewModel.setTournament(tournament.id);
        pairingsViewModel.setRound(round.id);

        // When
        boolean playersMatched = true;
        try {
            pairingsViewModel.manageNextRound();
            playersMatched = LiveDataTestUtil.getOrAwaitValue(pairingsViewModel.getPlayersMatched());
        } catch (InterruptedException ex) {}

        // Then
        assertFalse(playersMatched);
        verify(repository, times(1)).getTournament(tournament.id);
        verify(repository, times(1)).getRound(round.id);
        verify(repository, times(1)).createRound(tournament.id, currentRoundNumber, roundTime);
        verify(repository, times(1)).getOpponentsForTournament(tournament.id);
        verify(repository, times(1)).insertStandings(newStandings);
        verify(repository, times(1)).createNewPairings(tournament.id, newRound.id, standingsByPlayerId);
        verify(standingService, times(1)).createStandingsForNextRound(null, newRound.id);
        verify(standingService, times(1)).mapStandingsByPlayerId(newStandings);
        verify(standingService, times(1)).updateStandingsPoints(standingsByPlayerId, pairings);
        verify(standingService, times(1)).updateStandingsTiebreakers(standingsByPlayerId, opponentsByPlayerId);
        verify(pairingService, times(1)).getPairingsFromTuples(null);
    }
}