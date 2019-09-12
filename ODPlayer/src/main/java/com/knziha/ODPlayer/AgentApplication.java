package com.knziha.ODPlayer;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

import com.knziha.filepicker.model.MediaSliderApplication;

public class AgentApplication extends MediaSliderApplication {
    private List<Activity> activities = new ArrayList<Activity>();

	static {
	//	StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
	//            .detectAll()//监测所有内容
	 //           .penaltyLog()//违规对log日志
	//            .penaltyDeath()//违规Crash
	//            .build());
		CMN.AssetMap.clear();
		CMN.AssetMap.put("/ASSET/e.mp4", 0);
		CMN.AssetMap.put("/ASSET/j.mp4", 1);
		CMN.AssetMap.put("/ASSET/j.txt", null);
		CMN.AssetMap.put("/ASSET/e.txt", null);
	}
	
    public void addActivity(Activity activity) {
        activities.add(activity);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        for (Activity activity : activities) {
            activity.finish();
        }


        System.exit(0);
    }

    public void clearNonsenses() {
    }
}