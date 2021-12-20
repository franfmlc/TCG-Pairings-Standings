package com.southernsoft.tcgtournament.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import com.southernsoft.tcgtournament.entity.Round;
import com.southernsoft.tcgtournament.pojo.TournamentAndRound;

@Dao
public interface RoundDao {
    @Query("SELECT * FROM Round WHERE round_id = :roundId")
    Round getRoundById(int roundId);

    @Query("SELECT * FROM Round WHERE tournament_id = :tournamentId ORDER BY round_id DESC LIMIT 1")
    LiveData<Round> getLastRoundByTournamentId(int tournamentId);

    @Transaction
    @Query("SELECT MAX(round_number), Round.round_time, round_id, Round.tournament_id, Tournament.date, Tournament.number_players, Tournament.number_rounds " +
            "FROM Round INNER JOIN Tournament ON Round.tournament_id = Tournament.tournament_id GROUP BY Round.tournament_id")
    LiveData<List<TournamentAndRound>> getTournamentsWithLastRound();

    @Insert
    long insert(Round round);

    @Update
    void update(Round round);

    @Query("DELETE FROM Round WHERE round_id = :roundId")
    void deleteRound(int roundId);

    @Query("DELETE FROM Round")
    int deleteAll();
}
