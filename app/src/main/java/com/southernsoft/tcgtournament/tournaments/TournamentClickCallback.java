package com.southernsoft.tcgtournament.tournaments;

public interface TournamentClickCallback {
    void onResume(int tournamentId, int roundId);
    void onDelete(int tournamentId);
}
