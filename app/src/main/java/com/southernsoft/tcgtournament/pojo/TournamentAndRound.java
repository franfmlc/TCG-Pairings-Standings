package com.southernsoft.tcgtournament.pojo;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.ColumnInfo;
import androidx.room.TypeConverters;

import java.util.Date;

import com.southernsoft.tcgtournament.util.Converters;

@TypeConverters({Converters.class})
public class TournamentAndRound {
    @ColumnInfo(name = "MAX(round_number)")
    public int maxRound;

    @ColumnInfo(name = "round_time")
    public int remainingTime;

    @ColumnInfo(name = "round_id")
    public int roundId;

    @ColumnInfo(name = "tournament_id")
    public int tournamentId;

    @ColumnInfo(name = "date")
    public Date date;

    @ColumnInfo(name = "number_players")
    public int numberPlayers;

    @ColumnInfo(name = "number_rounds")
    public int numberRounds;

    public static final DiffUtil.ItemCallback<TournamentAndRound> DIFF_CALLBACK = new DiffUtil.ItemCallback<TournamentAndRound>() {
        @Override
        public boolean areItemsTheSame(@NonNull TournamentAndRound oldItem, @NonNull TournamentAndRound newItem) {
            return oldItem.tournamentId == newItem.tournamentId;
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull TournamentAndRound oldItem, @NonNull TournamentAndRound newItem) {
            return  oldItem.date == newItem.date &&
                    oldItem.numberPlayers == newItem.numberPlayers &&
                    oldItem.maxRound == newItem.maxRound &&
                    oldItem.numberRounds == newItem.numberRounds &&
                    oldItem.remainingTime == newItem.remainingTime;
        }
    };
}