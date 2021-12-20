package com.southernsoft.tcgtournament.playermanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import javax.inject.Inject;

import com.southernsoft.tcgtournament.R;
import com.southernsoft.tcgtournament.addplayer.AddPlayerView;
import com.southernsoft.tcgtournament.entity.Player;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PlayerManagementView extends AppCompatActivity {
    @Inject PlayerListAdapter adapter;
    private PlayerManagementViewModel viewmodel;
    private TextView playersMsg;
    private FloatingActionButton fab;
    private SearchView searchView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_management);

        playersMsg = findViewById(R.id.no_players_found);
        fab = findViewById(R.id.fab);

        RecyclerView recyclerView = findViewById(R.id.players_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        viewmodel = new ViewModelProvider(this).get(PlayerManagementViewModel.class);
        viewmodel.getPlayers().observe(this, this::showPlayers);

        fab.setOnClickListener(fab -> {
            Intent intent = new Intent(this, AddPlayerView.class);
            startActivity(intent);
        });
    }

    private void showPlayers(List<Player> players) {
        if (players.size() > 0)
            playersMsg.setVisibility(View.GONE);
        else
            playersMsg.setVisibility(View.VISIBLE);

        adapter.setOriginalList(players);

        if (searchView != null && !searchView.isIconified())
            adapter.getFilter().filter(searchView.getQuery());
    }

    public void deletePlayers(List<Player> players) {
        if (!viewmodel.deletePlayers(players))
            Toast.makeText(this, R.string.delete_players_error, Toast.LENGTH_LONG).show();
    }

    public void onActionMode(boolean isEnabled) {
        if (isEnabled)
            fab.setVisibility(View.GONE);
        else if (searchView.isIconified())
            fab.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.onActionViewCollapsed();
            fab.setVisibility(View.VISIBLE);
        }
        else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_action_menu, menu);
        searchView  = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setQueryHint(getString(R.string.search_hint));
        // Software keyboard doesn't occupy the whole screen in landscape mode
        searchView.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_ACTION_SEARCH);

        searchView.setOnSearchClickListener(view -> fab.setVisibility(View.GONE));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
        return true;
    }
}