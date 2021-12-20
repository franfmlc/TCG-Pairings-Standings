package com.southernsoft.tcgtournament;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.button.MaterialButton;

import javax.inject.Inject;

import com.southernsoft.tcgtournament.newtournament.NewTournamentView;
import com.southernsoft.tcgtournament.playermanagement.PlayerManagementView;
import com.southernsoft.tcgtournament.tournaments.TournamentsView;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    private MaterialButton newTournamentButton;
    private MaterialButton playerManagementButton;
    private MaterialButton tournamentManagerButton;
    @Inject public SharedPreferencesHelper sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newTournamentButton = findViewById(R.id.new_tournament);
        playerManagementButton = findViewById(R.id.player_management);
        tournamentManagerButton = findViewById(R.id.tournament_manager);

        newTournamentButton.setOnClickListener(button -> goToNewTournament());
        playerManagementButton.setOnClickListener(button -> goToPlayerManagement());
        tournamentManagerButton.setOnClickListener(button -> goToTournamentManager());

        boolean isDarkMode = sharedPreferences.getThemeMode();
        if (isDarkMode && !isCurrentNightMode())
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        MobileAds.initialize(this, initializationStatus -> {});
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void goToNewTournament() {
        Intent intent = new Intent(this, NewTournamentView.class);
        startActivity(intent);
    }

    private void goToPlayerManagement() {
        Intent intent = new Intent(this, PlayerManagementView.class);
        startActivity(intent);
    }

    private void goToTournamentManager() {
        Intent intent = new Intent(this, TournamentsView.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dark_mode_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.dark_mode) {
            if (isCurrentNightMode()) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                sharedPreferences.saveThemeMode(false);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                sharedPreferences.saveThemeMode(true);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isCurrentNightMode() {
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }
}