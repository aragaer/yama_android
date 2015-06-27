package com.aragaer.yama;

import java.io.*;
import java.util.*;

import android.content.Context;
import android.support.test.espresso.DataInteraction;
import android.support.test.filters.FlakyTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;
import android.view.KeyEvent;

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
	    .perform(replaceText("  Have a cup of Espresso.  \n"));

	onView(withText("Cancel")).perform(click());

	assertThat(readMemosFile(),
		   equalTo(Arrays.asList("* ", "  Some initial memo",
					 "* ", "  Two of them")));
    }

    @Test public void editMemo() throws Exception {
	onFirstItemEditText().check(matches(withText("Some initial memo")));
	onFirstItemEditText().perform(click());
	onFirstItemEditText().perform(replaceText("Edited now"));
	onEditText(1).perform(click());

	assertThat(readMemosFile(),
		   equalTo(Arrays.asList("* ", "  Edited now",
					 "* ", "  Two of them")));

	mActivityRule.getActivity().finish();
	mActivityRule.launchActivity(null);
	checkMemos("Edited now", "Two of them");
    }

    @Test @Ignore public void editInsertNewMemo() throws Exception {
	onFirstItemEditText().perform(click());
	onFirstItemEditText().check(matches(isDisplayed()));
	onFirstItemEditText().check(matches(withText("Some initial memo")));
	// FIXME: should not work like this
	// typeText will be needed to invoke the new memo insertion
	onFirstItemEditText()
	    .perform(replaceText("\n This is a new memo.\n\n  \nAnd one more."));

	checkOneMemo("This is a new memo.", 0);
	checkOneMemo("And one more.", 1);
	assertThat(readMemosFile(),
		   equalTo(Arrays.asList("* ", "  This is a new memo.",
					 "* ", "  And one more.",
					 "* ", "  Two of them")));
    }

    @Test public void pauseSavesEdit() throws Exception {
	onFirstItemEditText().check(matches(withText("Some initial memo")));
	onFirstItemEditText().perform(click());
	onFirstItemEditText().perform(replaceText("This is a new memo."));

	mActivityRule.getActivity().finish();
	mActivityRule.launchActivity(null);

	assertThat(readMemosFile(),
		   equalTo(Arrays.asList("* ", "  This is a new memo.",
					 "* ", "  Two of them")));

	checkMemos("This is a new memo.",
		   "Two of them");
    }

    @Test public void eraseMemo() throws Exception {
	onFirstItemEditText().check(matches(withText("Some initial memo")));
	onFirstItemEditText().perform(click());
	onFirstItemEditText().perform(replaceText("  \n\n \n "));

	mActivityRule.getActivity().finish();
	mActivityRule.launchActivity(null);

	checkMemos("Two of them");

	assertThat(readMemosFile(),
		   equalTo(Arrays.asList("* ", "  Two of them")));
    }

    @Test @Ignore public void joinMemoToNext() {
	onFirstItemEditText().perform(click(), clearText(), pressKey(KeyEvent.KEYCODE_FORWARD_DEL));
	onView(allOf(hasFocus(), withId(R.id.text))).check(matches(withText("Two of them")));
	checkMemos("Two of them");
    }

    @Test @Ignore public void deleteMemo() throws Exception {
	onFirstItemEditText();
	onView(withId(R.id.text)).check(matches(withText("Some initial memo")));
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

    @Test public void rotationDoesntAddStuff() {
	onView(isRoot()).perform(orientationLandscape());
	onView(isRoot()).perform(orientationPortrait());

	// verify there's only one button - will fail with multiple buttons
	onView(withId(R.id.new_memo_btn)).check(matches(isDisplayed()));
    }

    private DataInteraction onEditText(int position) {
	return onData(anything())
	    .inAdapterView(withId(R.id.memo_list))
	    .atPosition(position)
	    .onChildView(withId(R.id.text));
    }

    private DataInteraction onFirstItemEditText() {
	return onEditText(0);
    }

    private void checkOneMemo(String text, int position) {
	onEditText(position)
	    .check(matches(withText(text)));
    }

    private void checkMemos(String... lines) {
	for (int i = 0; i < lines.length; i++)
	    checkOneMemo(lines[i], i);
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
