package it.myAndroid.ShadowSMS;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

@SuppressWarnings("unused")
public class ShadowSMS extends Activity
{
	/** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) 
  {
      super.onCreate(savedInstanceState);
  		Thread.setDefaultUncaughtExceptionHandler(new ShadowsmsExceptionHandler(ShadowSMS.this));
      startService(new Intent(this, ShadowSMSServices.class));
      this.finish();
  }
}
