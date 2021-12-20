package com.southernsoft.tcgtournament.pairings.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import javax.inject.Inject;

import com.southernsoft.tcgtournament.R;
import com.southernsoft.tcgtournament.pojo.StandingRow;
import dagger.hilt.android.qualifiers.ActivityContext;
import dagger.hilt.android.scopes.FragmentScoped;

@FragmentScoped
public class StandingsAdapter extends RecyclerView.Adapter<StandingsAdapter.ViewHolder> {
    private final LayoutInflater layoutInflater;
    private List<StandingRow> standingRows;

    @Inject
    public StandingsAdapter(@ActivityContext Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    public void replaceData(List<StandingRow> standings) {
        standingRows = standings;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View standingRowView = layoutInflater.inflate(R.layout.standing_row, parent, false);
        return new ViewHolder(standingRowView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final StandingRow standingRow = standingRows.get(position);
        holder.playerPosition.setText(String.valueOf(++position));
        holder.playerName.setText(standingRow.playerName);
    }

    @Override
    public int getItemCount() {
        return standingRows == null ? 0 : standingRows.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView playerPosition;
        private final TextView playerName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            playerPosition = itemView.findViewById(R.id.player_position);
            playerName = itemView.findViewById(R.id.player_name);
        }
    }
}