package it.myAndroid.ShadowSMS;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

@SuppressWarnings("unused")
public class ExceptionHandler extends Activity implements UncaughtExceptionHandler  
{
	private UncaughtExceptionHandler defaultUEH; 
	private ShadowSMS myService;
  private String localPath;
  //private String url; 
  private Date date;
  private SharedPreferences preferences;
  private Utils clUtils;
  private Context currContext;
  
  public ExceptionHandler(Context context, String localPath) 
  { 
  	currContext = context;
  	// classe utils
   	clUtils = new Utils(context);
  	preferences = PreferenceManager.getDefaultSharedPreferences(context);
    //this.url = url; 
    this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    if ( localPath != null )
    {
    	this.localPath = localPath;
    }
    else
    {
      // se non è settata la path per il log nei settings uso quella dell'applicativo
   		this.localPath  = preferences.getString("PathLog", clUtils.getAppPath());
   	}
  } 
  
  public ExceptionHandler(Context context) 
  { 
  	// classe utils
  	currContext = context;
   	clUtils = new Utils(context);
  	preferences = PreferenceManager.getDefaultSharedPreferences(context);
    this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
 		this.localPath  = preferences.getString("PathLog", clUtils.getAppPath());
  } 
 
  public void uncaughtException(Thread t, Throwable e) 
  { 
  	date = new Date();
    String timestamp = TimestampFormat.getInstance().format(date);
      
    final Writer result = new StringWriter(); 
    final PrintWriter printWriter = new PrintWriter(result); 
    e.printStackTrace(printWriter); 
    String stacktrace = result.toString(); 
    printWriter.close(); 
    String filename = timestamp + ".stacktrace"; 
    // stoppo il servizio
    stopService(new Intent(getApplicationContext(), ShadowSMSServices.class));
    if (localPath != null) 
    { 
    	writeToFile(stacktrace, filename); 
    } 
    /*
    if (url != null) 
    { 
    	sendToServer(stacktrace, filename); 
    }*/
 
    defaultUEH.uncaughtException(t, e); 
  } 
 
  	private void writeToFile(String stacktrace, String filename) 
  { 
  	try 
  	{ 
  		BufferedWriter bos = new BufferedWriter(new FileWriter( localPath + "/" + filename)); 
      bos.write(stacktrace); 
      bos.flush(); 
      bos.close(); 
    } 
  	catch (Exception e) 
  	{ 
  		e.printStackTrace(); 
    } 
  } 
 
  /*
  private void sendToServer(String stacktrace, String filename) 
  { 
  	DefaultHttpClient httpClient = new DefaultHttpClient(); 
    HttpPost httpPost = new HttpPost(url); 
    List<NameValuePair> nvps = new ArrayList<NameValuePair>(); 
    nvps.add(new BasicNameValuePair("filename", filename)); 
    nvps.add(new BasicNameValuePair("stacktrace", stacktrace)); 
    try 
    { 
    	httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8)); 
      httpClient.execute(httpPost); 
    } 
    catch (IOException e) 
    { 
    	e.printStackTrace(); 
    } 
  }*/
  
} 


