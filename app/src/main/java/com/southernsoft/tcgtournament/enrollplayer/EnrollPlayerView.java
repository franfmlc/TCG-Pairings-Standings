package com.southernsoft.tcgtournament.enrollplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import javax.inject.Inject;

import com.southernsoft.tcgtournament.R;
import com.southernsoft.tcgtournament.addplayer.AddPlayerView;
import com.southernsoft.tcgtournament.entity.Player;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EnrollPlayerView extends AppCompatActivity {
    private RecyclerView availablePlayersList;
    private RecyclerView enrolledPlayersList;
    private Button enrollAllButton;
    private Button confirmButton;
    private TextView availableCount;
    private TextView enrolledCount;
    private EnrollPlayerViewModel viewModel;
    @Inject EnrollPlayerMultiAdapter multiAdapter;
    private EnrollPlayerMultiAdapter.EnrollPlayerAdapter availablePlayersAdapter;
    private EnrollPlayerMultiAdapter.EnrollPlayerAdapter enrolledPlayersAdapter;
    public final static String EXTRA_DATA = "TotalPlayers";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll_player);

        availablePlayersList = findViewById(R.id.available_list);
        enrolledPlayersList = findViewById(R.id.enrolled_list);
        enrollAllButton = findViewById(R.id.enroll_all);
        confirmButton = findViewById(R.id.confirm);
        availableCount = findViewById(R.id.available_count);
        enrolledCount = findViewById(R.id.enrolled_count);

        viewModel = new ViewModelProvider(this).get(EnrollPlayerViewModel.class);
        viewModel.getAvailablePlayers().observe(this, this::showAvailablePlayers);
        viewModel.getEnrolledPlayers().observe(this, this::showEnrolledPlayers);

        RecyclerView.LayoutManager layoutManagerAvailable = new LinearLayoutManager(this);
        RecyclerView.LayoutManager layoutManagerEnrolled = new LinearLayoutManager(this);

        availablePlayersAdapter = multiAdapter.getAvailablePlayersAdapter();
        enrolledPlayersAdapter = multiAdapter.getEnrolledPlayersAdapter();

        availablePlayersList.setLayoutManager(layoutManagerAvailable);
        availablePlayersList.setAdapter(multiAdapter.getAvailablePlayersAdapter());

        availablePlayersAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                availableCount.setText(String.valueOf(availablePlayersAdapter.getPlayersList().size()));
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                availableCount.setText(String.valueOf(availablePlayersAdapter.getPlayersList().size()));
            }

            @Override
            public void onChanged() {
                availableCount.setText(String.valueOf(availablePlayersAdapter.getPlayersList().size()));
            }
        });

        enrolledPlayersList.setLayoutManager(layoutManagerEnrolled);
        enrolledPlayersList.setAdapter(multiAdapter.getEnrolledPlayersAdapter());

        enrolledPlayersAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                enrolledCount.setText(String.valueOf(enrolledPlayersAdapter.getPlayersList().size()));
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                enrolledCount.setText(String.valueOf(enrolledPlayersAdapter.getPlayersList().size()));
            }

            @Override
            public void onChanged() {
                enrolledCount.setText(String.valueOf(enrolledPlayersAdapter.getPlayersList().size()));
            }
        });

        enrollAllButton.setOnClickListener(view -> {
            viewModel.enrollAllPlayers();
            availablePlayersAdapter.notifyDataSetChanged();
            enrolledPlayersAdapter.notifyDataSetChanged();
        });

        confirmButton.setOnClickListener(view -> {
            viewModel.saveEnrolledPlayers();
            Intent result = new Intent();
            result.putExtra(EXTRA_DATA, viewModel.getTotalEnrolled());
            setResult(Activity.RESULT_OK, result);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        Intent result = new Intent();
        result.putExtra(EXTRA_DATA, viewModel.getTotalEnrolled());
        setResult(Activity.RESULT_OK, result);
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing())
            viewModel.saveEnrolledPlayers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.enroll_player_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem createPlayer = menu.findItem(R.id.create_player);
        createPlayer.getActionView().setOnClickListener(view -> onOptionsItemSelected(createPlayer));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.create_player) {
            Intent intent = new Intent(this, AddPlayerView.class);
            startActivity(intent);
        }
        return true;
    }

    private void showAvailablePlayers(List<Player> playersList) {
        availablePlayersAdapter.replaceData(playersList);
        availableCount.setText(String.valueOf(playersList.size()));
    }

    private void showEnrolledPlayers(List<Player> playersList) {
        enrolledPlayersAdapter.replaceData(playersList);
        enrolledCount.setText(String.valueOf(playersList.size()));
    }
}