package com.aragaer.yama;

import java.io.*;
import java.util.*;

import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.*;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;

import static org.hamcrest.CoreMatchers.equalTo;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class CreateActivityTest {

    @Rule public ActivityTestRule<MemoCreateActivity> mActivityRule =
	    new ActivityTestRule<MemoCreateActivity>(MemoCreateActivity.class);

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

    private void writeMemosFile(List<String> lines) throws Exception {
	OutputStream file = mActivityRule.getActivity().openFileOutput("memo",
	       Context.MODE_PRIVATE | Context.MODE_APPEND);
	for (String line : lines) {
	    file.write(line.getBytes());
	    file.write("\n".getBytes());
	}
	file.close();
    }

    @After public void tearDown() {
	mActivityRule.getActivity().deleteFile("memo");
    }

    @Test public void shouldAppendTrimmedMemosToFile() throws Exception {
	writeMemosFile(Arrays.asList("Some initial memo", "Two of them"));

	onView(supportsInputMethods())
	    .perform(typeText("\n"
			      +"  Have a cup of Espresso.  \n"
			      +"\n"
			      +"  \n"
			      +"\n"
			      +"Write a second test.  \n"
			      +"\n"));
	onView(withText("Done")).perform(click());

	assertThat(readMemosFile(),
		   equalTo(Arrays.asList("Some initial memo",
					 "Two of them",
					 "Have a cup of Espresso.",
					 "Write a second test.")));
    }

    @Test public void shouldDiscardIfNotSaved() throws Exception {
	writeMemosFile(Arrays.asList("Some initial memo", "Two of them"));

	onView(supportsInputMethods()).perform(typeText("Have a cup of Espresso."));
	onView(withText("Cancel")).perform(click());

	assertThat(readMemosFile(),
		   equalTo(Arrays.asList("Some initial memo",
					 "Two of them")));
    }
}
