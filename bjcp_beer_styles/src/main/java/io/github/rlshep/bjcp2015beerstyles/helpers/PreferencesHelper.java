package io.github.rlshep.bjcp2015beerstyles.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.apache.commons.lang.StringUtils;

import io.github.rlshep.bjcp2015beerstyles.converters.MetricConverter;

public class PreferencesHelper {
    public static final String PREFERENCE_FILE_KEY = "bjcp_preferences";
    public static final String UNIT = "unit";
    private static final String METRIC = "metric";
    private static final String IMPERIAL = "imperial";

    public static final String UNIT_GRAVITY = "gravity";
    public static final String UNIT_COLOR = "color";
    public static final String GRAVITY_PLATO = "plato";
    public static final String GRAVITY_SPECIFIC = "specific";
    public static final String COLOR_SRM = "srm";
    public static final String COLOR_EBC = "ebc";
    public static final String LANGUAGE = "language";

    private SharedPreferences sharedPref;
    private Activity activity;
    private LocaleHelper lh;

    public PreferencesHelper(Activity a) {
        this.activity = a;
        this.sharedPref = a.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        this.lh = new LocaleHelper(this.activity);
    }

    public void setupPreferences() {
        String unitsPref = sharedPref.getString(UNIT, null); // old generic preference

        String gravityPref = sharedPref.getString(UNIT_GRAVITY, null);
        String language = sharedPref.getString(LANGUAGE, null);

        if (StringUtils.isEmpty(unitsPref) && StringUtils.isEmpty(gravityPref)) {       //First time in
            if (MetricConverter.isCountryMetric(lh.getCountry())) {
                setPreferences(UNIT_GRAVITY, GRAVITY_PLATO);
                setPreferences(UNIT_COLOR, COLOR_EBC);
            } else {
                setPreferences(UNIT_GRAVITY, GRAVITY_SPECIFIC);
                setPreferences(UNIT_COLOR, COLOR_SRM);
            }
        } else if (!StringUtils.isEmpty(unitsPref) && METRIC.equals(unitsPref)) {       //To convert old generic preferences
            setPreferences(UNIT_GRAVITY, GRAVITY_PLATO);
            setPreferences(UNIT_COLOR, COLOR_EBC);
            setPreferences(UNIT, null);     //invalidate
        } else if (!StringUtils.isEmpty(unitsPref) && IMPERIAL.equals(unitsPref)) {     //To convert old generic preferences
            setPreferences(UNIT_GRAVITY, GRAVITY_SPECIFIC);
            setPreferences(UNIT_COLOR, COLOR_SRM);
            setPreferences(UNIT, null);     //invalidate
        }

        if (StringUtils.isEmpty(language)) {       //First time in
            setPreferences(LANGUAGE, lh.getSystemLanguage());
        }
    }

    public void setPreferences(String key, String value) {
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(key, value);
        editor.commit();
    }

    public boolean isEBC() {
        return isPreferenceEqual(UNIT_COLOR, COLOR_EBC);
    }

    public boolean isPlato() {
        return isPreferenceEqual(UNIT_GRAVITY, GRAVITY_PLATO);
    }

    public boolean isPreferenceEqual(String key, String value) {
        boolean equal = false;
        String pref = sharedPref.getString(key, null);

        if (!StringUtils.isEmpty(pref) && pref.equals(value)) {
            equal = true;
        }

        return equal;
    }

    public String getLanguage() {
        return sharedPref.getString(LANGUAGE, null);
    }
}
