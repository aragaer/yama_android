package com.aragaer.yama;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;

import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import static android.content.Context.MODE_PRIVATE;


@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk=18)
public class AndroidFileProviderTest {

    private AndroidFileProvider fileProvider;

    @Before public void setUp() {
	fileProvider = new AndroidFileProvider(Robolectric.application);
    }

    @Test public void shouldListExistingFiles() throws Exception {
	List<String> files = fileProvider.fileList();
	assertThat(files.size(), equalTo(0));

	for (int i = 0; i < 10; i++) {
	    OutputStream stream = Robolectric.application.openFileOutput("file"+i,
									 MODE_PRIVATE);
	    stream.write("text".getBytes());
	}

	files = fileProvider.fileList();
	assertThat(files.size(), equalTo(10));
    }

    @Test public void shouldOpenFileForWriting() throws Exception {
	OutputStream stream = fileProvider.openFileForWriting("file");
	stream.write("text".getBytes());
	stream.close();

	List<String> files = fileProvider.fileList();
	assertThat(files.size(), equalTo(1));
	assertThat(files.get(0), equalTo("file"));

	byte[] buffer = new byte[80];
	InputStream is = Robolectric.application.openFileInput("file");
	int read = is.read(buffer, 0, 80);
	is.close();
	assertThat(read, equalTo(4));
	assertThat(Charset.defaultCharset().decode(ByteBuffer.wrap(buffer, 0, read)).toString(), equalTo("text"));
    }

    @Test public void shouldOpenFileForReading() throws Exception {
	OutputStream stream = Robolectric.application.openFileOutput("file", MODE_PRIVATE);
	stream.write("text".getBytes());
	stream.close();

	byte[] buffer = new byte[80];
	InputStream is = fileProvider.openFileForReading("file");
	int read = is.read(buffer, 0, 80);
	is.close();
	assertThat(read, equalTo(4));
	assertThat(Charset.defaultCharset().decode(ByteBuffer.wrap(buffer, 0, read)).toString(), equalTo("text"));
    }
}
