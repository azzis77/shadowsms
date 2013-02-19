package it.myAndroid.ShadowSMS;

import it.myAndroid.ShadowSMS.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.preference.PreferenceManager;

@SuppressWarnings("unused")
public class ShadowSMSServices extends Service
{
	private NotificationManager mNotificationManager;
	private final BroadcastReceiver smsReceiver = new SmsReceiver(); 
	private ShadowSMSDBHelper db;
	private SharedPreferences preferences;
	
	@Override
	public IBinder onBind(Intent intent) 
	{
		return null;
	}
	
	@Override
	public void onCreate() 
	{
		try
		{
		//Registro la ricezione dei messaggi
  	IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED"); 
  	filter.setPriority( IntentFilter.SYSTEM_HIGH_PRIORITY );
  	registerReceiver( smsReceiver, filter );
  	Log.d(ShadowSMSServices.class.getName(),"Registrazione ricezione SMS");
		}
		catch(Exception e)
		{
			Log.e(ShadowSMSServices.class.getName(),"Errore",e);
		}
	}

	@Override
	public void onDestroy() 
	{
		try
		{
			// deregistro la ricezione degli SMS 
			unregisterReceiver(smsReceiver);
			Log.d(ShadowSMSServices.class.getName(),"Deregistrazione ricezione SMS");
		}
		catch(Exception e)
		{
			Log.e(ShadowSMSServices.class.getName(),"Errore",e);
		}
	}
	
	@Override
	public void onStart(Intent intent, int startid) 
	{
		try
		{
			intent.setAction(Intent.ACTION_MAIN); 
			intent.addCategory(Intent.CATEGORY_LAUNCHER); 
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			ComponentName cn = new ComponentName(this, ShadowSMSActivity.class); 
			intent.setComponent(cn); 
			Log.d(ShadowSMSServices.class.getName(),"Avvio servizio");
			startActivity(intent); 
		}
		catch(Exception e)
		{
			Log.e(ShadowSMSServices.class.getName(),"Errore",e);
		}
	}
}
