package util;

import com.southernsoft.tcgtournament.entity.Standing;

public class StandingScenario {
    private Standing standing;

    public StandingScenario createStanding() {
        standing = new Standing();
        return this;
    }

    public StandingScenario withTournamentId(int tournamentId) {
        standing.tournamentId = tournamentId;
        return this;
    }

    public StandingScenario withPlayerId(int playerId) {
        standing.playerId = playerId;
        return this;
    }

    public StandingScenario withBye(boolean hasBye) {
        standing.hasBye = hasBye;
        return this;
    }

    public StandingScenario withRoundsPlayed(int roundsPlayed) {
        standing.roundsPlayed = roundsPlayed;
        return this;
    }

    public StandingScenario withGamesPlayed(int gamesPlayed) {
        standing.gamesPlayed = gamesPlayed;
        return this;
    }

    public StandingScenario withMatchPoints(int matchPoints) {
        standing.matchPoints = matchPoints;
        return this;
    }

    public StandingScenario withGamePoints(int gamePoints) {
        standing.gamePoints = gamePoints;
        return this;
    }

    public StandingScenario withMatchWinPercentage(int matchWinPercentage) {
        standing.matchWinPercentage = matchWinPercentage;
        return this;
    }

    public StandingScenario withGameWinPercentage(int gameWinPercentage) {
        standing.gameWinPercentage = gameWinPercentage;
        return this;
    }

    public StandingScenario withOppMatchWinPercentage(int oppMatchWinPercentage) {
        standing.oppMatchWinPercentage = oppMatchWinPercentage;
        return this;
    }

    public StandingScenario withOppGameWinPercentage(int oppGameWinPercentage) {
        standing.oppGameWinPercentage = oppGameWinPercentage;
        return this;
    }

    public Standing getStanding() {
        return standing;
    }
}