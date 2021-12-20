package com.southernsoft.tcgtournament.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

import com.southernsoft.tcgtournament.util.Converters;

@Entity
@TypeConverters({Converters.class})
public class Tournament {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "tournament_id")
    public int id;

    @ColumnInfo(name = "number_players")
    public int numberPlayers;

    @ColumnInfo(name = "number_rounds")
    public int numberRounds;

    @ColumnInfo(name = "round_time")
    public int roundTime;

    @ColumnInfo(name = "date")
    public Date date;
}