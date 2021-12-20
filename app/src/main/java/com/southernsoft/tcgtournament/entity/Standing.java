package com.southernsoft.tcgtournament.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = {
        @ForeignKey(entity = Tournament.class, parentColumns = {"tournament_id"}, childColumns = {"tournament_id"}, onDelete = CASCADE),
        @ForeignKey(entity = Round.class, parentColumns = {"round_id"}, childColumns = {"round_id"}),
        @ForeignKey(entity = Player.class, parentColumns = {"player_id"}, childColumns = {"player_id"})
})

public class Standing {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "standing_id")
    public int id;

    @ColumnInfo(name = "tournament_id", index = true)
    public int tournamentId;

    @ColumnInfo(name = "player_id", index = true)
    public int playerId;

    @ColumnInfo(name = "round_id", index = true)
    public int roundId;

    @ColumnInfo(name = "has_bye")
    public boolean hasBye;

    @ColumnInfo(name = "rounds_played")
    public int roundsPlayed;

    @ColumnInfo(name = "games_played")
    public int gamesPlayed;

    @ColumnInfo(name = "match_points")
    public int matchPoints;

    @ColumnInfo(name = "game_points")
    public int gamePoints;

    @ColumnInfo(name = "match_win_percentage", typeAffinity = ColumnInfo.INTEGER)
    public int matchWinPercentage;

    @ColumnInfo(name = "game_win_percentage", typeAffinity = ColumnInfo.INTEGER)
    public int gameWinPercentage;

    @ColumnInfo(name = "opp_match_win_percentage", typeAffinity = ColumnInfo.INTEGER)
    public int oppMatchWinPercentage;

    @ColumnInfo(name = "opp_game_win_percentage", typeAffinity = ColumnInfo.INTEGER)
    public int oppGameWinPercentage;
}