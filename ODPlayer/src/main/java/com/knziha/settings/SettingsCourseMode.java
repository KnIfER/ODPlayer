package com.knziha.settings;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.core.app.ActivityCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.jaredrummler.colorpicker.ColorPickerPreference;
import com.knziha.ODPlayer.AgentApplication;
import com.knziha.ODPlayer.CMN;
import com.knziha.ODPlayer.R;
import com.knziha.ODPlayer.VICMainAppOptions;

import java.util.HashMap;

public class SettingsCourseMode extends SettingsFragmentBase {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}


	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		switch (preference.getKey()){
			case "back_light":
				VICMainAppOptions.BGB = (int) newValue;
			break;
		}
		return true;
	}

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		addPreferencesFromResource(R.xml.textpreferences);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		AgentApplication agent = ((AgentApplication)getActivity().getApplication());
		//findPreference("browse_instant_Srch").setEnabled(agent .opt.isBrowser_AffectEtSearch());
		agent.clearNonsenses();

	}




	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		//v.setBackgroundColor(Color.WHITE);
		//if(Build.VERSION.SDK_INT >= 22) {


		return v;
	}
}