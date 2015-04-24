package com.example.audiogid;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class SavedFragmentActivity extends FragmentActivity {
	
	protected AudiogidApp application;

    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (AudiogidApp)this.getApplicationContext();
    }
    
    protected void onResume() {
        super.onResume();
        application.setCurrentActivity(this);
    }
    
    protected void onPause() {
        clearReferences();
        super.onPause();
    }
    
    protected void onDestroy() {        
        clearReferences();
        super.onDestroy();
    }

    private void clearReferences() {
        Activity currActivity = application.getCurrentActivity();
        if (currActivity != null && currActivity.equals(this))
            application.setCurrentActivity(null);
    }

}
