package com.southernsoft.tcgtournament.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

import static androidx.room.ForeignKey.CASCADE;


@Entity(primaryKeys = {"tournament_id", "round_id", "first_player_id", "second_player_id"},
        foreignKeys = {
        @ForeignKey(entity = Tournament.class, parentColumns = {"tournament_id"}, childColumns = {"tournament_id"}, onDelete = CASCADE),
        @ForeignKey(entity = Round.class, parentColumns = {"round_id"}, childColumns = {"round_id"}),
        @ForeignKey(entity = Player.class, parentColumns = {"player_id"}, childColumns = {"first_player_id"}),
        @ForeignKey(entity = Player.class, parentColumns = {"player_id"}, childColumns = {"second_player_id"})
})

public class Pairing {
    @ColumnInfo(name = "tournament_id", index = true)
    public int tournamentId;

    @ColumnInfo(name = "round_id", index = true)
    public int roundId;

    @ColumnInfo(name = "first_player_id", index = true)
    public int firstPlayerId;

    @ColumnInfo(name = "second_player_id", index = true)
    public int secondPlayerId;

    @ColumnInfo(name = "first_player_result")
    public int firstPlayerResult;

    @ColumnInfo(name = "second_player_result")
    public int secondPlayerResult;

    @ColumnInfo(name = "table_number")
    public int tableNumber;
}