package com.aragaer.yama;
// vim: et ts=4 sts=4 sw=4

import java.util.Arrays;

import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.tester.android.view.TestMenuItem;
import org.robolectric.util.ActivityController;

import android.content.Intent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

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

    @Test public void shouldResetBeforeReading() {
	ActivityController<MemoListActivity> controller =
	        Robolectric.buildActivity(MemoListActivity.class);
	controller.create();
	MemoFile.append(controller.get(), new String[] {"test"});
	controller.start();
	controller.resume();
	controller.pause();
	MemoFile.append(controller.get(), new String[] {"test2"});
	controller.resume();
	ListView list = (ListView) controller.get().findViewById(R.id.memo_list);
	ListAdapter adapter = list.getAdapter();
	assertThat(adapter.getCount(), equalTo(2));
	assertThat((String) adapter.getItem(0), equalTo("test"));
	assertThat((String) adapter.getItem(1), equalTo("test2"));
    }
}
