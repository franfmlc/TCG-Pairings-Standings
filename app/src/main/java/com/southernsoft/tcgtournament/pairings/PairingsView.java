package com.southernsoft.tcgtournament.pairings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import javax.inject.Inject;

import com.southernsoft.tcgtournament.IntentTags;
import com.southernsoft.tcgtournament.MainActivity;
import com.southernsoft.tcgtournament.R;
import com.southernsoft.tcgtournament.pairings.dialog.DeleteTournamentDialog;
import com.southernsoft.tcgtournament.pairings.dialog.DeleteTournamentListener;
import com.southernsoft.tcgtournament.pairings.dialog.PauseTournamentDialog;
import com.southernsoft.tcgtournament.pairings.fragment.FragmentAdapter;
import com.southernsoft.tcgtournament.util.RoundTimeUtils;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PairingsView extends AppCompatActivity implements DeleteTournamentListener {
    private PairingsViewModel viewModel;
    private TextView roundNumberView;
    private TextView roundTimeView;
    private ViewPager2 viewPager;
    @Inject public FragmentAdapter fragmentAdapter;
    private AlertDialog loading;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairings);

        roundNumberView = findViewById(R.id.toolbar_round_number);
        roundTimeView = findViewById(R.id.toolbar_round_time);
        viewPager = findViewById(R.id.pager);
        progressBar = findViewById(R.id.progress_bar);
        viewPager.setOffscreenPageLimit(1);
        fragmentAdapter = new FragmentAdapter(this);
        viewPager.setAdapter(fragmentAdapter);

        setupTabLayout();

        viewModel = new ViewModelProvider(this).get(PairingsViewModel.class);

        Intent intent = getIntent();
        viewModel.setTournament(intent.getIntExtra(IntentTags.TOURNAMENT_ID, 0));
        viewModel.setRound(intent.getIntExtra(IntentTags.ROUND_ID, 0));

        viewModel.getCurrentRoundNumber().observe(this, roundNumber -> roundNumberView.setText(String.valueOf(roundNumber)));
        viewModel.getCurrentRoundTime().observe(this, roundTime -> roundTimeView.setText(RoundTimeUtils.formatElapsedTime(roundTime)));

        loading = new AlertDialog.Builder(this).create();
        loading.getWindow().setDimAmount(0.2f);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (viewModel.hasRoundStarted())
            viewModel.resumeCountDown();
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.saveRoundTime();

        if (viewModel.hasRoundStarted())
            viewModel.stopCountDown();
    }

    @Override
    public void onBackPressed() {
        if (!viewModel.hasTournamentFinished())
            new PauseTournamentDialog().show(getSupportFragmentManager(), "dialog");
        else
            new DeleteTournamentDialog().show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onDialogDeleteTournament() {
        viewModel.removeTournament();
        goToMainActivity();
    }

    @Override
    public void onDialogKeepTournament() {
        goToMainActivity();
    }

    public void showLoadingDialog () {
        loading.show();
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideLoadingDialog() {
        loading.cancel();
        progressBar.setVisibility(View.GONE);
    }

    public void goToStandingsTab() {
        viewPager.setCurrentItem(1);
    }

    public void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private void setupTabLayout() {
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) ->  {
                    if (position == 0)
                        tab.setText(R.string.pairings);
                    else
                        tab.setText(R.string.standings);
                }).attach();
    }
}