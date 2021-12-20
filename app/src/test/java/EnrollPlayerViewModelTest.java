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
import com.southernsoft.tcgtournament.enrollplayer.EnrollPlayerViewModel;

import com.southernsoft.tcgtournament.entity.Player;
import com.southernsoft.tcgtournament.util.Lists;
import util.LiveDataTestUtil;
import util.RandomIdGenerator;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EnrollPlayerViewModelTest {
    @Rule public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Mock private Repository repository;

    private EnrollPlayerViewModel enrollPlayerViewModel;
    private RandomIdGenerator idGenerator;
    private Player playerOne;
    private Player playerTwo;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        idGenerator = new RandomIdGenerator();

        playerOne = new Player();
        playerTwo = new Player();

        playerOne.id = idGenerator.getRandomId();
        playerOne.playerName = "A";
        playerTwo.id = idGenerator.getRandomId();
        playerTwo.playerName = "B";

        List<Player> enrolledPlayers = Lists.newArrayList(playerOne, playerTwo);

        when(repository.getPlayersById(anySet())).thenReturn(enrolledPlayers);

        enrollPlayerViewModel = new EnrollPlayerViewModel(repository);
    }

    @Test
    public void getEnrolledPlayers_twoEnrolledPlayers_returnsEnrolledPlayers() {
        // Given
        List<Player> enrolledPlayers = new ArrayList<>();

        // When
        try {
            enrolledPlayers = LiveDataTestUtil.getOrAwaitValue(enrollPlayerViewModel.getEnrolledPlayers());
        } catch (InterruptedException ex) {}

        // Then
        assertEquals(playerOne, enrolledPlayers.get(0));
        assertEquals(playerTwo, enrolledPlayers.get(1));
    }

    @Test
    public void getAvailablePlayers_fourTotalPlayers_returnsAvailablePlayers() {
        // Given
        Player playerThree = new Player();
        Player playerFour = new Player();

        playerThree.id = idGenerator.getRandomId();
        playerFour.id = idGenerator.getRandomId();

        MutableLiveData<List<Player>> availablePlayers = new MutableLiveData<>();
        availablePlayers.setValue(Lists.newArrayList(playerOne, playerTwo, playerThree, playerFour));

        when(repository.getAllPlayers()).thenReturn(availablePlayers);

        List<Player> actualPlayers = new ArrayList<>();

        // When
        try {
            actualPlayers = LiveDataTestUtil.getOrAwaitValue(enrollPlayerViewModel.getAvailablePlayers());
        } catch (InterruptedException ex) {}

        // Then
        assertEquals(2, actualPlayers.size());
        assertEquals(playerThree, actualPlayers.get(0));
        assertEquals(playerFour, actualPlayers.get(1));
    }

    @Test
    public void getTotalEnrolled_twoEnrolledPlayers_returnsTwo() {
        // When
        int actualEnrolled = enrollPlayerViewModel.getTotalEnrolled();

        // Then
        assertEquals(2, actualEnrolled);
    }

    @Test
    public void saveEnrolledPlayers_repositoryCalled() {
        // When
        enrollPlayerViewModel.saveEnrolledPlayers();

        // Then
        verify(repository, times(1)).saveEnrolledPlayers(anyList());
    }

    @Test
    public void enrollAllPlayers_twoPlayersAvailable_fourPlayersEnrolled() {
        // Given
        Player playerThree = new Player();
        Player playerFour = new Player();

        playerThree.id = idGenerator.getRandomId();
        playerThree.playerName = "C";
        playerFour.id = idGenerator.getRandomId();
        playerFour.playerName = "D";

        MutableLiveData<List<Player>> availablePlayers = new MutableLiveData<>();
        availablePlayers.setValue(Lists.newArrayList(playerOne, playerTwo, playerThree, playerFour));

        when(repository.getAllPlayers()).thenReturn(availablePlayers);

        try {
            LiveDataTestUtil.getOrAwaitValue(enrollPlayerViewModel.getAvailablePlayers());
        } catch (InterruptedException ex) {}

        // When
        enrollPlayerViewModel.enrollAllPlayers();

        // Then
        List<Player> enrolledPlayers = new ArrayList<>();
        List<Player> remainingPlayers = new ArrayList<>();

        try {
            enrolledPlayers = LiveDataTestUtil.getOrAwaitValue(enrollPlayerViewModel.getEnrolledPlayers());
        } catch (InterruptedException ex) {}

        try {
            remainingPlayers = LiveDataTestUtil.getOrAwaitValue(enrollPlayerViewModel.getAvailablePlayers());
        } catch (InterruptedException ex) {}

        assertEquals(4, enrolledPlayers.size());
        assertEquals(0, remainingPlayers.size());
        assertEquals(playerOne, enrolledPlayers.get(0));
        assertEquals(playerTwo, enrolledPlayers.get(1));
        assertEquals(playerThree, enrolledPlayers.get(2));
        assertEquals(playerFour, enrolledPlayers.get(3));
    }
}