package com.aragaer.yama;

import java.io.OutputStream;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowContentResolver;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;


@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk=18)
public class MemoProviderTest {

    private MemoProvider provider;
    private ContentResolver resolver;
    private ShadowContentResolver shadowResolver;

    @Test public void initializeProvider() throws Exception {
	OutputStream output = Robolectric.application.openFileOutput("2015-05-11.txt",
								     Context.MODE_PRIVATE);
	output.write("* \n  line1\n  line2\n* \n  Other memo\n".getBytes());
	provider = new MemoProvider();
	resolver = Robolectric.application.getContentResolver();
	shadowResolver = Robolectric.shadowOf(resolver);
	provider.onCreate();
	shadowResolver.registerProvider(MemoProvider.AUTHORITY, provider);

	Cursor cursor = shadowResolver.query(MemoProvider.MEMOS_URI, null, null, null, null);

	assertThat(cursor.getCount(), equalTo(2));
    }
}
