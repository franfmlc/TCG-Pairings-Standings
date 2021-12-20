package com.southernsoft.tcgtournament.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = {
        @ForeignKey(entity = Tournament.class, parentColumns = {"tournament_id"}, childColumns = {"tournament_id"}, onDelete = CASCADE)
})

public class Round {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "round_id")
    public int id;

    @ColumnInfo(name = "tournament_id", index = true)
    public int tournamentId;

    @ColumnInfo(name = "round_number")
    public int roundNumber;

    @ColumnInfo(name = "round_time")
    public int roundTime;
}