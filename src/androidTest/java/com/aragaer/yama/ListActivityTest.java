package com.aragaer.yama;

import java.io.*;
import java.util.*;

import android.content.Context;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.filters.FlakyTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.BaseMatcher;
import org.junit.*;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.*;
import static junit.framework.Assert.assertNotNull;

import static com.aragaer.yama.OrientationChangeAction.*;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class ListActivityTest {

    @Rule public ActivityTestRule<MemoListActivity> mActivityRule =
	    new ActivityTestRule<MemoListActivity>(MemoListActivity.class);

    private void writeMemosFile(List<String> lines) throws Exception {
	OutputStream file = mActivityRule.getActivity().openFileOutput("memo",
	       Context.MODE_PRIVATE | Context.MODE_APPEND);
	for (String line : lines) {
	    file.write(line.getBytes());
	    file.write("\n".getBytes());
	}
	file.close();
    }

    private List<String> readMemosFile() throws Exception {
	List<String> result = new LinkedList<String>();
	InputStream file = mActivityRule.getActivity().openFileInput("memo.org");
	BufferedReader reader = new BufferedReader(new InputStreamReader(file));
	while (true) {
	    String line = reader.readLine();
	    if (line == null)
		break;
	    result.add(line);
	}
	file.close();
	return result;
    }

    @Before public void setUp() {
	try {
	    mActivityRule.getActivity().deleteFile("memo");
	    writeMemosFile(Arrays.asList("Some initial memo", "Two of them"));
	} catch (Exception e) {
	    Log.e("YAMA test", "Failed to write", e);
	}
	mActivityRule.getActivity().finish();
	mActivityRule.launchActivity(null);
    }

    @After public void tearDown() {
	mActivityRule.getActivity().deleteFile("memo");
	mActivityRule.getActivity().deleteFile("memo.org");
    }

    @Test public void shouldDisplayMemos() {
	checkMemos("Some initial memo", "Two of them");
    }

    @Test public void canCreateNewMemo() throws Exception {
	onView(withId(R.id.new_memo_btn)).perform(click());
	onView(withId(R.id.new_memo_edit)).check(matches(isDisplayed()));
	onView(withId(R.id.new_memo_edit))
	    .perform(replaceText("\n"
				 +"  Have a cup of Espresso.  \n"
				 +"\n"
				 +"  \n"
				 +"\n"
				 +"Write a second test.  \n"
				 +"\n"));
	android.support.test.espresso.Espresso.closeSoftKeyboard();
	android.support.test.espresso.Espresso.pressBack();

	onView(withId(R.id.memo_list)).check(matches(isDisplayed()));
	checkMemos("Some initial memo",
		   "Two of them",
		   "Have a cup of Espresso.",
		   "Write a second test.");

	assertThat(readMemosFile(),
		   equalTo(Arrays.asList("* ", "  Some initial memo",
					 "* ", "  Two of them",
					 "* ", "  Have a cup of Espresso.",
					 "* ", "  Write a second test.")));
    }

    @Test public void discardsNewMemoIfCancelled() throws Exception {
	onView(withId(R.id.new_memo_btn)).perform(click());
	onView(withId(R.id.new_memo_edit)).check(matches(isDisplayed()));
	onView(withId(R.id.new_memo_edit))
	    .perform(typeText("  Have a cup of Espresso.  \n"));

	onView(withText("Cancel")).perform(click());

	assertThat(readMemosFile(),
		   equalTo(Arrays.asList("* ", "  Some initial memo",
					 "* ", "  Two of them")));
    }

    @Test public void editMemo() throws Exception {
	clickFirstItem();
	onView(withId(R.id.memo_edit)).check(matches(isDisplayed()));
	onView(withId(R.id.memo_edit)).check(matches(withText("Some initial memo")));
	onView(withId(R.id.memo_edit))
	    .perform(replaceText("\n This is a new memo.\n\n  \nAnd one more."));
	onView(withText("Done")).perform(click());

	checkToast("Saved");

	checkOneMemo("This is a new memo.", 0);
	checkOneMemo("And one more.", 1);
	assertThat(readMemosFile(),
		   equalTo(Arrays.asList("* ", "  This is a new memo.",
					 "* ", "  And one more.",
					 "* ", "  Two of them")));
    }

    @Test public void cancelEdit() throws Exception {
	clickFirstItem();
	onView(withId(R.id.memo_edit)).check(matches(isDisplayed()));
	onView(withId(R.id.memo_edit))
	    .perform(replaceText("\n This is a new memo.\n\n  \nAnd one more."));
	onView(withText("Cancel")).perform(click());
	checkOneMemo("Some initial memo", 0);
	checkOneMemo("Two of them", 1);

	assertThat(readMemosFile(),
		   equalTo(Arrays.asList("* ", "  Some initial memo",
					 "* ", "  Two of them")));
    }

    @Test public void backSavesEdit() throws Exception {
	clickFirstItem();
	onView(withId(R.id.memo_edit)).check(matches(withText("Some initial memo")));
	onView(withId(R.id.memo_edit))
	    .perform(replaceText("\n This is a new memo.\n\n  \nAnd one more."));

	android.support.test.espresso.Espresso.closeSoftKeyboard();
	android.support.test.espresso.Espresso.pressBack();

	checkMemos("This is a new memo.",
		   "And one more.",
		   "Two of them");
	assertThat(readMemosFile(),
		   equalTo(Arrays.asList("* ", "  This is a new memo.",
					 "* ", "  And one more.",
					 "* ", "  Two of them")));
    }

    @Test public void eraseMemo() throws Exception {
	clickFirstItem();
	onView(withId(R.id.memo_edit)).check(matches(withText("Some initial memo")));
	onView(withId(R.id.memo_edit))
	    .perform(replaceText("\n \n\n   \n\n"));
	onView(withText("Done")).perform(click());

	checkToast("Deleted");
	checkMemos("Two of them");

	assertThat(readMemosFile(),
		   equalTo(Arrays.asList("* ", "  Two of them")));
    }

    @Test public void deleteMemo() throws Exception {
	clickFirstItem();
	onView(withId(R.id.memo_edit)).check(matches(withText("Some initial memo")));
	onView(withText("Delete")).perform(click());
	checkOneMemo("Two of them", 0);

	onView(withText("Deleted"))
	    .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView())))
	    .check(matches(isDisplayed()));

	assertThat(readMemosFile(),
		   equalTo(Arrays.asList("* ", "  Two of them")));
    }

    @Test public void scrollToEnd() throws Exception {
	String text = "p\n";
	for (int i = 0; i < 50; i++)
	    text += "x\n";
	text += "y";
	onView(withId(R.id.new_memo_btn)).perform(click());
	onView(withId(R.id.new_memo_edit))
	    .perform(replaceText(text));
	onView(withText(R.string.action_save)).perform(click());

	onView(withText("y")).check(matches(isDisplayed()));
    }

    @Test public void showEdited() throws Exception {
	String text = "";
	for (int i = 0; i < 20; i++)
	    text += "x\n";
	text += "y\n";
	for (int i = 0; i < 20; i++)
	    text += "x\n";
	onView(withId(R.id.new_memo_btn)).perform(click());
	onView(withId(R.id.new_memo_edit))
	    .perform(replaceText(text));
	onView(withText(R.string.action_save)).perform(click());

	onData(isMemoWithText("y"))
	    .inAdapterView(withId(R.id.memo_list))
	    .perform(click());

	onView(withId(R.id.memo_edit)).perform(replaceText("z"));
	onView(withText(R.string.action_save)).perform(click());

	onView(withText("z")).check(matches(isDisplayed()));
    }

    @Test public void scrollToEndOnStart() throws Exception {
	String text = "";
	for (int i = 0; i < 50; i++)
	    text += "x\n";
	text += "y";
	onView(withId(R.id.new_memo_btn)).perform(click());
	onView(withId(R.id.new_memo_edit))
	    .perform(replaceText(text));
	mActivityRule.getActivity().finish(); // force restart of main activity
	onView(withText(R.string.action_save)).perform(click());

	onView(withText("y")).check(matches(isDisplayed()));
    }


    @FlakyTest
    @Test public void toasts() {
	onView(withId(R.id.new_memo_btn)).perform(click());
	onView(withId(R.id.new_memo_edit)).perform(replaceText("abcd"));
	onView(withText("Done")).perform(click());

	checkToast("Saved");
    }

    @Test public void rotationDoesntAddStuff() {
	onView(isRoot()).perform(orientationLandscape());
	onView(isRoot()).perform(orientationPortrait());

	// verify there's only one button - will fail with multiple buttons
	onView(withId(R.id.new_memo_btn)).perform(click());
    }
    
    private void clickFirstItem() {
	onData(anything())
	    .inAdapterView(withId(R.id.memo_list))
	    .atPosition(0)
	    .perform(click());
    }

    private void checkOneMemo(String text, int position) {
	onData(anything())
	    .inAdapterView(withId(R.id.memo_list))
	    .atPosition(position)
	    .check(matches(withText(text)));
    }

    private void checkMemos(String... lines) {
	for (int i = 0; i < lines.length; i++)
	    checkOneMemo(lines[i], i);
    }

    private void checkToast(String toast) {
	onView(withText(toast))
	    .inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView())))
	    .check(matches(isDisplayed()));
    }

    private Matcher<Object> isMemoWithText(final String text) {
	return new BaseMatcher<Object>() {
	    @Override public boolean matches(Object memo) {
		return ((Memo) memo).getText().equals(text);
	    }

	    @Override public void describeTo(Description description) {
		description.appendText("Memo should be ").appendValue(text);
	    }
	};
    }
}
