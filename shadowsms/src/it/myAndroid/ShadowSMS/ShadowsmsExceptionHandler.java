package it.myAndroid.ShadowSMS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.os.Looper;
import android.os.StatFs;
import android.util.Log;

@SuppressWarnings("unused")
public class ShadowsmsExceptionHandler  implements UncaughtExceptionHandler
{
	 private static final String RECIPIENT = "stefano.azzi@gmail.com";
	 private Context context;
	 private static Context context1;
	 
	 public ShadowsmsExceptionHandler (Context ctx )
	 {
		  context = ctx;
	    context1 = ctx;
	 }
	 
	  private StatFs getStatFs() {
	    File path = Environment.getDataDirectory();
	    return new StatFs(path.getPath());
	  }
	  
	  private long getAvailableInternalMemorySize(StatFs stat) {
	    long blockSize = stat.getBlockSize();
	    long availableBlocks = stat.getAvailableBlocks();
	    return availableBlocks * blockSize;
	  }
	  
	  private long getTotalInternalMemorySize(StatFs stat) {
	    long blockSize = stat.getBlockSize();
	    long totalBlocks = stat.getBlockCount();
	    return totalBlocks * blockSize;
	  }
	  
 	  private void addInformation(StringBuilder message) 
	  {
	    message.append("Locale: ").append(Locale.getDefault()).append('\n');
	    try 
	    {
	      PackageManager pm = context.getPackageManager();
	      PackageInfo pi;
	      pi = pm.getPackageInfo(context.getPackageName(), 0);
	      message.append("Version: ").append(pi.versionName).append('\n');
	      message.append("Package: ").append(pi.packageName).append('\n');
	    } 
	    catch (Exception e) 
	    {
	    	Log.e(ShadowsmsExceptionHandler.class.getName(), "Error", e);
	      message.append("Could not get Version information for ").append(context.getPackageName());
	    }
	    message.append("Phone Model: ").append(android.os.Build.MODEL).append('\n');
	    message.append("Android Version: ").append(android.os.Build.VERSION.RELEASE).append('\n');
	    message.append("Board: ").append(android.os.Build.BOARD).append('\n');
	    message.append("Brand: ").append(android.os.Build.BRAND).append('\n');
	    message.append("Device: ").append(android.os.Build.DEVICE).append('\n');
	    message.append("Host: ").append(android.os.Build.HOST).append('\n');
	    message.append("ID: ").append(android.os.Build.ID).append('\n');
	    message.append("Model: ").append(android.os.Build.MODEL).append('\n');
	    message.append("Product: ").append(android.os.Build.PRODUCT).append('\n');
	    message.append("Type: ").append(android.os.Build.TYPE).append('\n');
	    StatFs stat = getStatFs();
	    message.append("Total Internal memory: ").append(getTotalInternalMemorySize(stat)).append('\n');
	    message.append("Available Internal memory: ").append(getAvailableInternalMemorySize(stat)).append('\n');
	  }
	 
	 public void uncaughtException(Thread t, Throwable e)
	 {
		 try
		 {
		 StringBuilder report = new StringBuilder();
	   Date curDate = new Date();
	   report.append("Error Report collected on : ").append(curDate.toString()).append('\n').append('\n');
	   report.append("Informations :").append('\n');
	   addInformation(report);
	   report.append('\n').append('\n');
	   report.append("Stack:\n");
	   final Writer result = new StringWriter();
	   final PrintWriter printWriter = new PrintWriter(result);
	   e.printStackTrace(printWriter);
	   report.append(result.toString());
	   printWriter.close();
	   report.append('\n');
	   report.append("****  End of current Report ***");
	   // Se l'eccezione è scatenata da un task in background 
	   // con getCause recupero l'eccezione.
	   Throwable cause = e.getCause();
	   while (cause != null)
	   {
	  	 cause.printStackTrace( printWriter );
	  	 report.append(result.toString());
	  	 cause = cause.getCause();
	   }
	   printWriter.close();
	   report.append("****  End of current Report ***");
	   //SaveAsFile(Report);
	   SendErrorMail(report);
	   //PreviousHandler.uncaughtException(t, e);
		 }
		 catch (Throwable ignore) 
		 {
			 Log.e(ShadowsmsExceptionHandler.class.getName(),"Error while sending error e-mail", ignore);
		 }
	 }
	 

	 
	 private void SendErrorMail(final StringBuilder ErrorContent )
	 {
		 final android.app.AlertDialog.Builder builder= new android.app.AlertDialog.Builder(context);
		 new Thread()
		 {
			 @Override
			 public void run() 
			 {
				 Looper.prepare();
				 builder.setTitle("Opsss...!");
				 builder.create();
				 builder.setNegativeButton("Cancel", new OnClickListener()
				 {
					
					 @Override
					 public void onClick(DialogInterface dialog, int which)
					 {
						 System.exit(0);
					 }
				 });
        
				 builder.setPositiveButton("Report", new OnClickListener() 
				 {
					 @Override
					 public void onClick(DialogInterface dialog, int which) 
					 {
						 Intent sendIntent = new Intent(Intent.ACTION_SEND);
						 String subject = "ShadowSMS stack log";
						 StringBuilder body = new StringBuilder("");
						 body.append('\n').append('\n');
						 body.append(ErrorContent).append('\n').append('\n');
             //sendIntent.setType("text/plain");
						 sendIntent.setType("message/rfc822");
						 sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { RECIPIENT });
						 sendIntent.putExtra(Intent.EXTRA_TEXT, body.toString());
						 sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
						 sendIntent.setType("message/rfc822");
						 //context.startActivity(Intent.createChooser(sendIntent, "Error Report"));
						 context1.startActivity(sendIntent);
						 System.exit(0);
					 }
         	});
         	builder.setMessage("ShadowSMS si è chiusa in modo anomalo!");
         	builder.show();
         	Looper.loop();
			 }
		 }.start();	
	 }
}
