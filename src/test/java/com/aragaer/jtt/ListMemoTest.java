package com.aragaer.yama;
// vim: et ts=4 sts=4 sw=4

import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.tester.android.view.TestMenuItem;

import android.view.View;
import android.content.Intent;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;


@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk=18)
public class ListMemoTest {

    @Test public void shouldAllowCreateNewMemo() {
	MemoListActivity activity = Robolectric.setupActivity(MemoListActivity.class);
	activity.onOptionsItemSelected(new TestMenuItem(R.id.new_memo_btn));

        Intent expectedIntent = new Intent(activity, MemoCreateActivity.class);
        assertThat(Robolectric.shadowOf(activity).getNextStartedActivity(),
		   equalTo(expectedIntent));
    }
}
