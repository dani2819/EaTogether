package com.applicoders.msp_2017_project.eatogether;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void loginActivityTest() {
        ViewInteraction tj = onView(
                allOf(withText("Sign In"),
                        withParent(allOf(withId(R.id.google_sign_in_button),
                                withParent(withId(R.id.email_login_form))))));
        tj.perform(scrollTo(), click());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.searchNearby), withText("Search for nearby neighbors"), isDisplayed()));
        appCompatButton.perform(click());

        pressBack();

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.searchNearby), withText("Search for nearby neighbors"), isDisplayed()));
        appCompatButton2.perform(click());

        pressBack();

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.searchNearby), withText("Search for nearby neighbors"), isDisplayed()));
        appCompatButton3.perform(click());

    }

}
