package io.github.rlshep.bjcp2015beerstyles;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;

import org.apache.commons.lang.StringUtils;

import java.util.Locale;

public abstract class BjcpActivity extends AppCompatActivity {
    private String currentLanguage;

    protected void setupToolbar(int toolBarId, String title, boolean showIcon, boolean showUp) {
        Toolbar toolbar = findViewById(toolBarId);

        if (!StringUtils.isEmpty(title)) {
            toolbar.setTitle(title);
        }

        setSupportActionBar(toolbar);

        if (showUp) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (showIcon) {
            getSupportActionBar().setIcon(R.drawable.action_icon);
        }
    }

    protected void setAppLanguage(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        setTitle(R.string.app_name);
    }
}
