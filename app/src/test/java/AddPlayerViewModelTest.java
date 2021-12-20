import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.southernsoft.tcgtournament.Repository;
import com.southernsoft.tcgtournament.addplayer.AddPlayerViewModel;
import com.southernsoft.tcgtournament.entity.Player;
import util.RandomIdGenerator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AddPlayerViewModelTest {
    @Rule public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Mock private Repository repository;

    private AddPlayerViewModel addPlayerViewModel;
    private RandomIdGenerator idGenerator;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        addPlayerViewModel = new AddPlayerViewModel(repository);
        idGenerator = new RandomIdGenerator();
    }

    @Test
    public void insertPlayer_dataProvided_repositoryCalled() {
        // Given
        int playerId = idGenerator.getRandomId();
        String name = "TestPlayer";
        String identification = "1234-567-890";

        // When
        addPlayerViewModel.insertPlayer(playerId, name, identification);

        // Then
        verify(repository, times(1)).insertPlayer(any(Player.class));
    }
}