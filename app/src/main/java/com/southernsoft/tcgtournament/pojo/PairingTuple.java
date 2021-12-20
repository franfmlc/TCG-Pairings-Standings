package com.southernsoft.tcgtournament.pojo;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.southernsoft.tcgtournament.entity.Pairing;
import com.southernsoft.tcgtournament.entity.Player;

public class PairingTuple {

    @Embedded
    public Pairing pairing;

    @Relation(parentColumn = "first_player_id", entityColumn = "player_id", entity = Player.class, projection = {"player_name"})
    public String firstPlayerName;

    @Relation(parentColumn = "second_player_id", entityColumn = "player_id", entity = Player.class, projection = {"player_name"})
    public String secondPlayerName;
}