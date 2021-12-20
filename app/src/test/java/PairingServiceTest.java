import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.collections.Sets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.southernsoft.tcgtournament.entity.Pairing;
import com.southernsoft.tcgtournament.entity.Standing;
import com.southernsoft.tcgtournament.pojo.PairingTuple;
import com.southernsoft.tcgtournament.service.MatchingService;
import com.southernsoft.tcgtournament.service.PairingService;
import com.southernsoft.tcgtournament.service.StandingService;
import com.southernsoft.tcgtournament.util.Lists;
import util.RandomIdGenerator;
import util.StandingScenario;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PairingServiceTest {
    @Mock private MatchingService mockedMatchingService;

    private PairingService pairingService;
    private RandomIdGenerator idGenerator;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        pairingService = new PairingService(mockedMatchingService);
        idGenerator = new RandomIdGenerator();
    }

    @Test
    public void generatePairings_differentMatchPoints_returnsPairingsAndOpponentsUpdated() {
        // Given
        int tournamentId = idGenerator.getRandomId();
        int roundId = idGenerator.getRandomId();
        int playerOneId = idGenerator.getRandomId();
        int playerTwoId = idGenerator.getRandomId();
        int playerThreeId = idGenerator.getRandomId();
        int playerFourId = idGenerator.getRandomId();
        int playerFiveId = idGenerator.getRandomId();
        int playerSixId = idGenerator.getRandomId();

        StandingScenario scenario = new StandingScenario();
        Standing firstStanding = scenario.createStanding().withPlayerId(playerTwoId).withMatchPoints(6).getStanding();
        Standing secondStanding = scenario.createStanding().withPlayerId(playerSixId).withMatchPoints(6).getStanding();
        Standing thirdStanding = scenario.createStanding().withPlayerId(playerFiveId).withMatchPoints(3).getStanding();
        Standing fourthStanding = scenario.createStanding().withPlayerId(playerOneId).withMatchPoints(3).getStanding();
        Standing fifthStanding = scenario.createStanding().withPlayerId(playerFourId).withMatchPoints(0).getStanding();
        Standing sixthStanding = scenario.createStanding().withPlayerId(playerThreeId).withMatchPoints(0).getStanding();

        Map<Integer, Standing> standingsByPlayerId = new HashMap<>();
        standingsByPlayerId.put(playerTwoId, firstStanding);
        standingsByPlayerId.put(playerSixId, secondStanding);
        standingsByPlayerId.put(playerFiveId, thirdStanding);
        standingsByPlayerId.put(playerOneId, fourthStanding);
        standingsByPlayerId.put(playerFourId, fifthStanding);
        standingsByPlayerId.put(playerThreeId, sixthStanding);

        Map<Integer, Set<Integer>> opponentsByPlayerId = new HashMap<>();
        opponentsByPlayerId.put(playerTwoId, Sets.newSet(playerOneId, playerThreeId, playerSixId));
        opponentsByPlayerId.put(playerSixId, Sets.newSet(playerOneId, playerTwoId, playerFiveId));
        opponentsByPlayerId.put(playerFiveId, Sets.newSet(playerThreeId, playerFourId, playerSixId));
        opponentsByPlayerId.put(playerOneId, Sets.newSet(playerTwoId, playerFourId, playerSixId));
        opponentsByPlayerId.put(playerFourId, Sets.newSet(playerOneId, playerThreeId, playerFiveId));
        opponentsByPlayerId.put(playerThreeId, Sets.newSet(playerTwoId, playerFourId, playerFiveId));

        MatchingService.PlayerPair firstPair = new MatchingService.PlayerPair(playerTwoId, playerSixId, 13);
        MatchingService.PlayerPair secondPair = new MatchingService.PlayerPair(playerFiveId, playerThreeId, 3);
        MatchingService.PlayerPair thirdPair = new MatchingService.PlayerPair(playerOneId, playerFourId, 3);

        List<MatchingService.PlayerPair> playerPairs = Lists.newArrayList(firstPair, secondPair, thirdPair);

        when(mockedMatchingService.getPlayerPairs()).thenReturn(playerPairs);

        // When
        List<Pairing> actualPairings = pairingService.generatePairings(tournamentId, roundId, standingsByPlayerId, opponentsByPlayerId);

        // Then
        Pairing pairing = actualPairings.get(0);
        assertEquals(tournamentId, pairing.tournamentId);
        assertEquals(roundId, pairing.roundId);
        assertEquals(playerTwoId, pairing.firstPlayerId);
        assertEquals(playerSixId, pairing.secondPlayerId);
        assertEquals(1, pairing.tableNumber);

        pairing = actualPairings.get(1);
        assertEquals(tournamentId, pairing.tournamentId);
        assertEquals(roundId, pairing.roundId);
        assertEquals(playerFiveId, pairing.firstPlayerId);
        assertEquals(playerThreeId, pairing.secondPlayerId);
        assertEquals(2, pairing.tableNumber);

        pairing = actualPairings.get(2);
        assertEquals(tournamentId, pairing.tournamentId);
        assertEquals(roundId, pairing.roundId);
        assertEquals(playerOneId, pairing.firstPlayerId);
        assertEquals(playerFourId, pairing.secondPlayerId);
        assertEquals(3, pairing.tableNumber);

        assertFalse(opponentsByPlayerId.get(playerTwoId).contains(playerSixId));
        assertFalse(opponentsByPlayerId.get(playerSixId).contains(playerTwoId));

        assertFalse(opponentsByPlayerId.get(playerFiveId).contains(playerThreeId));
        assertFalse(opponentsByPlayerId.get(playerThreeId).contains(playerFiveId));

        assertFalse(opponentsByPlayerId.get(playerOneId).contains(playerFourId));
        assertFalse(opponentsByPlayerId.get(playerFourId).contains(playerOneId));

        verify(mockedMatchingService, times(1)).createGraph();
        verify(mockedMatchingService, times(1)).addGraphVertices(anyList());
        verify(mockedMatchingService, times(18)).addEdge(anyInt(), anyInt());
        verify(mockedMatchingService, times(1)).getPlayerPairs();
    }

    @Test
    public void generatePairings_sameMatchPointsAndBye_returnsPairingsAndOpponentsUpdated() {
        // Given
        int tournamentId = idGenerator.getRandomId();
        int roundId = idGenerator.getRandomId();
        int byePlayerId = StandingService.BYE_PLAYER_ID;
        int playerOneId = idGenerator.getRandomId();
        int playerTwoId = idGenerator.getRandomId();
        int playerThreeId = idGenerator.getRandomId();
        int playerFourId = idGenerator.getRandomId();
        int playerFiveId = idGenerator.getRandomId();

        StandingScenario scenario = new StandingScenario();
        Standing firstStanding = scenario.createStanding().withPlayerId(playerOneId).withMatchPoints(0).getStanding();
        Standing secondStanding = scenario.createStanding().withPlayerId(playerTwoId).withMatchPoints(0).getStanding();
        Standing thirdStanding = scenario.createStanding().withPlayerId(playerThreeId).withMatchPoints(0).getStanding();
        Standing fourthStanding = scenario.createStanding().withPlayerId(playerFourId).withMatchPoints(0).getStanding();
        Standing fifthStanding = scenario.createStanding().withPlayerId(playerFiveId).withMatchPoints(0).getStanding();

        Map<Integer, Standing> standingsByPlayerId = new HashMap<>();
        standingsByPlayerId.put(playerOneId, firstStanding);
        standingsByPlayerId.put(playerTwoId, secondStanding);
        standingsByPlayerId.put(playerThreeId, thirdStanding);
        standingsByPlayerId.put(playerFourId, fourthStanding);
        standingsByPlayerId.put(playerFiveId, fifthStanding);

        Map<Integer, Set<Integer>> opponentsByPlayerId = new HashMap<>();
        opponentsByPlayerId.put(playerOneId, Sets.newSet(byePlayerId, playerTwoId, playerThreeId, playerFourId, playerFiveId));
        opponentsByPlayerId.put(playerTwoId, Sets.newSet(byePlayerId, playerOneId, playerThreeId, playerFourId, playerFiveId));
        opponentsByPlayerId.put(playerThreeId, Sets.newSet(byePlayerId, playerOneId, playerTwoId, playerFourId, playerFiveId));
        opponentsByPlayerId.put(playerFourId, Sets.newSet(byePlayerId, playerOneId, playerTwoId, playerThreeId, playerFiveId));
        opponentsByPlayerId.put(playerFiveId, Sets.newSet(byePlayerId, playerOneId, playerTwoId, playerThreeId, playerFourId));

        MatchingService.PlayerPair firstPair = new MatchingService.PlayerPair(playerFiveId, byePlayerId, -1);
        MatchingService.PlayerPair secondPair = new MatchingService.PlayerPair(playerTwoId, playerOneId, 0);
        MatchingService.PlayerPair thirdPair = new MatchingService.PlayerPair(playerFourId, playerThreeId, 0);

        List<MatchingService.PlayerPair> playerPairs = Lists.newArrayList(firstPair, secondPair, thirdPair);

        when(mockedMatchingService.getPlayerPairs()).thenReturn(playerPairs);

        // When
        List<Pairing> actualPairings = pairingService.generatePairings(tournamentId, roundId, standingsByPlayerId, opponentsByPlayerId);

        // Then
        Pairing pairing = actualPairings.get(0);
        assertEquals(tournamentId, pairing.tournamentId);
        assertEquals(roundId, pairing.roundId);
        assertEquals(playerTwoId, pairing.firstPlayerId);
        assertEquals(playerOneId, pairing.secondPlayerId);
        assertEquals(1, pairing.tableNumber);

        pairing = actualPairings.get(1);
        assertEquals(tournamentId, pairing.tournamentId);
        assertEquals(roundId, pairing.roundId);
        assertEquals(playerFourId, pairing.firstPlayerId);
        assertEquals(playerThreeId, pairing.secondPlayerId);
        assertEquals(2, pairing.tableNumber);

        pairing = actualPairings.get(2);
        assertEquals(tournamentId, pairing.tournamentId);
        assertEquals(roundId, pairing.roundId);
        assertEquals(playerFiveId, pairing.firstPlayerId);
        assertEquals(byePlayerId, pairing.secondPlayerId);
        assertEquals(3, pairing.tableNumber);

        assertFalse(opponentsByPlayerId.get(playerTwoId).contains(playerOneId));
        assertFalse(opponentsByPlayerId.get(playerOneId).contains(playerTwoId));

        assertFalse(opponentsByPlayerId.get(playerFourId).contains(playerThreeId));
        assertFalse(opponentsByPlayerId.get(playerThreeId).contains(playerFourId));

        assertFalse(opponentsByPlayerId.get(playerFiveId).contains(byePlayerId));

        verify(mockedMatchingService, times(1)).createGraph();
        verify(mockedMatchingService, times(1)).addGraphVertices(anyList());
        verify(mockedMatchingService, times(25)).addEdge(anyInt(), anyInt());
        verify(mockedMatchingService, times(1)).getPlayerPairs();
    }

    @Test
    public void generatePairings_DifferentMatchPoints_WeightsAreAdded() {
        // Given
        int tournamentId = idGenerator.getRandomId();
        int roundId = idGenerator.getRandomId();
        int playerOneId = idGenerator.getRandomId();
        int playerTwoId = idGenerator.getRandomId();

        StandingScenario scenario = new StandingScenario();
        Standing firstStanding = scenario.createStanding().withPlayerId(playerOneId).withMatchPoints(6).getStanding();
        Standing secondStanding = scenario.createStanding().withPlayerId(playerTwoId).withMatchPoints(3).getStanding();

        Map<Integer, Standing> standingsByPlayerId = new HashMap<>();
        standingsByPlayerId.put(playerOneId, firstStanding);
        standingsByPlayerId.put(playerTwoId, secondStanding);

        Map<Integer, Set<Integer>> opponentsByPlayerId = new HashMap<>();
        opponentsByPlayerId.put(playerOneId, Sets.newSet(playerTwoId));
        opponentsByPlayerId.put(playerTwoId, Sets.newSet(playerOneId));

        DefaultWeightedEdge edge = new DefaultWeightedEdge();
        when(mockedMatchingService.addEdge(playerOneId, playerTwoId)).thenReturn(edge);
        when(mockedMatchingService.addEdge(playerTwoId, playerOneId)).thenReturn(null);

        // When
        pairingService.generatePairings(tournamentId, roundId, standingsByPlayerId, opponentsByPlayerId);

        // Then
        int expectedWeight = 9;
        verify(mockedMatchingService, times(1)).createGraph();
        verify(mockedMatchingService, times(1)).addGraphVertices(anyList());
        verify(mockedMatchingService, times(2)).addEdge(anyInt(), anyInt());
        verify(mockedMatchingService, times(1)).setEdgeWeight(edge, expectedWeight);
    }

    @Test
        public void generatePairings_SameMatchPoints_ExtraPointAdded() {
        // Given
        int tournamentId = idGenerator.getRandomId();
        int roundId = idGenerator.getRandomId();
        int playerOneId = idGenerator.getRandomId();
        int playerTwoId = idGenerator.getRandomId();

        StandingScenario scenario = new StandingScenario();
        Standing firstStanding = scenario.createStanding().withPlayerId(playerOneId).withMatchPoints(5).getStanding();
        Standing secondStanding = scenario.createStanding().withPlayerId(playerTwoId).withMatchPoints(5).getStanding();

        Map<Integer, Standing> standingsByPlayerId = new HashMap<>();
        standingsByPlayerId.put(playerOneId, firstStanding);
        standingsByPlayerId.put(playerTwoId, secondStanding);

        Map<Integer, Set<Integer>> opponentsByPlayerId = new HashMap<>();
        opponentsByPlayerId.put(playerOneId, Sets.newSet(playerTwoId));
        opponentsByPlayerId.put(playerTwoId, Sets.newSet(playerOneId));

        DefaultWeightedEdge edge = new DefaultWeightedEdge();
        when(mockedMatchingService.addEdge(playerOneId, playerTwoId)).thenReturn(edge);
        when(mockedMatchingService.addEdge(playerTwoId, playerOneId)).thenReturn(null);

        // When
        pairingService.generatePairings(tournamentId, roundId, standingsByPlayerId, opponentsByPlayerId);

        // Then
        int expectedWeight = 11;
        verify(mockedMatchingService, times(1)).createGraph();
        verify(mockedMatchingService, times(1)).addGraphVertices(anyList());
        verify(mockedMatchingService, times(2)).addEdge(anyInt(), anyInt());
        verify(mockedMatchingService, times(1)).setEdgeWeight(edge, expectedWeight);
    }

    @Test
    public void generatePairings_illegalArgumentException_exceptionThrown() {
        // Given
        int tournamentId = idGenerator.getRandomId();
        int roundId = idGenerator.getRandomId();

        Map<Integer, Standing> standingsByPlayerId = new HashMap<>();
        Map<Integer, Set<Integer>> opponentsByPlayerId = new HashMap<>();

        when(mockedMatchingService.getPlayerPairs()).thenThrow(IllegalArgumentException.class);

        // When - Then
        assertThrows(IllegalArgumentException.class, () -> pairingService.generatePairings(tournamentId, roundId, standingsByPlayerId, opponentsByPlayerId));
    }

    @Test
    public void getPairingsFromTuples_severalTuples_pairingsReturned() {
        // Given
        int tournamentId = idGenerator.getRandomId();
        int roundId = idGenerator.getRandomId();
        int playerOneId = idGenerator.getRandomId();
        int playerTwoId = idGenerator.getRandomId();
        int playerThreeId = idGenerator.getRandomId();
        int playerFourId = idGenerator.getRandomId();
        int playerOneResult = 2;
        int playerTwoResult = 1;
        int playerThreeResult = 2;
        int playerFourResult = 0;
        int tableOne = 1;
        int tableTwo = 2;

        Pairing pairingOne = new Pairing();
        pairingOne.tournamentId = tournamentId;
        pairingOne.roundId = roundId;
        pairingOne.firstPlayerId = playerOneId;
        pairingOne.secondPlayerId = playerTwoId;
        pairingOne.firstPlayerResult = playerOneResult;
        pairingOne.secondPlayerResult = playerTwoResult;
        pairingOne.tableNumber = tableOne;

        Pairing pairingTwo = new Pairing();
        pairingTwo.tournamentId = tournamentId;
        pairingTwo.roundId = roundId;
        pairingTwo.firstPlayerId = playerThreeId;
        pairingTwo.secondPlayerId = playerFourId;
        pairingTwo.firstPlayerResult = playerThreeResult;
        pairingTwo.secondPlayerResult = playerFourResult;
        pairingTwo.tableNumber = tableTwo;

        PairingTuple tupleOne = new PairingTuple();
        tupleOne.pairing = pairingOne;

        PairingTuple tupleTwo = new PairingTuple();
        tupleTwo.pairing = pairingTwo;

        List<PairingTuple> tuples = Lists.newArrayList(tupleOne, tupleTwo);

        // When
        List<Pairing> pairings = pairingService.getPairingsFromTuples(tuples);

        // Then
        Pairing actualPairingOne = pairings.get(0);
        assertEquals(tournamentId, actualPairingOne.tournamentId);
        assertEquals(roundId, actualPairingOne.roundId);
        assertEquals(playerOneId, actualPairingOne.firstPlayerId);
        assertEquals(playerTwoId, actualPairingOne.secondPlayerId);
        assertEquals(playerOneResult, actualPairingOne.firstPlayerResult);
        assertEquals(playerTwoResult, actualPairingOne.secondPlayerResult);
        assertEquals(tableOne, actualPairingOne.tableNumber);

        Pairing actualPairingTwo = pairings.get(1);
        assertEquals(tournamentId, actualPairingTwo.tournamentId);
        assertEquals(roundId, actualPairingTwo.roundId);
        assertEquals(playerThreeId, actualPairingTwo.firstPlayerId);
        assertEquals(playerFourId, actualPairingTwo.secondPlayerId);
        assertEquals(playerThreeResult, actualPairingTwo.firstPlayerResult);
        assertEquals(playerFourResult, actualPairingTwo.secondPlayerResult);
        assertEquals(tableTwo, actualPairingTwo.tableNumber);
    }
}