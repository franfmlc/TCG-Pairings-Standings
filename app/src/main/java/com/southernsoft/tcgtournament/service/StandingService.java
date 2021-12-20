package com.southernsoft.tcgtournament.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import com.southernsoft.tcgtournament.entity.Pairing;
import com.southernsoft.tcgtournament.entity.Standing;
import com.southernsoft.tcgtournament.pojo.StandingRow;

public class StandingService {
    public static final int BYE_PLAYER_ID = 1;
    private final int MIN_PERCENTAGE = 3333;
    private final int MATCH_WIN_POINTS = 3;
    private final int MATCH_DRAW_POINTS = 1;
    private final int GAME_WIN_POINTS = 3;
    private final int GAME_DRAW_POINTS = 1;

    @Inject
    public StandingService() {}

    public List<Standing> createInitialStandings(int tournamentId, int roundId, Set<Integer> enrolledPlayerIds) {
        List<Standing> standings = new ArrayList<>();
        for (int playerId : enrolledPlayerIds) {
            Standing standing = new Standing();
            standing.tournamentId = tournamentId;
            standing.roundId = roundId;
            standing.playerId = playerId;
            standings.add(standing);
        }
        return standings;
    }

    public Map<Integer, Standing> mapStandingsByPlayerId(List<Standing> standings) {
        Map<Integer, Standing> standingsByPlayerId = new HashMap<>();
        for (Standing standing : standings)
            standingsByPlayerId.put(standing.playerId, standing);
        return standingsByPlayerId;
    }

    public Map<Integer, Standing> mapStandingRowsByPlayerId(List<StandingRow> standings) {
        Map<Integer, Standing> standingsByPlayerId = new HashMap<>();
        for (StandingRow standingRow : standings)
            standingsByPlayerId.put(standingRow.standing.playerId, standingRow.standing);
        return standingsByPlayerId;
    }

    public List<Standing> createStandingsForNextRound(List<StandingRow> standingRows, int roundId) {
        List<Standing> newStandings = new ArrayList<>();

        for (StandingRow standingRow : standingRows) {
            Standing standing = copyStandingFields(standingRow.standing);
            standing.roundId = roundId;
            newStandings.add(standing);
        }
        return newStandings;
    }

    public void updateStandingsPoints(Map<Integer, Standing> standingsByPlayerId, List<Pairing> pairings) {
        for (Pairing pairing : pairings) {
            int firstPlayerResult = pairing.firstPlayerResult;
            int secondPlayerResult = pairing.secondPlayerResult;
            int gamesPlayed = getGamesPlayed(firstPlayerResult, secondPlayerResult);

            updatePoints(standingsByPlayerId.get(pairing.firstPlayerId), firstPlayerResult, secondPlayerResult, gamesPlayed);
            if (pairing.secondPlayerId != BYE_PLAYER_ID)
                updatePoints(standingsByPlayerId.get(pairing.secondPlayerId), secondPlayerResult, firstPlayerResult, gamesPlayed);
        }
    }

    public void updateStandingsTiebreakers(Map<Integer, Standing> standingsByPlayerId, Map<Integer, Set<Integer>> opponentsByPlayerId) {
        for (Integer playerId : standingsByPlayerId.keySet()) {
            Standing standing = standingsByPlayerId.get(playerId);
            Set<Integer> opponentIds = new HashSet<>(standingsByPlayerId.keySet());
            Set<Integer> unmatchedOpponents = opponentsByPlayerId.get(playerId);
            opponentIds.removeAll(unmatchedOpponents);
            opponentIds.remove(playerId);

            BigDecimal oppMatchWinPercentage = new BigDecimal(0);
            BigDecimal oppGameWinPercentage = new BigDecimal(0);

            for (Integer opponentId : opponentIds) {
                if (opponentId != BYE_PLAYER_ID) {
                    Standing oppStanding = standingsByPlayerId.get(opponentId);
                    oppMatchWinPercentage = oppMatchWinPercentage.add(new BigDecimal(oppStanding.matchWinPercentage));
                    oppGameWinPercentage = oppGameWinPercentage.add(new BigDecimal(oppStanding.gameWinPercentage));
                }
            }

            BigDecimal numberOfOpponents = new BigDecimal(opponentIds.size());

            if (numberOfOpponents.intValue() > 0) {
                standing.oppMatchWinPercentage = oppMatchWinPercentage.divide(numberOfOpponents, BigDecimal.ROUND_HALF_UP).intValue();
                standing.oppGameWinPercentage = oppGameWinPercentage.divide(numberOfOpponents, BigDecimal.ROUND_HALF_UP).intValue();
            }
        }
    }

    public int getGamesPlayed(int playerScore, int opponentScore) {
        int total = playerScore + opponentScore;
        if (playerScore < 2 && opponentScore < 2)
            total++;
        return total;
    }

    public int getMatchPoints(int playerScore, int opponentScore) {
        if (playerScore > opponentScore)
            return MATCH_WIN_POINTS;
        if (playerScore == opponentScore)
            return MATCH_DRAW_POINTS;
        return 0;
    }

    public int getGamePoints(int playerScore, int opponentScore) {
        int gamePoints = 0;
        if (playerScore > 0)
            gamePoints = playerScore * GAME_WIN_POINTS;
        if (playerScore < 2 && opponentScore < 2)
            gamePoints += GAME_DRAW_POINTS;
        return gamePoints;
    }

    public int getWinPercentage(int points, int games) {
        BigDecimal totalPoints = new BigDecimal(games * 3);
        BigDecimal playerPoints = new BigDecimal(points);
        int percentage = playerPoints.divide(totalPoints, 4, RoundingMode.HALF_UP).movePointRight(4).intValue();
        return Math.max(percentage, MIN_PERCENTAGE);
    }

    private void updatePoints(Standing standing, int playerScore, int opponentScore, int gamesPlayed) {
        standing.matchPoints += getMatchPoints(playerScore, opponentScore);
        standing.gamePoints += getGamePoints(playerScore, opponentScore);
        standing.gamesPlayed += gamesPlayed;
        standing.roundsPlayed++;
        standing.matchWinPercentage = getWinPercentage(standing.matchPoints, standing.roundsPlayed);
        standing.gameWinPercentage = getWinPercentage(standing.gamePoints, standing.gamesPlayed);
    }

    private Standing copyStandingFields(Standing originalStanding) {
        Standing copy = new Standing();
        copy.tournamentId = originalStanding.tournamentId;
        copy.playerId = originalStanding.playerId;
        copy.roundsPlayed = originalStanding.roundsPlayed;
        copy.gamesPlayed = originalStanding.gamesPlayed;
        copy.matchPoints = originalStanding.matchPoints;
        copy.gamePoints = originalStanding.gamePoints;
        copy.matchWinPercentage = originalStanding.matchWinPercentage;
        copy.gameWinPercentage = originalStanding.gameWinPercentage;
        copy.oppMatchWinPercentage = originalStanding.oppMatchWinPercentage;
        copy.oppGameWinPercentage = originalStanding.oppGameWinPercentage;
        return copy;
    }
}