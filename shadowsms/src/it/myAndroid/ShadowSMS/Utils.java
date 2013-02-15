package it.myAndroid.ShadowSMS;

import it.myAndroid.ShadowSMS.R;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("unused")
public class Utils
{
	private Toast toast;
	private TextView text;
	private View Toastlayout;
	private Context context;
	
	public Utils(Context aContext, LayoutInflater layoutInflater)
	{
		this.context = aContext;
		//////////////////////// START Init toast 
		toast = new Toast(this.context);
		// setto il layout da utilizzare
		Toastlayout = layoutInflater.inflate(R.layout.toast_layout, null);
		ImageView image = (ImageView) Toastlayout.findViewById(R.id.image);
		image.setImageResource(R.drawable.ic_warning);
		text = (TextView) Toastlayout.findViewById(R.id.text);
		//////////////////////// STOP Init toast 
	}
	
	public Utils(Context aContext)
	{
	 	this.context = aContext; 
	}
	
	public void ShowMessageToast(String strMessage)
  {
  	toast.cancel();
  	text.setText(strMessage);
  	toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
  	toast.setDuration(Toast.LENGTH_LONG);
  	toast.setView(Toastlayout);
  	toast.show();
  }
	
	public String getAppPath()
	{
    PackageManager m = this.context.getPackageManager(); 
		String strPkgName =  this.context.getPackageName(); 
		PackageInfo p = null;
		try
		{
			p = m.getPackageInfo(strPkgName, 0);
		} catch (NameNotFoundException e)
		{
			e.printStackTrace();
		} 
		return p.applicationInfo.dataDir; 
	}
 
}
