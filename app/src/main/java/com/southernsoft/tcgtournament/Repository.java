package com.southernsoft.tcgtournament;

import android.database.sqlite.SQLiteConstraintException;

import androidx.collection.ArraySet;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.southernsoft.tcgtournament.dao.PairingDao;
import com.southernsoft.tcgtournament.dao.PlayerDao;
import com.southernsoft.tcgtournament.dao.RoundDao;
import com.southernsoft.tcgtournament.dao.StandingDao;
import com.southernsoft.tcgtournament.dao.TournamentDao;
import com.southernsoft.tcgtournament.entity.Pairing;
import com.southernsoft.tcgtournament.entity.Player;
import com.southernsoft.tcgtournament.entity.Round;
import com.southernsoft.tcgtournament.entity.Standing;
import com.southernsoft.tcgtournament.entity.Tournament;
import com.southernsoft.tcgtournament.pojo.PairingTuple;
import com.southernsoft.tcgtournament.pojo.StandingRow;
import com.southernsoft.tcgtournament.pojo.TournamentAndRound;
import com.southernsoft.tcgtournament.service.PairingService;
import com.southernsoft.tcgtournament.service.StandingService;
import com.southernsoft.tcgtournament.util.XmlUtils;

@Singleton
public class Repository {
    private final TournamentDao tournamentDao;
    private final RoundDao roundDao;
    private final PlayerDao playerDao;
    private final PairingDao pairingDao;
    private final StandingDao standingDao;
    private final PairingService pairingService;
    private final StandingService standingService;
    private final XmlUtils xmlUtils;

    @Inject
    public Repository (LocalDatabase database, PairingService pairingService, StandingService standingService, XmlUtils xmlUtils) {
        this.pairingService = pairingService;
        this.standingService = standingService;
        this.xmlUtils = xmlUtils;
        tournamentDao = database.tournamentDao();
        roundDao = database.roundDao();
        playerDao = database.playerDao();
        pairingDao = database.pairingDao();
        standingDao = database.standingDao();
    }

    public void saveEnrolledPlayers(List<Player> enrolledPlayers) {
        xmlUtils.serializeEnrolledPlayers(enrolledPlayers);
    }

    public Set<Integer> getEnrolledPlayerIds() {
        return xmlUtils.parseEnrolledPlayers();
    }

    public void saveOpponentsForTournament(int tournamentId, Map<Integer, Set<Integer>> opponentsByPlayerId) {
        xmlUtils.serializeOpponents(tournamentId, opponentsByPlayerId);
    }

    public Map<Integer, Set<Integer>> getOpponentsForTournament(int tournamentId) {
        return xmlUtils.parseOpponents(tournamentId);
    }

    public void deleteEnrolledPlayers() {
        xmlUtils.deleteEnrolledPlayers();
    }

    public void deleteTournamentFile(int tournamentId) {
        xmlUtils.deleteTournament(tournamentId);
    }

    public Tournament createTournament(int numberPlayers, int numberRounds, int roundTime) {
        Tournament tournament = new Tournament();
        tournament.numberPlayers = numberPlayers;
        tournament.numberRounds = numberRounds;
        tournament.roundTime = roundTime;
        tournament.date = new Date();
        tournament.id = (int) insertTournament(tournament);
        return tournament;
    }

    public Round createRound(int tournamentId, int roundNumber, int roundTime) {
        Round round = new Round();
        round.tournamentId = tournamentId;
        round.roundNumber = roundNumber;
        round.roundTime = roundTime;
        round.id = (int) insertRound(round);
        return round;
    }

    public List<Standing> createNewStandings(int tournamentId, int roundId, Set<Integer> enrolledPlayersIds) {
        List<Standing> standings = standingService.createInitialStandings(tournamentId, roundId, enrolledPlayersIds);
        insertStandings(standings);
        return standings;
    }

    public void createNewPairings(int tournamentId, int roundId, Map<Integer, Standing> standingsByPlayerId) {
        Map<Integer, Set<Integer>> opponentsByPlayerId = getOpponentsForTournament(tournamentId);
        List<Pairing> pairings = pairingService.generatePairings(tournamentId, roundId, standingsByPlayerId, opponentsByPlayerId);

        insertPairings(pairings);
        saveOpponentsForTournament(tournamentId, opponentsByPlayerId);
    }

