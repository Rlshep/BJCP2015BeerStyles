package io.github.rlshep.bjcp2015beerstyles;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import io.github.rlshep.bjcp2015beerstyles.adapters.ViewPagerAdapter;
import io.github.rlshep.bjcp2015beerstyles.constants.BjcpConstants;
import io.github.rlshep.bjcp2015beerstyles.controllers.BjcpController;
import io.github.rlshep.bjcp2015beerstyles.db.BjcpDataHelper;
import io.github.rlshep.bjcp2015beerstyles.exceptions.ExceptionHandler;
import io.github.rlshep.bjcp2015beerstyles.helpers.PreferencesHelper;
import io.github.rlshep.bjcp2015beerstyles.helpers.SearchHelper;
import io.github.rlshep.bjcp2015beerstyles.tabs.SlidingTabLayout;
import io.github.rlshep.bjcp2015beerstyles.view.ArrayAdapterSearchView;


public class MainActivity extends BjcpActivity implements SearchView.OnQueryTextListener {
    private static final int BOOKMARKED_TAB = 1;
    private static final int COLOR_TAB = 2;
    private static final int FILTER_TAB = 3;

    public String[] searchSuggestions;
    private ArrayAdapter<String> searchSuggestionAdapter;
    private Menu menu;
    private String currentLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_main);
        validateCorrectDatabaseVersion();
        searchSuggestions = new SearchHelper().getSearchSuggestions(this);
        PreferencesHelper preferencesHelper = new PreferencesHelper(this);

        setupToolbar(R.id.tool_bar, "", true, false);
        preferencesHelper.setupPreferences();

        currentLanguage = preferencesHelper.getLanguage();
        setAppLanguage(currentLanguage);

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), getTabTitles());
        final ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        SlidingTabLayout tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.pressed_color);
            }
        });

        // Added trigger On Tap reload when switching between tabs.
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (BOOKMARKED_TAB == position) {
                    BookmarkedTab fragment = (BookmarkedTab) adapter.instantiateItem(pager, position);
                    if (fragment != null) {
                        fragment.onResume();
                    }
                } else if (COLOR_TAB == position) {
                    ColorTableTab fragment = (ColorTableTab) adapter.instantiateItem(pager, position);
                    if (fragment != null) {
                        fragment.onResume();
                    }
                } else if (FILTER_TAB == position) {
                    FilterTab fragment = (FilterTab) adapter.instantiateItem(pager, position);
                    if (fragment != null) {
                        fragment.onResume();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferencesHelper preferencesHelper = new PreferencesHelper(this);
        String appLanguage = preferencesHelper.getLanguage();
        if (!appLanguage.equals(currentLanguage)) {
            currentLanguage = appLanguage;
            setAppLanguage(appLanguage);
            recreate();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu items for use in the action bar
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final ArrayAdapterSearchView searchView = (ArrayAdapterSearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        searchView.setBackgroundColor(getResources().getColor(R.color.pressed_color));

        // Set adapter to get search suggestions.
        searchSuggestionAdapter = new ArrayAdapter<String>(this, R.layout.find_view, searchSuggestions);
        searchView.setAdapter(searchSuggestionAdapter);

        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchView.setText(searchSuggestionAdapter.getItem(position).toString());
                onQueryTextSubmit(searchSuggestionAdapter.getItem(position).toString());
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_search:
                return super.onOptionsItemSelected(item);
            case R.id.action_info:
                BjcpController.startAboutActivity(this);
                return true;
            case R.id.action_settings:
                BjcpController.startSettingsActivity(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String keyword) {
        if (BjcpConstants.MAX_SEARCH_CHARS <= keyword.length()) {
            BjcpController.startSearchResultsActivity(this, keyword);
        } else {
            Toast.makeText(this.getApplicationContext(), R.string.not_enough_chars, Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        // Associate searchable configuration with the SearchView
        if (null != menu) {
            MenuItem searchItem = menu.findItem(R.id.action_search);
            MenuItemCompat.collapseActionView(searchItem);
        }
    }

    private void validateCorrectDatabaseVersion() {
        if (!BjcpDataHelper.getInstance(this).isCorrectDatabaseVersion()) {
            BjcpController.startCrashActivity(this, "Invalid database version.");
        }
    }

    protected CharSequence[] getTabTitles() {
        CharSequence titles[] = new SpannableStringBuilder[4];

        titles[0] = createSpannableStringBuilder(this.getApplicationContext().getResources().getDrawable(R.drawable.outline_list_white_48dp));
        titles[1] = createSpannableStringBuilder(this.getApplicationContext().getResources().getDrawable(R.drawable.outline_star_white_48dp));
        titles[2] = createSpannableStringBuilder(this.getApplicationContext().getResources().getDrawable(R.drawable.outline_palette_white_48dp));
        titles[3] = createSpannableStringBuilder(this.getApplicationContext().getResources().getDrawable(R.drawable.outline_filter_list_white_48dp));

        return titles;
    }

    private SpannableStringBuilder createSpannableStringBuilder(Drawable drawable) {
        SpannableStringBuilder sb = new SpannableStringBuilder("   ");

        try {
            drawable.setBounds(5, 5, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            ImageSpan span = new ImageSpan(drawable, DynamicDrawableSpan.ALIGN_BASELINE);
            sb.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } catch (Exception e) {
            Log.e("MAIN-TITLE", e.getMessage());
        }

        return sb;
    }
}
