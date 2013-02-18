package it.myAndroid.ShadowSMS;

import it.myAndroid.ShadowSMS.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

@SuppressWarnings("unused")
public class ShadowSMSLogin extends Activity
{
	 private EditText txtPassword;
	 private Button btnLogin;
	 private SharedPreferences preferences;
	 private Utils clUtils;
	 
	 @Override
   public void onCreate(Bundle savedInstanceState) 
	 { 
		 super.onCreate(savedInstanceState); 
		 Thread.setDefaultUncaughtExceptionHandler(new ShadowsmsExceptionHandler(ShadowSMSLogin.this));
		 //Set View to register.xml 
     setContentView(R.layout.login_activity); 
     
     clUtils = new Utils(this, getLayoutInflater());
     preferences = PreferenceManager.getDefaultSharedPreferences(this);
     txtPassword = (EditText) this.findViewById(R.id.txtPassword);
     btnLogin = (Button) this.findViewById(R.id.Login);
 
     btnLogin.setOnClickListener(new OnClickListener()
     {
				@Override
				public void onClick(View v)
				{
					if ( txtPassword.getText().toString().equals(preferences.getString("PWDAPP","encryptiko")))
					{
						finish();
					}
					else
					{
						clUtils.ShowMessageToast("Password errata!!");
					}
				}
			});
   }
	 
   @Override
   public boolean onKeyDown(int keyCode, KeyEvent event)  
   {
       if (keyCode == KeyEvent.KEYCODE_BACK) 
       {
       	//blocco il tasto back
       	return true;
       }
       return super.onKeyDown(keyCode, event);
   }

}
