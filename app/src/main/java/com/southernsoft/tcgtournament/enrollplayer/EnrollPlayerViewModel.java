package com.southernsoft.tcgtournament.enrollplayer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import com.southernsoft.tcgtournament.Repository;
import com.southernsoft.tcgtournament.entity.Player;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class EnrollPlayerViewModel extends ViewModel {
    private final Repository repository;
    private LiveData<List<Player>> availablePlayers;
    private MutableLiveData<List<Player>> enrolledPlayers;

    @Inject
    public EnrollPlayerViewModel(Repository repository) {
        this.repository = repository;
        loadEnrolledPlayerList();
    }

    public LiveData<List<Player>> getAvailablePlayers() {
        if (availablePlayers == null)
            availablePlayers = Transformations.map(repository.getAllPlayers(), allPlayers -> {
                removeEnrolledPlayers(allPlayers, enrolledPlayers.getValue());
                return allPlayers;
            });
        return availablePlayers;
    }

    public LiveData<List<Player>> getEnrolledPlayers() {
        return enrolledPlayers;
    }

    public int getTotalEnrolled() {
        return enrolledPlayers.getValue().size();
    }

    private void loadEnrolledPlayerList() {
        Set<Integer> enrolledPlayerIds = repository.getEnrolledPlayerIds();
        enrolledPlayers = new MutableLiveData<>(repository.getPlayersById(enrolledPlayerIds));
    }

    public void saveEnrolledPlayers() {
        repository.saveEnrolledPlayers(enrolledPlayers.getValue());
    }

    public void enrollAllPlayers() {
        List<Player> available = availablePlayers.getValue();
        if (available.size() > 0) {
            List<Player> enrolled = enrolledPlayers.getValue();
            enrolled.addAll(available);
            available.clear();
            Collections.sort(enrolled);
        }
    }

    private void removeEnrolledPlayers(List<Player> allPlayers, List<Player> enrolledPlayers) {
        if (enrolledPlayers.size() > 0) {
            List<Player> tmpEnrolled = new ArrayList<>(enrolledPlayers);
            int index = 0;
            while (tmpEnrolled.size() > 0) {
                for (int j = 0; j < tmpEnrolled.size(); j++)
                    if (allPlayers.get(index).id == tmpEnrolled.get(j).id) {
                        allPlayers.remove(index);
                        tmpEnrolled.remove(j);
                        index--;
                        break;
                    }
                index++;
            }
        }
    }
}
