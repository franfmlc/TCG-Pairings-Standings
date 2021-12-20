package com.southernsoft.tcgtournament.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;
import java.util.Set;

import com.southernsoft.tcgtournament.entity.Player;

@Dao
public interface PlayerDao {
    @Query("SELECT * FROM Player WHERE player_id != 1 ORDER BY player_name COLLATE NOCASE ASC")
    LiveData<List<Player>> getAllPlayers();

    @Query("SELECT * FROM Player WHERE player_id IN (:playerIds) ORDER BY player_name COLLATE NOCASE ASC")
    List<Player> getPlayersById(Set<Integer> playerIds);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Player player);

    @Insert
    void insert(List<Player> players);

    @Delete
    void delete(List<Player> players);

    @Query("DELETE FROM Player WHERE player_id != 1")
    void deleteAll();
}