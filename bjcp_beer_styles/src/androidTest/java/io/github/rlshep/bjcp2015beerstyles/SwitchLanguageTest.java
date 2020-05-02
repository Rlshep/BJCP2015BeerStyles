package io.github.rlshep.bjcp2015beerstyles;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import io.github.rlshep.bjcp2015beerstyles.matchers.Matchers;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

// Run with animations and transitions disabled
@LargeTest
@RunWith(Parameterized.class)
public class SwitchLanguageTest {
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"en", "Beer Styles", "Introductions", "Press", "Straw", "Search", "Results", "Festbier"},
                {"es", "Estilos de Cerveza", "Introducción", "Mantenga", "Paja", "Buscar", "Resultados", "Festbier"},
                {"uk", "Стилевий довідник", "Вступ", "Натисніть", "Солом’яний", "Пошук", "Результати", "Фестбір / Festbier"}});
    }

    private final String lang;
    private final String appTitle;
    private final String categoryText;
    private final String bookmarkedText;
    private final String colorStraw;
    private final String searchButtonText;
    private final String resultsTitle;
    private final String festbier;

    public SwitchLanguageTest(String lang,String appTitle, String categoryText,
                              String bookmarkedText, String colorStraw, String searchButtonText,
                              String resultsTitle, String festbier) {
        this.lang = lang;
        this.appTitle = appTitle;
        this.categoryText = categoryText;
        this.bookmarkedText = bookmarkedText;
        this.colorStraw = colorStraw;
        this.searchButtonText = searchButtonText;
        this.resultsTitle = resultsTitle;
        this.festbier = festbier;
    }

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void switchLanguageTestUpButton() {
        switchLanguage();
        onView(withContentDescription(R.string.abc_action_bar_up_description)).perform(click());
        checkTitleAndTabs();
    }

    @Test
    public void switchLanguageTestBackButton() {
        switchLanguage();
        pressBack();
        checkTitleAndTabs();
    }

    @Test
    public void switchLanguageTestFilteredSearch() {
        switchLanguage();
        pressBack();
        clickTabAtPosition(3);  // Filters
        onView(withId(R.id.filterSearch)).perform((click()));

        // Results Title
        onView(withId(R.id.srToolbar)).check(matches(hasDescendant(withText(containsString(resultsTitle)))));

        // Categories
        onView(withId(R.id.searchResults)).check(matches(Matchers.hasListViewEqualTo(festbier, 12)));

    }

    @Test
    public void swithLanguageTestInfo() {
        switchLanguage();
        pressBack();
        onView(withId(R.id.action_info)).perform(click());
        onView(withId(R.id.aboutText)).check(matches(withText(containsString(appTitle))));
    }

    private void switchLanguage() {
        Locale locale = new Locale(lang);
        onView(withId(R.id.action_settings)).perform(click());
        onView(withId(R.id.settings_lang)).perform(click());
        onData(allOf(is(instanceOf(Locale.class)), is(locale))).perform(click());
    }

    private void checkTitleAndTabs() {
        // Title
        onView(withId(R.id.tool_bar)).check(matches(hasDescendant(withText(containsString(appTitle)))));

        // Category list
        onView(withId(R.id.categoryListView)).check(matches(Matchers.hasListViewEqualTo(categoryText, 0)));

        // No need to navigate to favorites
        onView(withId(R.id.bookmarkedListView)).check(matches(hasDescendant(withText(containsString(bookmarkedText)))));

        clickTabAtPosition(2);  // Color chart
        onView(withId(R.id.colorTableTab)).check(matches(hasDescendant(withText(containsString(colorStraw)))));

        clickTabAtPosition(3);  // Filters
        onView(withId(R.id.filterSearch)).check(matches(withText(searchButtonText)));
    }

    private void clickTabAtPosition(int i) {
        onView(allOf(withText("   "),
                Matchers.childAtPosition(
                        Matchers.childAtPosition(
                                withId(R.id.tabs),
                                0),
                        i),
                isDisplayed())
        ).perform(click());
    }

}
