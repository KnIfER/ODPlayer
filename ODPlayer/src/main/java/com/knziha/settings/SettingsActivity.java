package com.knziha.settings;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.preference.PreferenceFragmentCompat;

import com.knziha.ODPlayer.CrashHandler;
import com.knziha.ODPlayer.Toastable_Activity;

import java.io.File;

public class SettingsActivity extends Toastable_Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Window win = getWindow();
		if(opt.isFullScreen()){
			win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		root=win.getDecorView().findViewById(android.R.id.content);
		checkMargin();
		if(Build.VERSION.SDK_INT>=21) {
			win.setStatusBarColor(Color.BLACK);
			win.setNavigationBarColor(0);
		}

		File log=new File(CrashHandler.getInstance(opt).getLogFile());
		File lock=new File(log.getParentFile(),"lock");
		if(lock.exists()) lock.delete();

		PreferenceFragmentCompat fragment;
		switch (getIntent().getIntExtra("realm", 0)){
			default:
			case 0:
				fragment = new SettingsMainProgram();
			break;
			case 1:
				fragment = new SettingsCourseMode();
			break;
		}

		getSupportFragmentManager().beginTransaction()
				.replace(android.R.id.content, fragment)
				.commit();
	}
}
