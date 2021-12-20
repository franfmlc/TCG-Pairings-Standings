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
public class TiebreakersAdapter extends RecyclerView.Adapter<TiebreakersAdapter.ViewHolder> {
    private final LayoutInflater layoutInflater;
    private List<StandingRow> standingRows;

    @Inject
    public TiebreakersAdapter(@ActivityContext Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    public void replaceData(List<StandingRow> standings) {
        standingRows = standings;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View tiebreakersView = layoutInflater.inflate(R.layout.tiebreakers_row, parent, false);
        return new ViewHolder(tiebreakersView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final StandingRow standingRow = standingRows.get(position);
        holder.playerMatchPoints.setText(String.valueOf(standingRow.standing.matchPoints));
        holder.playerOMW.setText(String.valueOf((float) standingRow.standing.oppMatchWinPercentage / 100));
        holder.playerGW.setText(String.valueOf((float)standingRow.standing.gameWinPercentage / 100));
        holder.playerOGW.setText(String.valueOf((float)standingRow.standing.oppGameWinPercentage / 100));
    }

    @Override
    public int getItemCount() {
        return standingRows == null ? 0 : standingRows.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView playerMatchPoints;
        private final TextView playerOMW;
        private final TextView playerGW;
        private final TextView playerOGW;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            playerMatchPoints = itemView.findViewById(R.id.player_match_points);
            playerOMW = itemView.findViewById(R.id.player_omw);
            playerGW = itemView.findViewById(R.id.player_gw);
            playerOGW = itemView.findViewById(R.id.player_ogw);
        }
    }
}