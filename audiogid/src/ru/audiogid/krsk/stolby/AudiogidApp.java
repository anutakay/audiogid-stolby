package ru.audiogid.krsk.stolby;

import android.app.Activity;
import android.app.Application;

public class AudiogidApp extends Application { 
	
	private Activity mCurrentActivity = null;
	
	public void onCreate() {
        super.onCreate();
	}
 
	public Activity getCurrentActivity()	{
		return mCurrentActivity;
	}
	
	public void setCurrentActivity(final Activity mCurrentActivity) {
        this.mCurrentActivity = mCurrentActivity;
	}

}
