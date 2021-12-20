package com.southernsoft.tcgtournament.addplayer;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.analytics.FirebaseAnalytics;

import com.southernsoft.tcgtournament.R;
import dagger.hilt.android.AndroidEntryPoint;
import com.southernsoft.tcgtournament.util.MaskWatcher;

@AndroidEntryPoint
public class AddPlayerView extends AppCompatActivity {
    private EditText playerName;
    private EditText playerIdentification;
    private AddPlayerViewModel viewModel;
    private int pId;
    private FirebaseAnalytics firebaseAnalytics;

    private final String EVENT_CREATE_PLAYER = "create_player";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_player);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        viewModel = new ViewModelProvider(this).get(AddPlayerViewModel.class);

        playerName = findViewById(R.id.player_name);
        playerIdentification = findViewById(R.id.player_identification);

        playerName.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        playerIdentification.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

        pId = getIntent().getIntExtra("playerId", 0);
        playerName.setText(getIntent().getStringExtra("playerName"));
        playerIdentification.setText(getIntent().getStringExtra("playerIdentification"));

        playerIdentification.addTextChangedListener(new MaskWatcher("####-###-###"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        playerName.requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_player_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem saveMenu = menu.findItem(R.id.save_player);
        saveMenu.getActionView().setOnClickListener(view -> onOptionsItemSelected(saveMenu));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save_player) {
            String name = String.valueOf(playerName.getText());
            String identification = String.valueOf(playerIdentification.getText());
            if (!name.isEmpty()) {
                logCreatePlayerEvent();
                name = name.trim();
                viewModel.insertPlayer(pId, name, identification);
                onBackPressed();
            }
        }
        return true;
    }

    private void logCreatePlayerEvent() {
        firebaseAnalytics.logEvent(EVENT_CREATE_PLAYER, null);
    }
}