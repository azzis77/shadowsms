package it.myAndroid.ShadowSMS;

import it.myAndroid.ShadowSMS.R;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.content.ContextWrapper;

public class Preferences extends PreferenceActivity  
{ 
   	private Preference mBlackListPref = null;

  	@SuppressWarnings("deprecation")
		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@Override 
    protected void onCreate(final Bundle savedInstanceState) 
    { 
  		super.onCreate(savedInstanceState); 
      getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferencesFragment()).commit();
      PreferenceManager.setDefaultValues(Preferences.this, R.xml.preference, false);
    } 
		
		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		public class PreferencesFragment extends PreferenceFragment
		{

			@Override
      public void onCreate(final Bundle savedInstanceState) 
			{

				super.onCreate(savedInstanceState);
          // Carico file xml delle preferenze
          addPreferencesFromResource(R.xml.preference);
          Preference customPref = (Preference) findPreference("blacklist"); 
          customPref.setOnPreferenceClickListener(new OnPreferenceClickListener()
					{
						
						@Override
						public boolean onPreferenceClick(Preference preference)
						{
				      
				      return true;
						}
					});
      }

		}
}

