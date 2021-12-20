package com.southernsoft.tcgtournament.addplayer;

import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import com.southernsoft.tcgtournament.Repository;
import com.southernsoft.tcgtournament.entity.Player;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AddPlayerViewModel extends ViewModel {
    private Repository repository;

    @Inject
    public AddPlayerViewModel(Repository repository) {
        this.repository = repository;
    }

    public void insertPlayer(int playerId, String playerName, String playerIdentification) {
        Player player = new Player();
        player.id = playerId;
        player.playerName = playerName;
        player.playerIdentification = playerIdentification;
        repository.insertPlayer(player);
    }
}
