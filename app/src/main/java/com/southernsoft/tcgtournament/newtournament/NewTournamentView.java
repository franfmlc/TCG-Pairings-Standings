package com.southernsoft.tcgtournament.newtournament;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.analytics.FirebaseAnalytics;

import com.southernsoft.tcgtournament.IntentTags;
import com.southernsoft.tcgtournament.R;
import com.southernsoft.tcgtournament.enrollplayer.EnrollPlayerView;
import com.southernsoft.tcgtournament.pairings.PairingsView;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NewTournamentView extends AppCompatActivity {
    private TextInputEditText numberPlayers;
    private TextInputEditText roundTime;
    private TextInputEditText numberRounds;
    private ImageButton decrementRounds;
    private ImageButton incrementRounds;
    private ImageButton decrementTime;
    private ImageButton incrementTime;
    private Button enroll;
    private Button start;
    private Animation animation;
    private NewTournamentViewModel viewModel;
    private FirebaseAnalytics firebaseAnalytics;

    private final String EVENT_NEW_TOURNAMENT = "new_tournament";
    private final String PARAM_NUMBER_PLAYERS = "number_players";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_tournament);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        viewModel = new ViewModelProvider(this).get(NewTournamentViewModel.class);

        numberPlayers = findViewById(R.id.number_players);
        numberRounds = findViewById(R.id.number_rounds);
        roundTime = findViewById(R.id.round_time);
        decrementRounds = findViewById(R.id.decrement_rounds);
        incrementRounds = findViewById(R.id.increment_rounds);
        decrementTime = findViewById(R.id.decrement_time);
        incrementTime = findViewById(R.id.increment_time);
        enroll = findViewById(R.id.enroll);
        start = findViewById(R.id.start);

        animation = AnimationUtils.loadAnimation(this, R.anim.bounce);

        viewModel.getNumberOfRounds().observe(this, rounds -> numberRounds.setText(String.valueOf(rounds)));
        viewModel.getRoundTime().observe(this, time -> roundTime.setText(String.valueOf(time)));

        ActivityResultLauncher<Intent> content =  registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                int numberOfPlayers = result.getData().getIntExtra(EnrollPlayerView.EXTRA_DATA, 0);
                viewModel.setNumberOfPlayers(numberOfPlayers);
                numberPlayers.setText(String.valueOf(numberOfPlayers));
            }
        });

        decrementRounds.setOnClickListener(view -> viewModel.decrementRounds());

        incrementRounds.setOnClickListener(view -> viewModel.incrementRounds());

        decrementTime.setOnClickListener(view -> viewModel.decrementTime());

        incrementTime.setOnClickListener(view -> viewModel.incrementTime());

        enroll.setOnClickListener(view -> content.launch(new Intent(this, EnrollPlayerView.class)));

        start.setOnClickListener(view -> {
            if (viewModel.validNumberOfPlayers()) {
                logTournamentEvent();
                viewModel.createNewTournament();
                viewModel.deleteEnrolledPlayers();
                Intent intent = new Intent(this, PairingsView.class);
                intent.putExtra(IntentTags.TOURNAMENT_ID, viewModel.getTournamentId());
                intent.putExtra(IntentTags.ROUND_ID, viewModel.getRoundId());
                startActivity(intent);
            } else {
                Toast.makeText(this, R.string.enroll_3_players, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (viewModel.getNumberOfPlayers() > 0) {
            enableView(numberRounds, false);
            enableView(roundTime, false);
            enableView(decrementRounds, true);
            enableView(incrementRounds, true);
            enableView(decrementTime, true);
            enableView(incrementTime, true);
        } else {
            enroll.startAnimation(animation);
            disableView(numberRounds);
            disableView(roundTime);
            disableView(decrementRounds);
            disableView(incrementRounds);
            disableView(decrementTime);
            disableView(incrementTime);
        }
        disableView(numberPlayers);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isFinishing())
            viewModel.deleteEnrolledPlayers();
    }

    private void disableView(View view) {
        view.setAlpha(0.5f);
        view.setClickable(false);
        view.setFocusable(false);
    }

    private void enableView(View view, Boolean clickable) {
        view.setAlpha(1f);
        view.setClickable(clickable);
        view.setFocusable(clickable);
    }

    private void logTournamentEvent() {
        Bundle bundle = new Bundle();
        bundle.putInt(PARAM_NUMBER_PLAYERS, viewModel.getNumberOfPlayers());
        firebaseAnalytics.logEvent(EVENT_NEW_TOURNAMENT, bundle);
    }
}