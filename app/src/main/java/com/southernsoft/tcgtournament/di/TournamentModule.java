package com.southernsoft.tcgtournament.di;

import android.app.Activity;
import android.content.Intent;

import com.southernsoft.tcgtournament.IntentTags;
import com.southernsoft.tcgtournament.pairings.PairingsView;
import com.southernsoft.tcgtournament.tournaments.TournamentClickCallback;
import com.southernsoft.tcgtournament.tournaments.TournamentsView;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.scopes.ActivityScoped;

@Module
@InstallIn(ActivityComponent.class)
public abstract class TournamentModule {
    @Provides
    @ActivityScoped
    static TournamentClickCallback providesClickCallback(Activity activity) {
        return new TournamentClickCallback() {
            final TournamentsView view = (TournamentsView) activity;

            @Override
            public void onResume(int tournamentId, int roundId) {
                Intent intent = new Intent(view, PairingsView.class);
                intent.putExtra(IntentTags.TOURNAMENT_ID, tournamentId);
                intent.putExtra(IntentTags.ROUND_ID, roundId);
                view.startActivity(intent);
            }
            
            @Override
            public void onDelete(int tournamentId) {
                view.deleteTournament(tournamentId);
            }
        };
    }
}