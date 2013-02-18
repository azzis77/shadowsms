package it.myAndroid.ShadowSMS;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.app.Activity;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.widget.ListView;
import android.widget.TextView;

@SuppressWarnings("unused")
public class SmsReceiver extends BroadcastReceiver
{

	public static final String SMS_EXTRA_NAME = "pdus";
	public static final String SMS_URI = "content://sms";
	
	public static final String ADDRESS = "address";
  public static final String PERSON = "person";
  public static final String DATE = "date";
  public static final String READ = "read";
  public static final String STATUS = "status";
  public static final String TYPE = "type";
  public static final String BODY = "body";
  public static final String SEEN = "seen";
    
  public static final int MESSAGE_TYPE_INBOX = 1;
  public static final int MESSAGE_TYPE_SENT = 2;
    
  public static final int MESSAGE_IS_NOT_READ = 0;
  public static final int MESSAGE_IS_READ = 1;
    
  public static final int MESSAGE_IS_NOT_SEEN = 0;
  public static final int MESSAGE_IS_SEEN = 1;
  private SharedPreferences preferences;
  private ShadowSMSDBHelper db;
  private List<Sms> smsList;
	
	
  public void onReceive( Context context, Intent intent ) 
	{
  	try
  	{
  		Bundle extras = intent.getExtras();
		  preferences = PreferenceManager.getDefaultSharedPreferences(context); 
		  db = new ShadowSMSDBHelper(context);
		  String typeSMS = preferences.getString("listTypeSMS","-1");
	    
		  if ( extras != null && !typeSMS.equals("normale") )
	    {
	    	// Recupero l'array degli SMS ricevuti
	      Object[] smsExtra = (Object[]) extras.get( SMS_EXTRA_NAME );
	           
	      // Get ContentResolver object for pushing encrypted SMS to incoming folder
	      ContentResolver contentResolver = context.getContentResolver();
	            
	      for ( int i = 0; i < smsExtra.length; ++i )
	      {
	      	SmsMessage sms = SmsMessage.createFromPdu((byte[])smsExtra[i]);
	            	
      	 
	      	if (preferences.getString("listTypeSMS","-1").equals("dummy"))
	  	    {
		      	
	  	    	// Crypt e salvataggio del messaggio
		      	putSmsToCustomDatabase( sms );

		      	
		      	// Blocco l'arrivo del SMS lasciando solo quello cryptato  
		  	    this.abortBroadcast();
		  	    
		  	    //Invio l'SMS fasullo
		  	    SmsSend.sendSms(context, preferences.getString("NRMMITT", "40916"), preferences.getString("SMSMITT", "Nuove offerte per te da TIM, chiama il servizio clienti."));
		  	    
		        ShadowSMSActivity myClass = (ShadowSMSActivity)context;
  	    
	  	    }
	      	else if(preferences.getString("listTypeSMS","-1").equals("silente"))
	  	    {
	  	    	// Crypt e salvataggio del messaggio
		      	putSmsToCustomDatabase( sms );

		      	
		      	// Blocco l'arrivo del SMS lasciando solo quello cryptato  
		  	    this.abortBroadcast();
	  	    }
	      }
	      // Invio l'evento per la ricezione di un nuovo SMS
	      Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("SMS_RECEIVED_ACTION");
				context.sendBroadcast(broadcastIntent);
	    }
  	}
  	catch (Exception e)
  	{
  	}
	}
	
	private void putSmsToDatabase( ContentResolver contentResolver, SmsMessage sms )
	{
		ContentValues values = new ContentValues();
    values.put( ADDRESS, sms.getOriginatingAddress() );
    values.put( DATE, sms.getTimestampMillis() );
    values.put( READ, MESSAGE_IS_NOT_READ );
    values.put( STATUS, sms.getStatus() );
    values.put( TYPE, MESSAGE_TYPE_INBOX );
    values.put( SEEN, MESSAGE_IS_NOT_SEEN );
    try
    {
    	// Crypt del BODY del messaggio
      String encryptedPassword = StringCryptor.encrypt( new String(preferences.getString("PWDSMS","encryptiko")), sms.getMessageBody().toString() ); 
      values.put( BODY, encryptedPassword );
    }
    catch ( Exception e ) 
    { 
    } 
    // Salvo il messaggio nella lista
    contentResolver.insert( Uri.parse( SMS_URI ), values );
	}
	
	private void putSmsToCustomDatabase( SmsMessage objSms )
	{
	   try
	    {
	  	 // Crypt del BODY del messaggio
       String encryptedBody = StringCryptor.encrypt( new String(preferences.getString("PWDSMS","encryptiko")), objSms.getMessageBody()); 

       // Salvo il messaggio nel Dbase
       db.open();
       db.addContact(new Sms (objSms.getOriginatingAddress(), encryptedBody, Sms.SMS_UNREAD ));
       db.close();
	    }
	    catch ( Exception e ) 
	    { 
	    }
	}

}