    public void initializeOpponents(int tournamentId, Set<Integer> playerIds) {
        boolean oddNumberOfPlayers = playerIds.size() % 2 != 0;
        Map<Integer, Set<Integer>> opponentsByPlayerId = new HashMap<>();
        for (Integer playerId : playerIds) {
            Set<Integer> opponentIds = new ArraySet<>(playerIds);
            if (oddNumberOfPlayers)
                opponentIds.add(StandingService.BYE_PLAYER_ID);
            opponentIds.remove(playerId);
            opponentsByPlayerId.put(playerId, opponentIds);
        }
        saveOpponentsForTournament(tournamentId, opponentsByPlayerId);
    }

    public Tournament getTournament(int tournamentId) {
        Tournament tournament = new Tournament();
        try {
            tournament = LocalDatabase.databaseWriteExecutor.submit(() -> tournamentDao.getTournamentById(tournamentId)).get();
        } catch (ExecutionException | InterruptedException e) {}
        return tournament;
    }

    public Round getRound(int roundId) {
        Round round = new Round();
        try {
            round = LocalDatabase.databaseWriteExecutor.submit(() -> roundDao.getRoundById(roundId)).get();
        } catch (ExecutionException | InterruptedException e) {}
        return round;
    }

    public LiveData<List<TournamentAndRound>> getTournamentsWithLastRound() {
        return roundDao.getTournamentsWithLastRound();
    }

    public void insertPlayer(Player player) {
        LocalDatabase.databaseWriteExecutor.execute(() -> playerDao.insert(player));
    }

    public LiveData<List<Player>> getAllPlayers() {
        return playerDao.getAllPlayers();
    }

    public void deleteTournament(int tournamentId) {
        LocalDatabase.databaseWriteExecutor.execute(() -> tournamentDao.deleteTournament(tournamentId));
    }

    public List<Player> getPlayersById(Set<Integer> playerIds) {
        List<Player> playersList = new ArrayList<>();
        try {
            playersList = LocalDatabase.databaseWriteExecutor.submit(() -> playerDao.getPlayersById(playerIds)).get();
        } catch (ExecutionException | InterruptedException e) {}
        return playersList;
    }

    public boolean deletePlayers(List<Player> players) {
        boolean result = true;
        try {
            LocalDatabase.databaseWriteExecutor.submit(() -> playerDao.delete(players)).get();
        } catch (ExecutionException | InterruptedException | SQLiteConstraintException e) {
            result = false;
        }
        return result;
    }

    public LiveData<List<PairingTuple>> getLastPairingsForTournament(int tournamentId) {
        return pairingDao.getLastPairingsForTournament(tournamentId);
    }

    public LiveData<List<StandingRow>> getLastStandingsForTournament(int tournamentId) {
        return standingDao.getLastStandingsForTournament(tournamentId);
    }

    public long insertTournament(final Tournament tournament) {
        long tournamentId = -1;
        try {
            tournamentId = LocalDatabase.databaseWriteExecutor.submit(() -> tournamentDao.insert(tournament)).get();
        } catch (ExecutionException | InterruptedException e) {}
        return tournamentId;
    }

    public long insertRound(final Round round) {
        long roundId = -1;
        try {
            roundId = LocalDatabase.databaseWriteExecutor.submit(() -> roundDao.insert(round)).get();
        } catch (ExecutionException | InterruptedException e) {}
        return roundId;
    }

    public void insertPairings(final List<Pairing> pairings) {
        LocalDatabase.databaseWriteExecutor.execute(() -> pairingDao.insert(pairings));
    }

    public void insertStandings(final List<Standing> standings) {
        LocalDatabase.databaseWriteExecutor.execute(() -> standingDao.insert(standings));
    }

    public void updateRound(final Round round) {
        LocalDatabase.databaseWriteExecutor.execute(() -> roundDao.update(round));
    }

    public void updatePairings(final List<Pairing> pairings) {
        LocalDatabase.databaseWriteExecutor.execute(() -> pairingDao.update(pairings));
    }
}