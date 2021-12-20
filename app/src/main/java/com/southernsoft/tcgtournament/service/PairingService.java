package com.southernsoft.tcgtournament.service;

import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import com.southernsoft.tcgtournament.entity.Pairing;
import com.southernsoft.tcgtournament.entity.Standing;
import com.southernsoft.tcgtournament.pojo.PairingTuple;

import static com.southernsoft.tcgtournament.service.StandingService.BYE_PLAYER_ID;

public class PairingService {
    private final MatchingService matchingService;
    private final int BYE_PENALTY = -4; // This penalty ensures that Bye is the last player to be paired.

    @Inject
    public PairingService(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    public List<Pairing> generatePairings(int tournamentId, int roundId, Map<Integer, Standing> standingsByPlayerId,  Map<Integer, Set<Integer>> opponentsByPlayerId) {
        boolean oddNumberOfPlayers = standingsByPlayerId.size() % 2 != 0;
        List<Integer> vertices = new ArrayList<>(standingsByPlayerId.keySet());

        if (oddNumberOfPlayers)
            vertices.add(BYE_PLAYER_ID);

        Collections.shuffle(vertices);

        matchingService.createGraph();
        matchingService.addGraphVertices(vertices);

        for (Integer playerId : standingsByPlayerId.keySet()) {
            int playerMatchPoints = standingsByPlayerId.get(playerId).matchPoints;
            for (Integer opponentId : opponentsByPlayerId.get(playerId)) {
                int opponentMatchPoints;

                if (opponentId == BYE_PLAYER_ID)
                    opponentMatchPoints = BYE_PENALTY;
                else
                    opponentMatchPoints = standingsByPlayerId.get(opponentId).matchPoints;

                DefaultWeightedEdge edge = matchingService.addEdge(playerId, opponentId);

                if (edge != null) {
                    int weight = playerMatchPoints + opponentMatchPoints;

                    if (playerMatchPoints != 0 && playerMatchPoints == opponentMatchPoints)
                        weight++;

                    matchingService.setEdgeWeight(edge, weight);
                }
            }
        }

        List<MatchingService.PlayerPair> playerPairs = matchingService.getPlayerPairs();
        return createPairingsFromPairs(tournamentId, roundId, playerPairs, opponentsByPlayerId);
    }

    private List<Pairing> createPairingsFromPairs(int tournamentId, int roundId, List<MatchingService.PlayerPair> playerPairs, Map<Integer, Set<Integer>> opponentsByPlayerId) {
        Collections.sort(playerPairs, Collections.reverseOrder());

        List<Pairing> pairings = new ArrayList<>();
        int tableNumber = 1;

        for (MatchingService.PlayerPair pair : playerPairs) {
            int playerId = pair.playerId;
            int opponentId = pair.opponentId;

            opponentsByPlayerId.get(playerId).remove(opponentId);

            if (opponentId != BYE_PLAYER_ID) {
                opponentsByPlayerId.get(opponentId).remove(playerId);
                pairings.add(createPairing(tournamentId, roundId, playerId, opponentId, 0, 0, tableNumber++));
            } else {
                pairings.add(createPairing(tournamentId, roundId, playerId, BYE_PLAYER_ID, 2, 0, tableNumber++));
            }
        }
        return pairings;
    }

    public List<Pairing> getPairingsFromTuples(List<PairingTuple> tuples) {
        List<Pairing> pairings = new ArrayList<>();
        for (PairingTuple tuple : tuples)
            pairings.add(tuple.pairing);
        return pairings;
    }

    private Pairing createPairing(int tournamentId, int roundId, int firstPlayerId, int secondPlayerId, int firstPlayerResult, int secondPlayerResult, int tableNumber) {
        Pairing pairing = new Pairing();
        pairing.tournamentId = tournamentId;
        pairing.roundId = roundId;
        pairing.firstPlayerId = firstPlayerId;
        pairing.secondPlayerId = secondPlayerId;
        pairing.firstPlayerResult = firstPlayerResult;
        pairing.secondPlayerResult = secondPlayerResult;
        pairing.tableNumber = tableNumber;
        return pairing;
    }
}