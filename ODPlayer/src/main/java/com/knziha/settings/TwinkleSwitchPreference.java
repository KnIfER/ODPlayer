package com.knziha.settings;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;

import com.knziha.ODPlayer.AgentApplication;
import com.knziha.ODPlayer.CMN;
import com.knziha.ODPlayer.R;
import com.knziha.ODPlayer.VICMainAppOptions;
import com.knziha.settings.SettingsFragmentBase;

public class TwinkleSwitchPreference extends SwitchPreference {
	public TwinkleSwitchPreference(Context context) {
		this(context, null);
	}

	public TwinkleSwitchPreference(Context context, AttributeSet attrs) {
		this(context, attrs, TypedArrayUtils.getAttr(context,
				androidx.preference.R.attr.switchPreferenceStyle,
				android.R.attr.switchPreferenceStyle));
	}

	public TwinkleSwitchPreference(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public void setDefaultValue(Object defaultValue) {
		super.setDefaultValue(defaultValue);
		if(!isPersistent())
			setChecked((Boolean) defaultValue);
	}
}