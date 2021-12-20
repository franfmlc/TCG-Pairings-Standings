package com.southernsoft.tcgtournament.playermanagement;

import android.content.Context;
import android.content.Intent;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.southernsoft.tcgtournament.R;
import com.southernsoft.tcgtournament.addplayer.AddPlayerView;
import com.southernsoft.tcgtournament.databinding.PlayerRowBinding;
import com.southernsoft.tcgtournament.entity.Player;
import dagger.hilt.android.qualifiers.ActivityContext;
import dagger.hilt.android.scopes.ActivityScoped;

@ActivityScoped
public class PlayerListAdapter extends ListAdapter<Player, PlayerListAdapter.ViewHolder> {
    private final LayoutInflater layoutInflater;
    private Filter filter;
    private List<Player> originalList;
    private List<Player> selectedPlayers;
    private boolean actionModeEnabled = false;
    private PlayerManagementCallback callback;

    @Inject
    public PlayerListAdapter(@ActivityContext Context context, PlayerManagementCallback providedCallback) {
        super(Player.DIFF_CALLBACK);
        layoutInflater = LayoutInflater.from(context);
        selectedPlayers = new ArrayList<>();
        callback = providedCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PlayerRowBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.player_row, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Player player = getItem(position);
        holder.bind(player);
    }

    public void setOriginalList(List<Player> playerList) {
        originalList = playerList;
        selectedPlayers.clear();
        submitList(playerList);
    }

    public Filter getFilter() {
        if (filter == null)
            filter = new PlayersFilter();
       return filter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final PlayerRowBinding binding;
        private final View rootView;

        public ViewHolder(@NonNull PlayerRowBinding playerBinding) {
            super(playerBinding.getRoot());
            rootView = playerBinding.getRoot();
            binding = playerBinding;

            binding.checkBox.setOnClickListener(v -> {
                Player player = originalList.get(getAbsoluteAdapterPosition());
                if (!selectedPlayers.contains(player))
                    selectedPlayers.add(player);
                else
                    selectedPlayers.remove(player);
            });

            rootView.setOnClickListener(view -> {
                Player player = getCurrentList().get(getAbsoluteAdapterPosition());
                Intent intent = new Intent(rootView.getContext(), AddPlayerView.class);
                intent.putExtra("playerId", player.id);
                intent.putExtra("playerName", player.playerName);
                intent.putExtra("playerIdentification", player.playerIdentification);
                rootView.getContext().startActivity(intent);
            });

            rootView.setOnLongClickListener(view -> {
                if (!actionModeEnabled) {
                    view.startActionMode(new ActionMode.Callback() {
                        @Override
                        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                            mode.getMenuInflater().inflate(R.menu.player_context_menu, menu);
                            actionModeEnabled = true;
                            callback.onActionMode(true);
                            return true;
                        }

                        @Override
                        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                            return false;
                        }

                        @Override
                        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                            if (item.getItemId() == R.id.delete_player) {
                                if (selectedPlayers.size() > 0) {
                                    callback.delete(selectedPlayers);
                                    return true;
                                }
                            }
                            return false;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode mode) {
                            actionModeEnabled = false;
                            callback.onActionMode(false);
                            notifyDataSetChanged();
                        }
                    });
                    Player player = getCurrentList().get(getAbsoluteAdapterPosition());
                    if (!selectedPlayers.contains(player))
                        selectedPlayers.add(player);
                    notifyDataSetChanged();
                }
                return true;
            });
        }

        public void bind(Player player) {
            binding.setPlayerName(player.playerName);
            binding.setPlayerIdentification(player.playerIdentification);
            if (actionModeEnabled) {
                binding.checkBox.setVisibility(View.VISIBLE);
                binding.checkBox.setChecked(selectedPlayers.contains(player));
            } else {
                binding.checkBox.setVisibility(View.GONE);
            }
        }
    }

    public class PlayersFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                List<Player> filteredPlayers = new ArrayList<>();
                String upperConstraint = constraint.toString().toUpperCase();

                for (int i = 0; i < originalList.size(); i++) {
                    final Player player = originalList.get(i);
                    if (player.playerName.toUpperCase().startsWith(upperConstraint) ||
                            (player.playerIdentification != null && player.playerIdentification.startsWith(upperConstraint))) {
                        filteredPlayers.add(player);
                    }
                }
                results.count = filteredPlayers.size();
                results.values = filteredPlayers;
            } else {
                results.count = originalList.size();
                results.values = originalList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            submitList((List<Player>) results.values);
        }
    }
}