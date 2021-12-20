import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import com.southernsoft.tcgtournament.Repository;
import com.southernsoft.tcgtournament.entity.Player;
import com.southernsoft.tcgtournament.playermanagement.PlayerManagementViewModel;
import util.LiveDataTestUtil;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PlayerManagementViewModelTest {
    @Rule public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Mock private Repository repository;

    private PlayerManagementViewModel playerManagementViewModel;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        playerManagementViewModel = new PlayerManagementViewModel(repository);
    }

    @Test
    public void getPlayers_returnsPlayersList() {
        // Given
        MutableLiveData<List<Player>> mockedLiveData = new MutableLiveData<>();
        List<Player> mockedPlayers = new ArrayList<>();
        List<Player> actualPlayers = new ArrayList<>();

        mockedLiveData.setValue(mockedPlayers);
        when(repository.getAllPlayers()).thenReturn(mockedLiveData);

        // When
        try {
            actualPlayers = LiveDataTestUtil.getOrAwaitValue(playerManagementViewModel.getPlayers());
        } catch (InterruptedException ex) {}

        // Then
        assertEquals(mockedPlayers, actualPlayers);
        verify(repository, times(1)).getAllPlayers();
    }

    @Test
    public void deletePlayers_playersListProvided_repositoryCalled() {
        // Given
        List<Player> playersToDelete = new ArrayList<>();

        // When
        playerManagementViewModel.deletePlayers(playersToDelete);

        // Then
        verify(repository, times(1)).deletePlayers(playersToDelete);
    }
}