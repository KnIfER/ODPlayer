package com.knziha.settings;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.knziha.ODPlayer.CMN;
import com.knziha.ODPlayer.R;

public class SettingsFragmentBase extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		return false;
	}

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

	}

	public static void init_switch_preference(SettingsFragmentBase preference, String key, Object val, String dynamicSummary) {
		Preference perfer = preference.findPreference(key);
		if(perfer != null){
			if(val!=null) perfer.setDefaultValue(val);
			if(dynamicSummary!=null)  perfer.setSummary(dynamicSummary);
			perfer.setOnPreferenceChangeListener(preference);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		//CMN.recurseLogCascade(v);
		if(v != null) {
			View lv = v.findViewById(R.id.recycler_view);
			//lv.setPadding(10, 0, 0, 0);
			lv.setBackgroundColor(Color.WHITE);
		}
		return v;
	}
}