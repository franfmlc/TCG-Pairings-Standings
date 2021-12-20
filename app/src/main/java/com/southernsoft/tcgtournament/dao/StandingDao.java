package com.southernsoft.tcgtournament.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import com.southernsoft.tcgtournament.entity.Standing;
import com.southernsoft.tcgtournament.pojo.StandingRow;

@Dao
public interface StandingDao {
    @Transaction
    @Query("SELECT * FROM Standing WHERE tournament_id = :tournamentId AND round_id = (SELECT MAX(round_id) FROM Standing WHERE tournament_id = :tournamentId) " +
            "ORDER BY match_points DESC, opp_match_win_percentage DESC, game_win_percentage DESC, opp_game_win_percentage DESC")
    LiveData<List<StandingRow>> getLastStandingsForTournament(int tournamentId);

    @Insert
    long[] insert(List<Standing> standings);

    @Update
    void update(Standing standing);

    @Update
    int update(List<Standing> standings);

    @Delete
    void delete(Standing standing);

    @Query("DELETE FROM Standing")
    int deleteAll();
}