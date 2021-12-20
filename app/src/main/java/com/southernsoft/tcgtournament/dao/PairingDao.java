package com.southernsoft.tcgtournament.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import com.southernsoft.tcgtournament.entity.Pairing;
import com.southernsoft.tcgtournament.pojo.PairingTuple;

@Dao
public interface PairingDao {
    @Transaction
    @Query("SELECT * FROM Pairing WHERE tournament_id = :tournamentId AND round_id = (SELECT MAX(round_id) FROM Pairing WHERE tournament_id = :tournamentId) " +
            "ORDER BY table_number ASC")
    LiveData<List<PairingTuple>> getLastPairingsForTournament(int tournamentId);

    @Insert
    void insert(Pairing pairing);

    @Insert
    void insert(List<Pairing> pairings);

    @Update
    void update(List<Pairing> pairings);

    @Delete
    void delete(Pairing Pairing);

    @Query("DELETE FROM Pairing")
    int deleteAll();
}
