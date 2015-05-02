package ru.audiogid.krsk.stolby;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;

public class PrefActivity extends PreferenceActivity {
	
	CheckBoxPreference chb1;
	
	@SuppressWarnings("deprecation")
	@Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    addPreferencesFromResource(R.xml.pref);

	    chb1 = (CheckBoxPreference) findPreference("chb1");
	  }

}
