import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.southernsoft.tcgtournament.LocalDatabase;
import com.southernsoft.tcgtournament.MainActivity;
import com.southernsoft.tcgtournament.R;
import com.southernsoft.tcgtournament.entity.Player;
import com.southernsoft.tcgtournament.util.Lists;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBackUnconditionally;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.CoreMatchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EndToEndTest {
    @Rule
    public ActivityScenarioRule<MainActivity> rule = new ActivityScenarioRule<>(MainActivity.class);

    private String p1Name = "Alucard";
    private String p2Name = "Richter";
    private String p3Name = "Miriam";

    @Before
    public void setup() {
        LocalDatabase database = LocalDatabase.getDatabase(getInstrumentation().getTargetContext());
        database.tournamentDao().deleteAll();
        database.playerDao().deleteAll();

        Player playerOne = new Player();
        playerOne.playerName = "Alucard";

        Player playerTwo = new Player();
        playerTwo.playerName = "Richter";

        database.playerDao().insert(Lists.newArrayList(playerOne, playerTwo));
    }

    @Test
    public void completeFlow() {
        onView(withId(R.id.new_tournament)).perform(click());

        // Block -- Check New Tournament screen
        onView(withId(R.id.number_players)).check(matches(withText("0")));
        onView(withId(R.id.number_rounds)).check(matches(withText("0")));
        onView(withId(R.id.round_time)).check(matches(withText("50")));

        onView(withId(R.id.enroll)).perform(click());

        // Block -- Enroll players screen
        onView(withId(R.id.available_count)).check(matches(withText("2")));
        onView(withId(R.id.enrolled_count)).check(matches(withText("0")));

        onView(withText(p1Name)).check(matches(isDisplayed()));
        onView(withText(p2Name)).check(matches(isDisplayed()));

        onView(withId(R.id.editor_menu_create_button)).perform(click());

        //Block -- Add new player screen
        onView(withId(R.id.player_name)).perform(typeText(p3Name));
        onView(withId(R.id.editor_menu_save_button)).perform(click());

        // Block -- Enroll players screen -- Enroll all players
        onView(withText(p3Name)).perform(click());
        onView(withId(R.id.enroll_all)).perform(click());

        onView(withId(R.id.available_count)).check(matches(withText("0")));
        onView(withId(R.id.enrolled_count)).check(matches(withText("3")));

        onView(withId(R.id.confirm)).perform(click());

        // Block -- New Tournament -- Set up tournament
        onView(withId(R.id.increment_time)).perform(click());
        onView(withId(R.id.increment_time)).perform(click());

        onView(withId(R.id.number_players)).check(matches(withText("3")));
        onView(withId(R.id.number_rounds)).check(matches(withText("2")));
        onView(withId(R.id.round_time)).check(matches(withText("70")));

        onView(withId(R.id.start)).perform(click());

        // Block -- Pairings View -- Start first Round
        onView(withId(R.id.toolbar_round_number)).check(matches(withText("1")));
        onView(withId(R.id.toolbar_round_time)).check(matches(withText("70:00")));
        onView(withText("Bye")).check(matches(isDisplayed()));
        onView(withId(R.id.button_start_round)).check(matches(isDisplayed()));
        onView(withId(R.id.button_finish_round)).check(matches(not(isDisplayed())));
        onView(withId(R.id.button_pause_round)).check(matches(not(isDisplayed())));

        onView(withId(R.id.button_start_round)).perform(click());

        onView(withId(R.id.button_start_round)).check(matches(not(isDisplayed())));
        onView(withId(R.id.button_finish_round)).check(matches(isDisplayed()));
        onView(withId(R.id.button_pause_round)).check(matches(isDisplayed()));

        onView(withId(R.id.button_finish_round)).perform(click());
        onView(withText(R.string.continues)).perform(click());

        // Block -- Pairings View -- Round 2
        onView(withId(R.id.toolbar_round_number)).check(matches(withText("2")));
        onView(withId(R.id.toolbar_round_time)).check(matches(withText("70:00")));
        onView(withId(R.id.button_start_round)).check(matches(isDisplayed()));
        onView(withId(R.id.button_finish_round)).check(matches(not(isDisplayed())));
        onView(withId(R.id.button_pause_round)).check(matches(not(isDisplayed())));

        onView(withId(R.id.button_start_round)).perform(click());
        onView(withId(R.id.button_finish_round)).perform(click());
        onView(withText(R.string.continues)).perform(click());

        onView(withId(R.id.points)).check(matches(isDisplayed()));
        onView(withId(R.id.omw)).check(matches(isDisplayed()));

        pressBackUnconditionally();

        onView(withText(R.string.keep)).perform(click());

        // Block -- Tournaments View -- Delete tournament
        onView(withId(R.id.tournament_manager)).perform(click());
        onView(withId(R.id.players)).perform(click());
        onView(withText(R.string.delete)).perform(click());
        onView(withText(R.string.confirm)).perform(click());

        onView(withText(R.string.no_tournaments_found)).check(matches(isDisplayed()));
    }
}