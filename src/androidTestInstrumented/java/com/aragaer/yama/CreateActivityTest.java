package com.aragaer.yama;

import android.content.Context;
import android.content.Intent;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.*;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;


@LargeTest
public class CreateActivityTest extends InstrumentationTestCase {

    private UiDevice uidevice;

    @Before public void setUp() {
	uidevice = UiDevice.getInstance(getInstrumentation());
    }

    @Test public void testBackstack() throws Exception {
	goHome();
	launchCreateActivity();
	new UiObject(new UiSelector().text("Done")).click();
	goHome();
	launchCreateActivity();
	assertTrue(new UiObject(new UiSelector().resourceId("com.aragaer.yama:id/new_memo_edit")).exists());
    }

    private void goHome() {
	uidevice.pressHome();
    }

    private void launchCreateActivity() throws Exception {
	new UiObject(new UiSelector().description("Apps")).clickAndWaitForNewWindow();
	new UiObject(new UiSelector().text("Apps")).click();
	UiScrollable ListOfapplications = new UiScrollable(new UiSelector().scrollable(true));
	UiObject create = ListOfapplications.getChildByText(new UiSelector().className(android.widget.TextView.class.getName()), "create memo");
	create.clickAndWaitForNewWindow();
    }
}
