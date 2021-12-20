package com.southernsoft.tcgtournament.enrollplayer;

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
import com.southernsoft.tcgtournament.entity.Player;
import dagger.hilt.android.qualifiers.ActivityContext;
import dagger.hilt.android.scopes.ActivityScoped;

@ActivityScoped
public class EnrollPlayerMultiAdapter {
    private final EnrollPlayerAdapter availablePlayersAdapter;
    private final EnrollPlayerAdapter enrolledPlayersAdapter;

    @Inject
    public EnrollPlayerMultiAdapter(@ActivityContext Context context) {
        availablePlayersAdapter = new EnrollPlayerAdapter(context);
        enrolledPlayersAdapter = new EnrollPlayerAdapter(context);
    }

    public EnrollPlayerAdapter getAvailablePlayersAdapter() {
        return availablePlayersAdapter;
    }

    public EnrollPlayerAdapter getEnrolledPlayersAdapter() {
        return enrolledPlayersAdapter;
    }

    public class EnrollPlayerAdapter extends RecyclerView.Adapter<EnrollPlayerAdapter.ViewHolder> {
        private List<Player> playersList;
        private LayoutInflater layoutInflater;

        public EnrollPlayerAdapter(Context context) {
            layoutInflater = LayoutInflater.from(context);
        }

        public void replaceData(List<Player> playersList) {
            this.playersList = playersList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public EnrollPlayerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View playerRowView = layoutInflater.inflate(R.layout.player_simple_row, viewGroup, false);
            return new EnrollPlayerAdapter.ViewHolder(playerRowView);
        }

        @Override
        public void onBindViewHolder(@NonNull EnrollPlayerAdapter.ViewHolder viewHolder, int position) {
            Player player = playersList.get(position);
            viewHolder.playerName.setText(player.playerName);
        }

        @Override
        public int getItemCount() {
            return playersList == null ? 0 : playersList.size();
        }

        public List<Player> getPlayersList() {
            return playersList;
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private final TextView playerName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                playerName = itemView.findViewById(R.id.player_name_simple_row);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int position = getBindingAdapterPosition();
                if (position >= 0) {
                    Player removedPlayer = playersList.remove(position);
                    notifyItemRemoved(position);

                    if (getBindingAdapter() == availablePlayersAdapter) {
                        int newPlayerPosition = updatePlayersList(enrolledPlayersAdapter.getPlayersList(), removedPlayer);
                        enrolledPlayersAdapter.notifyItemInserted(newPlayerPosition);
                    } else {
                        int newPlayerPosition = updatePlayersList(availablePlayersAdapter.getPlayersList(), removedPlayer);
                        availablePlayersAdapter.notifyItemInserted(newPlayerPosition);
                    }
                }
            }
        }

        private int updatePlayersList(List<Player> listToUpdate, Player newPlayer) {
            int position = 0;
            if (listToUpdate.size() > 0)
                for (; position < listToUpdate.size(); position++)
                    if (newPlayer.playerName.compareToIgnoreCase(listToUpdate.get(position).playerName) < 0)
                        break;

            listToUpdate.add(position, newPlayer);
            return position;
        }
    }
}
