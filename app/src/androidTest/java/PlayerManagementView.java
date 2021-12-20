import androidx.test.espresso.ViewInteraction;
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

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBackUnconditionally;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withResourceName;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.CoreMatchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class PlayerManagementView {
    @Rule
    public ActivityScenarioRule<MainActivity> rule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setup() {
        LocalDatabase database = LocalDatabase.getDatabase(getInstrumentation().getTargetContext());
        database.tournamentDao().deleteAll();
        database.playerDao().deleteAll();
    }

    @Test
    public void createsTwoPlayers_usesSearchBar_enablesActionMode_deletesOnePlayer() {
        final String p1Name = "Bruce";
        final String p2Name = "Bruce Dickinson";
        final String p2UpdatedName = "Bruce Lee";
        final String p1Id = "1234567678";
        final String p2Id = "9876543666";
        final String p1IdHyphens = "1234-567-678";
        final String p2IdHyphens = "9876-543-666";
        ViewInteraction fab = onView(withId(R.id.fab));

        onView(withId(R.id.player_management)).perform(click());

        // Block -- Create one player
        onView(withId(R.id.no_players_found)).check(matches(isDisplayed()));
        fab.check(matches(isDisplayed()));

        fab.perform(click());

        onView(withId(R.id.player_name)).perform(typeText(p1Name));
        onView(withId(R.id.player_identification)).perform(typeText(p1Id));
        onView(withId(R.id.editor_menu_save_button)).perform(click());

        onView(withId(R.id.no_players_found)).check(matches(not(isDisplayed())));

        // Block -- Create another player
        fab.perform(click());

        onView(withId(R.id.player_name)).perform(typeText(p2Name));
        onView(withId(R.id.player_identification)).perform(typeText(p2Id));
        onView(withId(R.id.editor_menu_save_button)).perform(click());

        onView(withText(p1Name)).check(matches(isDisplayed()));
        onView(withText(p1IdHyphens)).check(matches(isDisplayed()));
        onView(withText(p2Name)).check(matches(isDisplayed()));
        onView(withText(p2IdHyphens)).perform(click());

        // Block -- updates player name
        onView(withId(R.id.player_name)).perform(clearText());
        onView(withId(R.id.player_name)).perform(typeText(p2UpdatedName));
        onView(withId(R.id.editor_menu_save_button)).perform(click());

        onView(withText(p1Name)).check(matches(isDisplayed()));
        onView(withText(p1IdHyphens)).check(matches(isDisplayed()));
        onView(withText(p2UpdatedName)).check(matches(isDisplayed()));
        onView(withText(p2IdHyphens)).check(matches(isDisplayed()));
        onView(withText(p2Name)).check(doesNotExist());

        // Block -- uses search bar
        onView(withId(R.id.app_bar_search)).perform(click());
        onView(withResourceName("search_src_text")).perform(typeText("Bruce L"));

        fab.check(matches(not(isDisplayed())));
        onView(withText(p1Name)).check(doesNotExist());
        onView(withText(p1IdHyphens)).check(doesNotExist());
        onView(withText(p2UpdatedName)).check(matches(isDisplayed()));
        onView(withText(p2IdHyphens)).check(matches(isDisplayed()));

        // Block -- enables action mode and deletes player
        onView(withText(p2UpdatedName)).perform(longClick());
        onView(withId(R.id.delete_player)).perform(click());

        onView(withText(p2UpdatedName)).check(doesNotExist());

        // Block -- closes action mode and search bar
        pressBackUnconditionally(); // Close action mode
        pressBackUnconditionally(); // Clears search bar text

        fab.check(matches(isDisplayed()));
        onView(withText(p1Name)).check(matches(isDisplayed()));
        onView(withText(p1IdHyphens)).check(matches(isDisplayed()));
        onView(withText(p2UpdatedName)).check(doesNotExist());
        onView(withText(p2IdHyphens)).check(doesNotExist());
    }
}