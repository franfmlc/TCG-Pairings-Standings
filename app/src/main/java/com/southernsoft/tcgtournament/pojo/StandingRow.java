package com.southernsoft.tcgtournament.pojo;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.southernsoft.tcgtournament.entity.Player;
import com.southernsoft.tcgtournament.entity.Standing;

public class StandingRow {
    @Embedded
    public Standing standing;

    @Relation(parentColumn = "player_id", entityColumn = "player_id", entity = Player.class, projection = {"player_name"})
    public String playerName;
}