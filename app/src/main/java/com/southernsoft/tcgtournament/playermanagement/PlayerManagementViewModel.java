package com.southernsoft.tcgtournament.playermanagement;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import com.southernsoft.tcgtournament.Repository;
import com.southernsoft.tcgtournament.entity.Player;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PlayerManagementViewModel extends ViewModel {
    private LiveData<List<Player>> players;
    private Repository repository;

    @Inject
    public PlayerManagementViewModel(Repository repository) {
        this.repository = repository;
    }

    public LiveData<List<Player>> getPlayers() {
        if (players == null) {
            players = repository.getAllPlayers();
        }
        return players;
    }

    public boolean deletePlayers(List<Player> players) {
        return repository.deletePlayers(players);
    }
}