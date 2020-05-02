package io.github.rlshep.bjcp2015beerstyles.helpers;

import android.content.Context;
import android.os.Build;
import android.os.LocaleList;
import android.support.annotation.RequiresApi;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Locale;

import static io.github.rlshep.bjcp2015beerstyles.constants.BjcpConstants.DEFAULT_COUNTRY;
import static io.github.rlshep.bjcp2015beerstyles.constants.BjcpConstants.DEFAULT_LANGUAGE;
import static io.github.rlshep.bjcp2015beerstyles.constants.BjcpConstants.allowedLanguages;

public class LocaleHelper {
    private Context c;

    public LocaleHelper(final Context context) {
        this.c = context;
    }

    public String getSystemLanguage() {
        String language = DEFAULT_LANGUAGE;

        try {
            if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                LocaleList locales = c.getResources().getConfiguration().getLocales();
                language = getLanguageFromMultipleLocales(locales);
            } else if (allowedLanguages.contains(Locale.getDefault().getLanguage())) {
                language = Locale.getDefault().getLanguage();
            }
        } catch (Exception e) {
            //Default ok.
        }

        return language;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String getLanguageFromMultipleLocales(final LocaleList locales) {
        String language = DEFAULT_LANGUAGE;
        boolean found = false;
        int i = 0;

        while (!found && i < locales.size()) {
            String localeLanguage = locales.get(i).getLanguage();

            if (allowedLanguages.contains(localeLanguage)) {
                language = localeLanguage;
                found = true;
            }

            i++;
        }

        return language;
    }

    public String getCountry() {
        String country = DEFAULT_COUNTRY;

        try {
            if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                Locale primaryLocale = c.getResources().getConfiguration().getLocales().get(0);
                country = primaryLocale.getCountry();
            } else {
                country = Locale.getDefault().getCountry();
            }
        } catch (Exception e) {
            //Default ok.
        }

        return country;
    }


    protected static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }
}
