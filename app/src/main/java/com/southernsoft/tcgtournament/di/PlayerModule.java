package com.southernsoft.tcgtournament.di;

import android.app.Activity;

import java.util.List;

import com.southernsoft.tcgtournament.entity.Player;
import com.southernsoft.tcgtournament.playermanagement.PlayerManagementCallback;
import com.southernsoft.tcgtournament.playermanagement.PlayerManagementView;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.scopes.ActivityScoped;

@Module
@InstallIn(ActivityComponent.class)
public abstract class PlayerModule {
    @Provides
    @ActivityScoped
    static PlayerManagementCallback providesCallback(Activity activity) {
        return new PlayerManagementCallback() {
            private final PlayerManagementView view = (PlayerManagementView) activity;
            @Override
            public void delete(List<Player> players) {
                view.deletePlayers(players);
            }

            @Override
            public void onActionMode(boolean enabled) {
                view.onActionMode(enabled);
            }
        };
    }
}
