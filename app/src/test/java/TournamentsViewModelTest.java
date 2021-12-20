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
import com.southernsoft.tcgtournament.pojo.TournamentAndRound;
import com.southernsoft.tcgtournament.tournaments.TournamentsViewModel;
import util.LiveDataTestUtil;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TournamentsViewModelTest {
    @Rule public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Mock private Repository repository;

    private TournamentsViewModel tournamentsViewModel;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        tournamentsViewModel = new TournamentsViewModel(repository);
    }

    @Test
    public void getTournaments_returnsTournamentsList() {
        // Given
        MutableLiveData<List<TournamentAndRound>> mockedLiveData = new MutableLiveData<>();
        List<TournamentAndRound> mockedTournaments = new ArrayList<>();
        List<TournamentAndRound> actualTournaments = new ArrayList<>();

        mockedLiveData.setValue(mockedTournaments);
        when(repository.getTournamentsWithLastRound()).thenReturn(mockedLiveData);

        // When
        try {
            actualTournaments = LiveDataTestUtil.getOrAwaitValue(tournamentsViewModel.getTournaments());
        } catch (InterruptedException ex) {}

        // Then
        assertEquals(mockedTournaments, actualTournaments);
        verify(repository, times(1)).getTournamentsWithLastRound();
    }

    @Test
    public void removeTournament_idProvided_repositoryCalled() {
        // Given
        int tournamentId = 2;

        // When
        tournamentsViewModel.removeTournament(tournamentId);

        // Then
        verify(repository, times(1)).deleteTournament(tournamentId);
        verify(repository, times(1)).deleteTournamentFile(tournamentId);
    }
}