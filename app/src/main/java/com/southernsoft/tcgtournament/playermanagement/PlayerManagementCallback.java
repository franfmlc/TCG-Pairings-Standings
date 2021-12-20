package com.southernsoft.tcgtournament.playermanagement;

import java.util.List;

import com.southernsoft.tcgtournament.entity.Player;

public interface PlayerManagementCallback {
    void delete(List<Player> players);
    void onActionMode(boolean enabled);
}
