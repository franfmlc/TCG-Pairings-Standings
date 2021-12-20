package com.southernsoft.tcgtournament.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.southernsoft.tcgtournament.entity.Tournament;

@Dao
public interface TournamentDao {
    @Query("SELECT * FROM Tournament WHERE tournament_id = :tournamentId")
    Tournament getTournamentById(int tournamentId);

    @Insert
    long insert(Tournament tournament);

    @Query("DELETE FROM Tournament WHERE tournament_id = :tournamentId")
    void deleteTournament(int tournamentId);

    @Query("DELETE FROM Tournament")
    int deleteAll();
}
