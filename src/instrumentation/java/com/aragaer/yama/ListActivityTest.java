package com.aragaer.yama;

import java.io.*;
import java.util.*;

import android.content.Context;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.*;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.*;
import static junit.framework.Assert.assertNotNull;


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
	InputStream file = mActivityRule.getActivity().openFileInput("memo");
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
    }

    @Test public void shouldDisplayMemos() {
	checkOneMemo("Some initial memo", 0);
	checkOneMemo("Two of them", 1);
    }

    @Test public void canCreateNewMemo() throws Exception {
	onView(withId(R.id.new_memo_btn)).perform(click());
	onView(withId(R.id.new_memo_edit)).check(matches(isDisplayed()));
	onView(withId(R.id.new_memo_edit)).perform(typeText("A memo"));
	android.support.test.espresso.Espresso.closeSoftKeyboard();
	android.support.test.espresso.Espresso.pressBack();
	assertThat(readMemosFile(),
		   equalTo(Arrays.asList("Some initial memo",
					 "Two of them",
					 "A memo")));
	checkOneMemo("A memo", 2);
    }

    private void checkOneMemo(String text, int position) {
	onData(anything())
	    .inAdapterView(withId(R.id.memo_list))
	    .atPosition(position)
	    .check(matches(withText(text)));
    }
}
