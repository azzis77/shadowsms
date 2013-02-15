package it.myAndroid.ShadowSMS;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

@SuppressWarnings("unused")
public class ShadowSMSBLackList extends Activity
{
	 private SharedPreferences preferences;
	 private ExceptionHandler EncryptEx;
	 private String decrypBody;
	 private String Number;
	 @Override
   public void onCreate(Bundle savedInstanceState) 
	 { 
		 super.onCreate(savedInstanceState); 

		 preferences = PreferenceManager.getDefaultSharedPreferences(this); 
		 EncryptEx = new ExceptionHandler(this);
		 
		 // Set View to register.xml 
     setContentView(R.layout.blacklist_layout); 
	 }
}
