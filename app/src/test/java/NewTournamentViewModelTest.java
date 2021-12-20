import android.util.ArraySet;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

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
import com.southernsoft.tcgtournament.entity.Round;
import com.southernsoft.tcgtournament.entity.Standing;
import com.southernsoft.tcgtournament.entity.Tournament;
import com.southernsoft.tcgtournament.newtournament.NewTournamentViewModel;
import com.southernsoft.tcgtournament.service.StandingService;
import util.LiveDataTestUtil;
import util.RandomIdGenerator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NewTournamentViewModelTest {
    @Rule public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Mock private Repository repository;
    @Mock private StandingService standingService;

    private NewTournamentViewModel newTournamentViewModel;
    private RandomIdGenerator idGenerator;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        newTournamentViewModel = new NewTournamentViewModel(repository, standingService);
        idGenerator = new RandomIdGenerator();
    }

    @Test
    public void getNumberOfRounds_sixPlayers_numberOfRoundsCalculated() {
        // Given
        int numOfPlayers = 6;
        int expectedNumOfRounds = 5;
        int actualNumOfRounds = 0;

        newTournamentViewModel.setNumberOfPlayers(numOfPlayers);
        // When
        try {
            actualNumOfRounds = LiveDataTestUtil.getOrAwaitValue(newTournamentViewModel.getNumberOfRounds());
        } catch (InterruptedException ex) {}

        // Then
        assertEquals(expectedNumOfRounds, actualNumOfRounds);
        assertEquals(numOfPlayers, newTournamentViewModel.getNumberOfPlayers());
    }

    @Test
    public void getNumberOfRounds_roundsIncremented_returnsRounds() {
        // Given
        int expectedRounds = 2;
        int actualRounds = 0;
        int numOfPlayers = 3;

        newTournamentViewModel.setNumberOfPlayers(numOfPlayers);

        // When
        newTournamentViewModel.decrementRounds(); // 1
        newTournamentViewModel.incrementRounds(); // 2

        try {
            actualRounds = LiveDataTestUtil.getOrAwaitValue(newTournamentViewModel.getNumberOfRounds());
        } catch (InterruptedException ex) {}

        // Then
        assertEquals(expectedRounds, actualRounds);
    }

    @Test
    public void getRoundTime_timeIncremented_returnsTime() {
        // Given
        int expectedRoundTime = 70;
        int actualRoundTime = 0;

        // When
        newTournamentViewModel.incrementTime(); // 60
        newTournamentViewModel.incrementTime(); // 70
        newTournamentViewModel.incrementTime(); // 80
        newTournamentViewModel.decrementTime(); // 70

        try {
            actualRoundTime = LiveDataTestUtil.getOrAwaitValue(newTournamentViewModel.getRoundTime());
        } catch (InterruptedException ex) {}

        // Then
        assertEquals(expectedRoundTime, actualRoundTime);
    }

    @Test
    public void createNewTournament_allDataProvided_servicesCalled() {
        // Given
        int numOfPlayers = 5;
        int numOfRounds = 4;
        Tournament tournament = new Tournament();
        Round round = new Round();
        Set<Integer> enrolledPlayers = new ArraySet<>();
        List<Standing> standings = new ArrayList<>();
        Map<Integer, Standing> standingsByPlayerId = new HashMap<>();

        tournament.id = idGenerator.getRandomId();
        round.id = idGenerator.getRandomId();

        newTournamentViewModel.setNumberOfPlayers(numOfPlayers);

        when(repository.createTournament(numOfPlayers, numOfRounds, 3000)).thenReturn(tournament);
        when(repository.createRound(tournament.id, 1, 3000)).thenReturn(round);
        when(repository.getEnrolledPlayerIds()).thenReturn(enrolledPlayers);
        when(repository.createNewStandings(tournament.id, round.id, enrolledPlayers)).thenReturn(standings);
        when(standingService.mapStandingsByPlayerId(standings)).thenReturn(standingsByPlayerId);

        // When
        newTournamentViewModel.createNewTournament();

        // Then
        verify(repository, times(1)).createTournament(numOfPlayers, numOfRounds, 3000);
        verify(repository, times(1)).createRound(tournament.id, 1, 3000);
        verify(repository, times(1)).getEnrolledPlayerIds();
        verify(repository, times(1)).initializeOpponents(tournament.id, enrolledPlayers);
        verify(repository, times(1)).createNewStandings(tournament.id, round.id, enrolledPlayers);
        verify(repository, times(1)).createNewPairings(tournament.id, round.id, standingsByPlayerId);
        verify(standingService, times(1)).mapStandingsByPlayerId(standings);
    }

    @Test
    public void getTournamentId_noTournamentSet_returnsZero() {
        // Given
        int expectedId = 0;

        // When - Then
        assertEquals(expectedId, newTournamentViewModel.getTournamentId());
    }

    @Test
    public void getRoundId_noRoundSet_returnsZero() {
        // Given
        int expectedId = 0;

        // When - Then
        assertEquals(expectedId, newTournamentViewModel.getRoundId());
    }

    @Test
    public void deleteEnrolledPlayers_repositoryCalled() {
        // When
        newTournamentViewModel.deleteEnrolledPlayers();

        // Then
        verify(repository, times(1)).deleteEnrolledPlayers();
    }

    @Test
    public void validNumberOfPlayers_threePlayers_returnsTrue() {
        // Given
        int numOfPlayers = 3;

        newTournamentViewModel.setNumberOfPlayers(numOfPlayers);

        // When - Then
        assertTrue(newTournamentViewModel.validNumberOfPlayers());
    }
}