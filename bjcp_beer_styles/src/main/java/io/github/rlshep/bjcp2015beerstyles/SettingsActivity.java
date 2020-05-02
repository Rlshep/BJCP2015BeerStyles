package io.github.rlshep.bjcp2015beerstyles;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Locale;

import io.github.rlshep.bjcp2015beerstyles.adapters.LocaleArrayAdapter;
import io.github.rlshep.bjcp2015beerstyles.db.BjcpDataHelper;
import io.github.rlshep.bjcp2015beerstyles.exceptions.ExceptionHandler;
import io.github.rlshep.bjcp2015beerstyles.helpers.PreferencesHelper;

public class SettingsActivity extends BjcpActivity {
    private PreferencesHelper preferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_settings);

        preferencesHelper = new PreferencesHelper(this);
        setupToolbar(R.id.scbToolbar, getString(R.string.title_activity_settings), true, true);

        initializeRadioButtons();
        initializeLangSelection();
        addListenerOnButton();
        addListenerOnLangSelection();
    }

    private void initializeRadioButtons() {
        RadioButton specificGravity = findViewById(R.id.settings_specific_gravity);
        RadioButton plato = findViewById(R.id.settings_plato);
        RadioButton srm = findViewById(R.id.settings_srm);
        RadioButton ebc = findViewById(R.id.settings_ebc);

        specificGravity.setChecked(!preferencesHelper.isPlato());
        plato.setChecked(preferencesHelper.isPlato());
        srm.setChecked(!preferencesHelper.isEBC());
        ebc.setChecked(preferencesHelper.isEBC());
    }

    private void initializeLangSelection() {
        Spinner lang = findViewById(R.id.settings_lang);
        BjcpDataHelper bjcpDataHelper = BjcpDataHelper.getInstance(this);
        ArrayList locales = new ArrayList<Locale>();

        for (String langCode: bjcpDataHelper.getAllLanguages()) {
            locales.add(new Locale(langCode));
        }

        LocaleArrayAdapter adapter = new LocaleArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, locales);

        lang.setAdapter(adapter);
        lang.setSelection(adapter.getPosition(new Locale(preferencesHelper.getLanguage())));
    }

    public void addListenerOnButton() {
        RadioButton specificGravity = findViewById(R.id.settings_specific_gravity);
        RadioButton plato = findViewById(R.id.settings_plato);
        RadioButton srm = findViewById(R.id.settings_srm);
        RadioButton ebc = findViewById(R.id.settings_ebc);

        specificGravity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferencesHelper.setPreferences(PreferencesHelper.UNIT_GRAVITY, PreferencesHelper.GRAVITY_SPECIFIC);
            }
        });

        plato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferencesHelper.setPreferences(PreferencesHelper.UNIT_GRAVITY, PreferencesHelper.GRAVITY_PLATO);
            }
        });

        srm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferencesHelper.setPreferences(PreferencesHelper.UNIT_COLOR, PreferencesHelper.COLOR_SRM);
            }
        });

        ebc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferencesHelper.setPreferences(PreferencesHelper.UNIT_COLOR, PreferencesHelper.COLOR_EBC);
            }
        });
    }

    public void addListenerOnLangSelection() {
        Spinner lang = findViewById(R.id.settings_lang);
        Activity activity = this;

        lang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String selectedLanguage = ((Locale) parent.getItemAtPosition(pos)).getLanguage();
                if (!selectedLanguage.equals(preferencesHelper.getLanguage())) {
                    setAppLanguage(selectedLanguage);
                    recreate();
                    preferencesHelper.setPreferences(preferencesHelper.LANGUAGE, selectedLanguage);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }
}
