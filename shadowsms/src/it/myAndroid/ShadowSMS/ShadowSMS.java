package it.myAndroid.ShadowSMS;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ShadowSMS extends Activity
{
	/** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) 
  {
      super.onCreate(savedInstanceState);
      startService(new Intent(this, ShadowSMSServices.class));
      this.finish();
  }
}
