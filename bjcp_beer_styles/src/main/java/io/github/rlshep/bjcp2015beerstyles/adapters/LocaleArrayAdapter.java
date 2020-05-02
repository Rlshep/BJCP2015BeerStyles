package io.github.rlshep.bjcp2015beerstyles.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class LocaleArrayAdapter extends ArrayAdapter<Locale> {
    public LocaleArrayAdapter(@NonNull Context context, int resource, @NonNull List<Locale> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView tv = (TextView) super.getView(position, convertView, parent);
        Locale locale = getItem(position);
        tv.setText(locale.getDisplayLanguage(locale));
        return tv;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView tv = (TextView) super.getDropDownView(position, convertView, parent);
        Locale locale = getItem(position);
        tv.setText(locale.getDisplayLanguage(locale));
        return tv;
    }
}