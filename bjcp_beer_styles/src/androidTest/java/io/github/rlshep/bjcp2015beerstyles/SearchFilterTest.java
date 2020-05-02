package io.github.rlshep.bjcp2015beerstyles;


import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.rlshep.bjcp2015beerstyles.matchers.Matchers;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SearchFilterTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void searchFilterTest() {
        ViewInteraction textView = onView(
                allOf(withText("   "),
                        Matchers.childAtPosition(
                                Matchers.childAtPosition(
                                        withId(R.id.tabs),
                                        0),
                                3),
                        isDisplayed()));
        textView.perform(click());

        ViewInteraction viewPager = onView(
                allOf(withId(R.id.pager),
                        Matchers.childAtPosition(
                                Matchers.childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                2),
                        isDisplayed()));
        viewPager.perform(swipeLeft());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.filterSearch), withText(R.string.search),
                        Matchers.childAtPosition(
                                Matchers.childAtPosition(
                                        withClassName(is("android.widget.TableLayout")),
                                        0),
                                1),
                        isDisplayed()));
        appCompatButton.perform(click());
    }
}
