package com.grenades.soleilinfos.ui.util;

import android.content.Context;
import android.support.v7.preference.EditTextPreference;
import android.util.AttributeSet;

public class EditTextPreferenceShowsSummary extends EditTextPreference {

    public EditTextPreferenceShowsSummary(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextPreferenceShowsSummary(Context context) {
        super(context);
    }

    @Override
    public void setText(String text) {
        super.setText(text);
        setSummary(text);
    }
}
