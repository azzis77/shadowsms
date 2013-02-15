package it.myAndroid.ShadowSMS;

import it.myAndroid.ShadowSMS.R;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.app.LauncherActivity;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

@SuppressWarnings("unused")
public class ShadowSMSActivity extends Activity
{
    private View LoginFormLayout;
  	private SharedPreferences preferences;
  	private Utils clUtils;
  	private ExceptionHandler EncryptEx;
  	private static ShadowSMSDBHelper db;
  	private ListView mListView;
  	private String smsMessage;
  	private String smsNumber;
  	private static ArrayList<Sms> smsList;
  	boolean loadingMore = false;
  	private static SMSListAdapter mySmsAdapter;
  	private IntentFilter intentFilter;
  	private BroadcastReceiver intentReceiver = new BroadcastReceiver() 
  	{
  		@Override
  		public void onReceive(Context context, Intent intent) 
  		{
       	new  UpdateListTask(ShadowSMSActivity.this).execute();
  		}
  	};
  	
  	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)  
    {
    	super.onCreate(savedInstanceState);
      try
      {

       	setContentView(R.layout.main);
       	
  			intentFilter=new IntentFilter();
  			intentFilter.addAction("SMS_RECEIVED_ACTION");
  			
      	db = new ShadowSMSDBHelper(this);
      	
      	// classe preference
      	preferences = PreferenceManager.getDefaultSharedPreferences(this);
      	
      	// classe utils
       	clUtils = new Utils(this, getLayoutInflater());

         // se non è settata la path per il log nei settings setto quella dell'applicativo
       	if ( preferences.getString("PathLog", "").equals(""))
       	{
       		int storedPreference = preferences.getInt("PathLog", 0); 
       		// Recupero il path dell'applicativo
       		String strLogPath = clUtils.getAppPath();
       		SharedPreferences.Editor editor = preferences.edit(); 
       		// Salvo la path recuperata nei setting dell'utente
       		editor.putString("PathLog", strLogPath); 
       		editor.commit(); 
       	}
        // classe Exception su file
       	EncryptEx = new ExceptionHandler(this);
       	
       	mListView = (ListView)findViewById(R.id.listView);
       	
       	db.open();
       	smsList = db.getAllSms();
       	db.close();
       	mySmsAdapter = new SMSListAdapter(this, smsList);
       	mListView.setAdapter(mySmsAdapter);
       	mListView.setOnItemLongClickListener(new OnItemLongClickListener()
				{
					@Override
					public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id)
					{
						onLongListItemClick(v, pos, id); 
            return true; 
					}
				});
        
       	mListView.setOnItemClickListener(new OnItemClickListener()
				{

					@Override
					public void onItemClick(AdapterView<?> av, View v, int pos, long id)
					{
						onListItemClick(av, v, pos, id); 
					}
				});
        
        //new AddStringTask().execute();
        
        // avvio password check
       	if (preferences.getBoolean("checkPwdApplication",false))
       	{
       		startActivity(new Intent(getApplicationContext(), ShadowSMSLogin.class));
       	}
      }
      catch ( Exception e)
      {
      	EncryptEx.uncaughtException(new Thread(), e);
      }
    }
    
    protected void onLongListItemClick(View v, int pos2, long id2) 
    { 
			Sms currSms =(Sms) smsList.get(pos2);
			showOptionsMenu(currSms);
    } 
    
    protected void onListItemClick(AdapterView<?> av, View v, int pos2, long id2) 
    {
			Bundle bundle =new Bundle();
			Sms o = (Sms)smsList.get(pos2);
//    Imposto l'SMS come letto
			o.setSmsStatus(Sms.SMS_READ);
			db.open();
			db.updateSms(o);
			db.close();
			Intent i = new Intent(getApplicationContext(), ShadowSMSSmsDetails.class);
			bundle.putString("number", o.getSmsNumber());
			bundle.putString("body",  o.getSmsBody());
      i.putExtras(bundle);
      startActivityForResult(i, 0);
    }
    
		public List<Sms> getItemList() 
    { 
    	db.open();
     	smsList = db.getAllSms();
     	db.close();
     	return smsList;
    }

		public void showOptionsMenu(final Sms currSms)
		{
			new AlertDialog.Builder(this).setTitle("Tel: " + currSms.getSmsNumber()).setCancelable(true).setItems(
					R.array.smsMenu, new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialoginterface, int i) 
				{
				 switch (i)
				 {
					case 0:
					  DeleteSMSAlertDialog("Cancellare?", currSms);
				 }
		    }
		  }
		)
		.show();
		}

		@Override
		protected void onResume() 
		{
			new  UpdateListTask(ShadowSMSActivity.this).execute();
			registerReceiver(intentReceiver,intentFilter);
			if( preferences.getBoolean("showIcons",true) == true )
      {
      	showNotification(this, R.string.app_name);
      }
      else
      {
      	CancelNotification(this, R.string.app_name);
      }
			super.onResume();
		}
		
		
		public PendingIntent existNotification(int id) 
		{
		  Intent intent = new Intent(this, ShadowSMSActivity.class);
		  intent.setAction(Intent.ACTION_VIEW);
		  PendingIntent test = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_NO_CREATE);
		  return test;
		 }
		
		
