package it.myAndroid.ShadowSMS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class ShadowSMSBoot extends BroadcastReceiver 
{
	private SharedPreferences preferences;
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
			Thread.setDefaultUncaughtExceptionHandler(new ShadowsmsExceptionHandler(context));
			preferences = PreferenceManager.getDefaultSharedPreferences(context); 
			
			if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction()) && 
					 preferences.getBoolean("checkboxLanuch",false) == true ) 
		 {
			 Intent serviceIntent = new Intent("ShadowSMS");
			 context.startService(serviceIntent);
		 }
	}
}
