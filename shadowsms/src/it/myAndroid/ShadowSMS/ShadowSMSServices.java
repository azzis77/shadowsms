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
import android.preference.PreferenceManager;

public class ShadowSMSServices extends Service
{
	private NotificationManager mNotificationManager;
	private final BroadcastReceiver smsReceiver = new SmsReceiver(); 
	private ShadowSMSDBHelper db;
	private SharedPreferences preferences;
	private static final int FM_NOTIFICATION_ID = 10000;
	
	@Override
	public IBinder onBind(Intent intent) 
	{
		return null;
	}
	
	@Override
	public void onCreate() 
	{
		// Registro la ricezione dei messaggi
  	IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED"); 
  	filter.setPriority( IntentFilter.SYSTEM_HIGH_PRIORITY );
  	registerReceiver( smsReceiver, filter );
	  // Registro notifica dalla barra
  	mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    // classe preference
  	preferences = PreferenceManager.getDefaultSharedPreferences(this);
	}

	@Override
	public void onDestroy() 
	{
		// deregistro la ricezione degli SMS 
		unregisterReceiver(smsReceiver);
	  // tolgo notifica dalla barra
		mNotificationManager.cancel(R.string.app_name);
	}
	
	@Override
	public void onStart(Intent intent, int startid) 
	{
		intent.setAction(Intent.ACTION_MAIN); 
    intent.addCategory(Intent.CATEGORY_LAUNCHER); 
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    ComponentName cn = new ComponentName(this, ShadowSMSActivity.class); 
    intent.setComponent(cn); 
    startActivity(intent); 

		// Visualizzo la notifica nella bar
    /*
    if( preferences.getBoolean("showIcons",true) == true )
    {
    	showNotification();
    }
    */
	}
	
	private void showNotification() 
  {
		Notification.Builder builder = new Notification.Builder(this)  
        .setSmallIcon(R.drawable.ic_launcher)  
        .setContentTitle(getText(R.string.app_name))
        .setContentText(getText(R.string.app_name));
        
		Intent notificationIntent = new Intent(this, ShadowSMSActivity.class);  
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,PendingIntent.FLAG_CANCEL_CURRENT);  
    builder.setContentIntent(contentIntent);  

    // Add as notification  
    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);  
    manager.notify(R.string.app_name, builder.build());  
		
    /*		 
     CharSequence text = getText(R.string.app_name);
  	 @SuppressWarnings("deprecation")
 		 
  	 Notification notification = new Notification(R.drawable.ic_launcher, text, System.currentTimeMillis());
  	 PendingIntent contentIntent = PendingIntent.getActivity(this, 0,new Intent(this, ShadowSMSActivity.class), 0);
     notification.setLatestEventInfo(this, getText(R.string.app_name),text, contentIntent);
     // setto la notifica come non cancellabile
     notification.flags = notification.FLAG_NO_CLEAR;
     mNotificationManager.notify(R.string.app_name, notification);
    */ 
 	}

}