//  Visualizzo notifica nella barra
		private void showNotification(Context ctx, int notifyID) 
	  {
		  Notification.Builder builder = new Notification.Builder(ctx)  
		       .setSmallIcon(R.drawable.ic_launcher)  
		       .setContentTitle(getText(R.string.app_name))
		       .setContentText(getText(R.string.app_name));
		  
			Intent notificationIntent = new Intent(ctx, ShadowSMSActivity.class);  
			PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(contentIntent);

		  // Add as notification  
	  	NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);  
		  final Notification notification = builder.build();
	    notification.flags = Notification.FLAG_NO_CLEAR;
	    notification.defaults = Notification.DEFAULT_LIGHTS;
		  manager.notify(notifyID, notification);
	  }

//  Cancello notifica nella barra
		public void CancelNotification(Context ctx, int notifyID) 
		{
			NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);  
			manager.cancel(notifyID);  			
	  }
		
		@Override
		protected void onPause() 
		{
		  unregisterReceiver(intentReceiver);
		  super.onPause();
		}
		
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  
    {
        if (keyCode == KeyEvent.KEYCODE_BACK) 
        {
        	//return true;
        	//startService(new Intent(this, EncryptikoServices.class));
        }
        return super.onKeyDown(keyCode, event);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    { 
    	super.onCreateOptionsMenu(menu);
    	MenuInflater inflater = getMenuInflater();    
    	inflater.inflate(R.menu.menu, menu);    
    	return true;
    } 
         
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    { 
      boolean handled = false; 
      switch (item.getItemId())
      { 
        // Verifico il tasto del menu premuto.
        case R.id.mnu_exit:
        	ExitAlertDialog("ShadowSMS verrà chiuso, continuare?");
        	handled = false;
        	break;
        case R.id.mnu_settings:
        	// Avvio screen settings
    		  startActivity(new Intent(getApplicationContext(), Preferences.class));
    		  break;
      }
      return handled;

    } 
    
    public void ExitAlertDialog(String strTextDialog)
    {
    	boolean blRet;
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(strTextDialog);
    	builder.setCancelable(false);
      builder.setPositiveButton("Si", new DialogInterface.OnClickListener() 
      { 
      	public void onClick(DialogInterface dialog, int id) 
        { 
      		stopService(new Intent(getApplicationContext(), ShadowSMSServices.class));
      		ShadowSMSActivity.this.finish();  	
       	}       
      });   
      builder.setNegativeButton("No", new DialogInterface.OnClickListener() 
      {           
      	public void onClick(DialogInterface dialog, int id) 
        {                
      		dialog.cancel();           
       	}       
     	}).show();
    }

    public void DeleteSMSAlertDialog(String strTextDialog, final Sms smsToDelete)
    {
    	boolean blRet;
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(strTextDialog);
    	builder.setCancelable(false);
      builder.setPositiveButton("Si", new DialogInterface.OnClickListener() 
      { 
      	public void onClick(DialogInterface dialog, int id) 
        { 
      		db.open();
      		db.deleteSms(smsToDelete);
      		db.close();
      		new UpdateListTask(ShadowSMSActivity.this).execute();
       	}       
      });   
      builder.setNegativeButton("No", new DialogInterface.OnClickListener() 
      {           
      	public void onClick(DialogInterface dialog, int id) 
        {                
      		dialog.cancel();           
      		new UpdateListTask(ShadowSMSActivity.this).execute();
       	}       
     	}).show();
    }
    
    // ListViewAdapter custom per la gestione dei testi/icone dei messaggi
    public static class SMSListAdapter extends BaseAdapter 
    {
    	private Context context;
    	private Bitmap mIconSmsRead;
      private Bitmap mIconSmsUnread;
      private ArrayList<Sms> listSms;
      
    	public SMSListAdapter(Context context, ArrayList<Sms> listSms) 
    	{
    		this.context = context;
    	  // Icons bound to the rows.
        mIconSmsRead = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_sms_old);
        mIconSmsUnread = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_sms_new);
        this.listSms = listSms;
    	}

    	public int getCount() 
    	{
    		return listSms.size();
    	}

    	public Object getItem(int position) 
    	{
    		return listSms.get(position);
    	}

    	public long getItemId(int position) 
    	{
    		return position;
    	}

    	public View getView(int position, View convertView, ViewGroup parent) 
    	{
    		ViewHolder holder;
    		SpannableString spanString = new SpannableString(listSms.get(position).getSmsNumber());
    		
    		if (convertView == null) 
    		{
    			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    			convertView =inflater.inflate(R.layout.expandablelistview, null);
    			holder = new ViewHolder();
    			holder.text = (TextView) convertView.findViewById(R.id.lstNumber);
    			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
    			convertView.setTag(holder);
    		} 
    		else 
    		{
    			holder = (ViewHolder) convertView.getTag();
    		}

        //Setto lo stile in base al tipo di messaggio READ/UNREAD    			
    		if ( listSms.get(position).getSmsStatus() == Sms.SMS_UNREAD )
    	  {
    	    spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
    	  	holder.text.setText(spanString);
    	  	holder.icon.setImageBitmap(mIconSmsUnread);
    	  }
    	  else if ( listSms.get(position).getSmsStatus() == Sms.SMS_READ )
    	  {
    	    spanString.setSpan(new StyleSpan(Typeface.NORMAL), 0, spanString.length(), 0);
    	  	holder.text.setText(spanString);
    	  	holder.icon.setImageBitmap(mIconSmsRead);
    	  }
    		
    	return convertView;
    	}

    	static class ViewHolder 
    	{
    		TextView text;
    		ImageView icon;
    	}
    }
   
//  Aggiorno la ListView    
    class UpdateListTask extends AsyncTask<Void, String, Void> 
    {
    	private ProgressDialog dialog;
    	private Activity activity;
      private Context context;
    	
    	public UpdateListTask(Activity activity) 
    	{
        this.activity = activity;
        context = activity;
        dialog = new ProgressDialog(context);
      }
    	
    	@Override
      protected void onPreExecute(){
          //this.dialog.setMessage("Updating..");
          //this.dialog.show();
      }
    	
    	@Override
      protected Void doInBackground(Void... unused) 
      {
       	smsList.clear();
      	db.open();
       	smsList = db.getAllSms();
       	db.close();
       	return(null);
      }

      @Override
      protected void onPostExecute(Void unused) 
      {
        if ( dialog.isShowing() )
        {
          dialog.dismiss();
        }
        
        SMSListAdapter adapter = new SMSListAdapter(activity, smsList);
        mListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
      }
    }
}

