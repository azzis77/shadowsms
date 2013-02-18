package it.myAndroid.ShadowSMS;

import it.myAndroid.ShadowSMS.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

@SuppressWarnings("unused")
public class ShadowSMSSmsDetails extends Activity
{
	 private SharedPreferences preferences;
	 private String decrypBody;
	 private String Number;
	 @Override
   public void onCreate(Bundle savedInstanceState) 
	 { 
		 super.onCreate(savedInstanceState); 
  	 Thread.setDefaultUncaughtExceptionHandler(new ShadowsmsExceptionHandler(ShadowSMSSmsDetails.this));

		 preferences = PreferenceManager.getDefaultSharedPreferences(this); 
		 
		 // Set View to register.xml 
     setContentView(R.layout.smsdetails_layout); 
     
     Intent in = this.getIntent();
     Number = in.getStringExtra("number");
     String encryptBody = in.getStringExtra("body");
     try
		{
			decrypBody = StringCryptor.decrypt( new String(preferences.getString("PWDSMS","encryptiko")), encryptBody);
		} catch (Exception e)
		{
			decrypBody = encryptBody;
		}
     
    TextView smsNumber = (TextView)findViewById(R.id.smsNumber);
    smsNumber.setText(Number, TextView.BufferType.NORMAL);
    		 
    TextView smsBody = (TextView)findViewById(R.id.smsBody);
    smsBody.setText(decrypBody, TextView.BufferType.NORMAL);

	 }
	 
}
