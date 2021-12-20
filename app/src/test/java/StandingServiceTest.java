import androidx.collection.ArraySet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.southernsoft.tcgtournament.entity.Pairing;
import com.southernsoft.tcgtournament.entity.Standing;

import com.southernsoft.tcgtournament.pojo.StandingRow;
import com.southernsoft.tcgtournament.service.StandingService;
import com.southernsoft.tcgtournament.util.Lists;
import util.RandomIdGenerator;
import util.StandingScenario;

import static org.junit.Assert.assertEquals;

public class StandingServiceTest {
    private StandingService standingService;
    private RandomIdGenerator idGenerator;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        standingService = new StandingService();
        idGenerator = new RandomIdGenerator();
    }

    @Test
    public void createInitialStandings_dataProvided_returnsStandings() {
        // Given
        int tournamentId = idGenerator.getRandomId();
        int roundId = idGenerator.getRandomId();
        int playerOneId = idGenerator.getRandomId();
        int playerTwoId = playerOneId + 5;
        Set<Integer> enrolledPlayersIds = new ArraySet<>();

        enrolledPlayersIds.add(playerOneId);
        enrolledPlayersIds.add(playerTwoId);

        // When
        List<Standing> standings = standingService.createInitialStandings(tournamentId, roundId, enrolledPlayersIds);

        // Then
        Standing firstStanding = standings.get(0);
        assertEquals(tournamentId, firstStanding.tournamentId);
        assertEquals(roundId, firstStanding.roundId);
        assertEquals(playerOneId, firstStanding.playerId);

        Standing secondStanding = standings.get(1);
        assertEquals(tournamentId, secondStanding.tournamentId);
        assertEquals(roundId, secondStanding.roundId);
        assertEquals(playerTwoId, secondStanding.playerId);
    }

    @Test
    public void mapStandingsByPlayerId_listProvided_returnsMap() {
        // Given
        int playerOneId = idGenerator.getRandomId();
        int playerTwoId = idGenerator.getRandomId();
        Standing firstStanding = new Standing();
        Standing secondStanding = new Standing();

        firstStanding.playerId = playerOneId;
        secondStanding.playerId = playerTwoId;

        // When
        Map<Integer, Standing> standingsByPlayerId = standingService.mapStandingsByPlayerId(Lists.newArrayList(firstStanding, secondStanding));

        // Then
        assertEquals(firstStanding, standingsByPlayerId.get(playerOneId));
        assertEquals(secondStanding, standingsByPlayerId.get(playerTwoId));
    }

    @Test
    public void mapStandingRowsByPlayerId_listProvided_returnsMap() {
        // Given
        int playerOneId = idGenerator.getRandomId();
        int playerTwoId = idGenerator.getRandomId();
        Standing firstStanding = new Standing();
        Standing secondStanding = new Standing();
        StandingRow firstStandingRow = new StandingRow();
        StandingRow secondStandingRow = new StandingRow();

        firstStanding.playerId = playerOneId;
        secondStanding.playerId = playerTwoId;
        firstStandingRow.standing = firstStanding;
        secondStandingRow.standing = secondStanding;

        // When
        Map<Integer, Standing> standingsByPlayerId = standingService.mapStandingRowsByPlayerId(Lists.newArrayList(firstStandingRow, secondStandingRow));

        // Then
        assertEquals(firstStanding, standingsByPlayerId.get(playerOneId));
        assertEquals(secondStanding, standingsByPlayerId.get(playerTwoId));
    }

    @Test
    public void createStandingsForNextRound_listAndRoundProvided_returnsStandings() {
        // Given
        int tournamentId = idGenerator.getRandomId();
        int roundId = idGenerator.getRandomId();
        int playerId = idGenerator.getRandomId();
        int roundsPlayed = idGenerator.getRandomId();
        int gamesPlayed = idGenerator.getRandomId();
        int matchPoints = idGenerator.getRandomId();
        int gamePoints = idGenerator.getRandomId();
        int matchWinPercentage = idGenerator.getRandomId();
        int gameWinPercentage = idGenerator.getRandomId();
        int oppMatchWinPercentage = idGenerator.getRandomId();
        int oppGameWinPercentage = idGenerator.getRandomId();

        Standing standing = new StandingScenario().createStanding().withTournamentId(tournamentId).withPlayerId(playerId)
                .withRoundsPlayed(roundsPlayed).withGamesPlayed(gamesPlayed).withMatchPoints(matchPoints).withGamePoints(gamePoints)
                .withMatchWinPercentage(matchWinPercentage).withGameWinPercentage(gameWinPercentage).withOppMatchWinPercentage(oppMatchWinPercentage)
                .withOppGameWinPercentage(oppGameWinPercentage).getStanding();

        StandingRow row = new StandingRow();
        row.standing = standing;

        // When
        List<Standing> standings = standingService.createStandingsForNextRound(Lists.newArrayList(row), roundId);

        // Then
        Standing newStanding = standings.get(0);
        assertEquals(tournamentId, newStanding.tournamentId);
        assertEquals(roundId, newStanding.roundId);
        assertEquals(playerId, newStanding.playerId);
        assertEquals(roundsPlayed, newStanding.roundsPlayed);
        assertEquals(gamesPlayed, newStanding.gamesPlayed);
        assertEquals(matchPoints, newStanding.matchPoints);
        assertEquals(gamePoints, newStanding.gamePoints);
        assertEquals(matchWinPercentage, newStanding.matchWinPercentage);
        assertEquals(gameWinPercentage, newStanding.gameWinPercentage);
        assertEquals(oppMatchWinPercentage, newStanding.oppMatchWinPercentage);
        assertEquals(oppGameWinPercentage, newStanding.oppGameWinPercentage);
    }

    @Test
    public void updateStandingsPoints_standingsAndPairingsProvided_pointsUpdated() {
        // Given
        int playerOneId = idGenerator.getRandomId();
        int playerTwoId = idGenerator.getRandomId();

        Pairing pairing = new Pairing();
        pairing.firstPlayerId = playerOneId;
        pairing.secondPlayerId = playerTwoId;
        pairing.firstPlayerResult = 2;
        pairing.secondPlayerResult = 1;

        Standing playerOneStanding = new StandingScenario().createStanding().withPlayerId(playerOneId).withRoundsPlayed(0)
                .withGamesPlayed(0).withMatchPoints(0).withGamePoints(0).withMatchWinPercentage(0).withGameWinPercentage(0).getStanding();

        Standing playerTwoStanding = new StandingScenario().createStanding().withPlayerId(playerTwoId).withRoundsPlayed(0)
                .withGamesPlayed(0).withMatchPoints(0).withGamePoints(0).withMatchWinPercentage(0).withGameWinPercentage(0).getStanding();

        Map<Integer, Standing> standingsByPlayerId = standingService.mapStandingsByPlayerId(Lists.newArrayList(playerOneStanding, playerTwoStanding));

        // When
        standingService.updateStandingsPoints(standingsByPlayerId, Lists.newArrayList(pairing));

        // Then
        assertEquals(3, playerOneStanding.matchPoints);
        assertEquals(6, playerOneStanding.gamePoints);
        assertEquals(1, playerOneStanding.roundsPlayed);
        assertEquals(3, playerOneStanding.gamesPlayed);
        assertEquals(10000, playerOneStanding.matchWinPercentage);
        assertEquals(6667, playerOneStanding.gameWinPercentage);

        assertEquals(0, playerTwoStanding.matchPoints);
        assertEquals(3, playerTwoStanding.gamePoints);
        assertEquals(1, playerTwoStanding.roundsPlayed);
        assertEquals(3, playerTwoStanding.gamesPlayed);
        assertEquals(3333, playerTwoStanding.matchWinPercentage);
        assertEquals(3333, playerTwoStanding.gameWinPercentage);
    }

    @Test
    public void updateStandingsTiebreakers_standingsAndOpponentsProvided_pointsUpdated() {
        // Given
        int playerOneId = idGenerator.getRandomId();
        int playerTwoId = idGenerator.getRandomId();
        Map<Integer, Set<Integer>> opponentsByPlayerId = new HashMap<>();

        Standing playerOneStanding = new StandingScenario().createStanding().withPlayerId(playerOneId)
                .withMatchWinPercentage(10000).withGameWinPercentage(6667).getStanding();

        Standing playerTwoStanding = new StandingScenario().createStanding().withPlayerId(playerTwoId)
                .withMatchWinPercentage(3333).withGameWinPercentage(3333).getStanding();

        Map<Integer, Standing> standingsByPlayerId = standingService.mapStandingsByPlayerId(Lists.newArrayList(playerOneStanding, playerTwoStanding));

        opponentsByPlayerId.put(playerOneId, new ArraySet<>());
        opponentsByPlayerId.put(playerTwoId, new ArraySet<>());

        // When
        standingService.updateStandingsTiebreakers(standingsByPlayerId, opponentsByPlayerId);

        // Then
        assertEquals(3333, playerOneStanding.oppMatchWinPercentage);
        assertEquals(3333, playerOneStanding.oppGameWinPercentage);

        assertEquals(10000, playerTwoStanding.oppMatchWinPercentage);
        assertEquals(6667, playerTwoStanding.oppGameWinPercentage);
    }
}