package com.southernsoft.tcgtournament.entity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Player implements Comparable<Player> {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "player_id")
    public int id;

    @ColumnInfo(name = "player_name")
    public String playerName;

    @ColumnInfo(name = "player_identification")
    public String playerIdentification;

    @Override
    public int compareTo(Player player) {
        return this.playerName.compareToIgnoreCase(player.playerName);
    }

    public static final DiffUtil.ItemCallback<Player> DIFF_CALLBACK = new DiffUtil.ItemCallback<Player>() {
        @Override
        public boolean areItemsTheSame(@NonNull Player oldItem, @NonNull Player newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Player oldItem, @NonNull Player newItem) {
            return  oldItem.playerName.equals(newItem.playerName) &&
                    oldItem.playerIdentification == newItem.playerIdentification;
        }
    };
}