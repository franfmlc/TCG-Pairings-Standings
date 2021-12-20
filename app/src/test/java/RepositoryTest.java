import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.collection.ArraySet;

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

import com.southernsoft.tcgtournament.LocalDatabase;
import com.southernsoft.tcgtournament.Repository;
import com.southernsoft.tcgtournament.dao.PairingDao;
import com.southernsoft.tcgtournament.dao.RoundDao;
import com.southernsoft.tcgtournament.dao.StandingDao;
import com.southernsoft.tcgtournament.dao.TournamentDao;
import com.southernsoft.tcgtournament.entity.Pairing;
import com.southernsoft.tcgtournament.entity.Round;
import com.southernsoft.tcgtournament.entity.Standing;
import com.southernsoft.tcgtournament.entity.Tournament;
import com.southernsoft.tcgtournament.service.PairingService;
import com.southernsoft.tcgtournament.service.StandingService;
import com.southernsoft.tcgtournament.util.XmlUtils;
import util.RandomIdGenerator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RepositoryTest {
    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Mock private LocalDatabase localDatabase;
    @Mock private PairingService pairingService;
    @Mock private StandingService standingService;
    @Mock private XmlUtils xmlUtils;
    @Mock private TournamentDao tournamentDao;
    @Mock private RoundDao roundDao;
    @Mock private StandingDao standingDao;
    @Mock private PairingDao pairingDao;

    private Repository repository;
    private RandomIdGenerator idGenerator;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        when(localDatabase.tournamentDao()).thenReturn(tournamentDao);
        when(localDatabase.roundDao()).thenReturn(roundDao);
        when(localDatabase.standingDao()).thenReturn(standingDao);
        when(localDatabase.pairingDao()).thenReturn(pairingDao);

        repository = new Repository(localDatabase, pairingService, standingService, xmlUtils);
        idGenerator = new RandomIdGenerator();
    }

    @Test
    public void createTournament_dataProvided_returnsTournament() {
        // Given
        int numOfPlayers = idGenerator.getRandomId();
        int numOfRounds = idGenerator.getRandomId();
        int roundTime = idGenerator.getRandomId();
        long fakeTournamentId = idGenerator.getRandomId();

        when(tournamentDao.insert(any(Tournament.class))).thenReturn(fakeTournamentId);

        // When
        Tournament actualTournament = repository.createTournament(numOfPlayers, numOfRounds, roundTime);

        // Then
        assertEquals(fakeTournamentId, actualTournament.id);
        assertEquals(numOfPlayers, actualTournament.numberPlayers);
        assertEquals(numOfRounds, actualTournament.numberRounds);
        assertEquals(roundTime, actualTournament.roundTime);
        assertNotNull(actualTournament.date);
        verify(tournamentDao, times(1)).insert(any(Tournament.class));
    }

    @Test
    public void createRound_dataProvided_returnsRound() {
        // Given
        int tournamendId = idGenerator.getRandomId();
        int roundNumber = idGenerator.getRandomId();
        int roundTime = idGenerator.getRandomId();
        long fakeRoundId = idGenerator.getRandomId();

        when(roundDao.insert(any(Round.class))).thenReturn(fakeRoundId);

        // When
        Round actualRound = repository.createRound(tournamendId, roundNumber, roundTime);

        // Then
        assertEquals(fakeRoundId, actualRound.id);
        assertEquals(roundNumber, actualRound.roundNumber);
        assertEquals(roundTime, actualRound.roundTime);
        verify(roundDao, times(1)).insert(any(Round.class));
    }

    @Test
    public void createNewStandings_dataProvided_returnsStandings() {
        // Given
        int tournamentId = idGenerator.getRandomId();
        int roundId = idGenerator.getRandomId();
        Set<Integer> enrolledPlayers = new ArraySet<>();
        List<Standing> standings = new ArrayList<>();

        when(standingService.createInitialStandings(tournamentId, roundId, enrolledPlayers)).thenReturn(standings);

        // When
        List<Standing> actualStandings = repository.createNewStandings(tournamentId, roundId, enrolledPlayers);

        //Then
        assertEquals(standings, actualStandings);
        verify(standingService, times(1)).createInitialStandings(tournamentId, roundId, enrolledPlayers);
        verify(standingDao, times(1)).insert(standings);
    }

    @Test
    public void createNewPairings_dataProvided_servicesCalled() {
        // Given
        int tournamentId = idGenerator.getRandomId();
        int roundId = idGenerator.getRandomId();
        Map<Integer, Standing> standingsByPlayerId = new HashMap<>();
        Map<Integer, Set<Integer>> opponentsByPlayerId = new HashMap<>();
        List<Pairing> pairings = new ArrayList<>();

        when(xmlUtils.parseOpponents(tournamentId)).thenReturn(opponentsByPlayerId);
        when(pairingService.generatePairings(tournamentId, roundId, standingsByPlayerId, opponentsByPlayerId)).thenReturn(pairings);

        // When
        repository.createNewPairings(tournamentId, roundId, standingsByPlayerId);

        //Then
        verify(xmlUtils, times(1)).parseOpponents(tournamentId);
        verify(xmlUtils, times(1)).serializeOpponents(tournamentId, opponentsByPlayerId);
        verify(pairingService, times(1)).generatePairings(tournamentId, roundId, standingsByPlayerId, opponentsByPlayerId);
        verify(pairingDao, times(1)).insert(pairings);
    }

    @Test
    public void initializeOpponents_dataProvided_servicesCalled() {
        // Given
        int tournamentId = idGenerator.getRandomId();
        Set<Integer> playerIds = new ArraySet<>();

        // When
        repository.initializeOpponents(tournamentId, playerIds);

        // Then
        verify(xmlUtils, times(1)).serializeOpponents(anyInt(), anyMap());
    }
}