package com.aragaer.yama;
// vim: et ts=4 sts=4 sw=4

import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;


@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk=18)
public class CreateMemoTest {

    @Test public void testFail() {
	fail();
    }
}
