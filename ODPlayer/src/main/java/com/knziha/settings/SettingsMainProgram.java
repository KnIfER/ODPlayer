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

public class SettingsMainProgram extends SettingsFragmentBase {
	private String localeStamp;
	private static HashMap<String, String> nym;
	StringBuilder flag_code= new StringBuilder();

	static{
		nym=new HashMap<>(15);
		nym.put("ar", "ae");
		nym.put("zh", "cn");
		nym.put("ja", "jp");
		nym.put("ca", "");
		nym.put("gl", "");
		nym.put("el", "gr");
		nym.put("ko", "kr");
		nym.put("en", "gb\t\tus");
		nym.put("cs", "cz");
		nym.put("da", "dk");
		nym.put("sv", "se");
		nym.put("sl", "si");
		nym.put("nb", "no");
		nym.put("sr", "rs");
		nym.put("uk", "ua");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init_switch_preference(this, "back_light", null, null);
		init_switch_preference(this, "back_light2", null, null);
		init_switch_preference(this, "gradient_background", VICMainAppOptions.getUseGradientBackground(), null);
		init_switch_preference(this, "locale", null, getNameFlag(localeStamp = VICMainAppOptions.locale));

	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		switch (preference.getKey()){
			case "back_light":
				VICMainAppOptions.BGB = (int) newValue;
			break;
			case "gradient_background":
				VICMainAppOptions.setUseGradientBackground((Boolean) newValue);
			break;
			case "locale":
				if(localeStamp!=null)
					VICMainAppOptions.locale=localeStamp.equals(newValue)?localeStamp:null;
				preference.setSummary(getNameFlag((String) newValue));
			break;
		}
		return true;
	}

	private String getNameFlag(String andoid_country_code) {
		if(andoid_country_code==null || andoid_country_code.length()==0)
			return null;
		String name=andoid_country_code;
		int idx=-1;
		if((idx = name.indexOf("-")) != -1.)
			name=name.substring(0, idx);
		else
			andoid_country_code=andoid_country_code.toUpperCase();
		name=name.toLowerCase();
		if(nym.containsKey(name))
			name=nym.get(name);
		if(flag_code==null)
			flag_code= new StringBuilder();
		flag_code.append(andoid_country_code).append("\t\t\t\t");
		for (int i = 0; i < name.length(); i++) {
			char cI = name.charAt(i);
			if(cI>=0x61 && cI<=0x61+26){
				flag_code.append("\uD83C").append((char) (0xDDE6 + cI - 0x61));
			}else
				flag_code.append(cI);
		}
		return flag_code.toString();
	}

	@Override
	public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		addPreferencesFromResource(R.xml.preferences);

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