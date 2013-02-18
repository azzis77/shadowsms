package it.myAndroid.ShadowSMS;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

@SuppressWarnings("unused")
public class ShadowSMSDBHelper 
{

  private static final String DB_PATH = "/data/data/it.myAndroid.Encryptiko/databases/";
  private static final String DATABASE_NAME = "encryptiko.db"; 
  public static final  String TABLE_SMS = "SMS"; 
  public static final  String TABLE_BLACKLIST = "BLACKLIST";
  public static final  String KEY_ID = "_id";
	public static final  String KEY_NUMBER = "number";
	public static final  String KEY_BODY = "body";
	public static final  String KEY_STATUS = "status";
  public static final  String KEY_BLID = "_id";
	public static final  String KEY_BLNUMBER = "number";
	public static final  String KEY_BLNAME = "name";

	private static final int DATABASE_VERSION = 1; 
	
  private SQLiteDatabase db; 
  private EncryptikoDB encrypticoDB;
  private final Context	context;
  
  public ShadowSMSDBHelper(Context ctx) 
  {
    this.context = ctx;
    encrypticoDB = new EncryptikoDB(context);
  }

	public ShadowSMSDBHelper open() throws SQLException
	{
		db = encrypticoDB.getWritableDatabase();
	  return this;
	}
	
	public void close() 
	{
		encrypticoDB.close();
	}
	 	
	// Aggiunge un nuovo SMS 
	public long addContact(Sms sms) 
	{ 
		ContentValues values = new ContentValues(); 
	  values.put(KEY_NUMBER, sms.getSmsNumber()); 		// Numero SMS 
	  values.put(KEY_BODY, sms.getSmsBody()); 				// Testo SMS 
	  values.put(KEY_STATUS, sms.getSmsStatus());		// Stato messaggio 0 non letto 1 letto 

	  // Inserisco il record 
	  return db.insert(TABLE_SMS, null, values); 
	} 
	
  // Legge un singolo SMS dal DB 
	public Sms getSms(int id) 
	{ 
	  Cursor cursor = db.query(TABLE_SMS, new String[] { KEY_ID, KEY_NUMBER, KEY_BODY, KEY_STATUS }, KEY_ID + "=?", 
	                               new String[] { String.valueOf(id) }, null, null, null, null); 
	  if (cursor != null)
	    cursor.moveToFirst(); 
	
	    Sms sms = new Sms(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), Integer.parseInt(cursor.getString(3))); 
	       
	    return sms; 
	}

	// Legge un singolo SMS dal db e ritorna il cursor
	public Cursor fetchSmsForNumber(int id)
	{
		 Cursor cursor = db.query(TABLE_SMS, new String[] { KEY_ID, KEY_NUMBER, KEY_BODY, KEY_STATUS }, KEY_ID + "=?", 
	                            new String[] { String.valueOf(id) }, null, null, null, null); 
	 	 return cursor; 
	}
	  
	// Ritorna un cursor con con tutti gli SMS dal db
	public Cursor fetchAllSms()
	{
	  String selectQuery = "SELECT * FROM " + TABLE_SMS + " ORDER BY " + KEY_NUMBER; 
	
	  Cursor cursor = db.rawQuery(selectQuery, null); 
    return cursor;
	}
	  
	// Ritorna una lista con tutti gli SMS dal db
	public ArrayList<Sms> getAllSms() 
	{ 
		ArrayList<Sms> smsList = new ArrayList<Sms>(); 
    String selectQuery = "SELECT * FROM " + TABLE_SMS + " ORDER BY " + KEY_NUMBER; 
	
    Cursor cursor = db.rawQuery(selectQuery, null); 
	
	  // loop per le righe del DB 
	  if (cursor.moveToFirst()) 
	  { 
	    do 
	    { 
	      Sms sms = new Sms(); 
	      sms.setId(Integer.parseInt(cursor.getString(0))); 
	      sms.setSmsNumber(cursor.getString(1)); 
	      sms.setSmsBody(cursor.getString(2)); 
	      sms.setSmsStatus(Integer.parseInt(cursor.getString(3)));
	    
	      // Aggiungo il record  
	      smsList.add(sms); 
      } while (cursor.moveToNext()); 
     } 
      return smsList; 
	  } 
	
	// Aggiorna un singolo SMS 
	public boolean updateSms(Sms sms) 
	{ 
	  
    ContentValues values = new ContentValues(); 
	  values.put(KEY_NUMBER, sms.getSmsNumber()); 
	  values.put(KEY_BODY, sms.getSmsBody()); 
	  values.put(KEY_STATUS, sms.getSmsStatus());
	
	  return db.update(TABLE_SMS, values, KEY_ID + " = ?", new String[] { String.valueOf(sms.getId()) }) > 0; 
	} 
	
	// Cancella un singolo SMS 
	public boolean deleteSms(Sms sms) 
	{ 
	  return db.delete(TABLE_SMS, KEY_ID + " = ?", new String[] { String.valueOf(sms.getId()) }) > 0; 
	}  
	  
	// Cancella tutti gli SMS archiviati
	public boolean deleteAllSms()
	{
	  return db.delete(TABLE_SMS, null, null) > 0; 
	}
	
	// Ritorna il numero di SMS 
	public int getContactsCount() 
	{ 
	  String countQuery = "SELECT  * FROM " + TABLE_SMS; 
	  Cursor cursor = db.rawQuery(countQuery, null); 
	  cursor.close(); 
	
    return cursor.getCount(); 
   }
	  
 	public class EncryptikoDB extends SQLiteOpenHelper
	{
	  
	  public EncryptikoDB(Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) 
		{
			// Creo la tabella contenente SMS
		  String CREATE_SMS_TABLE = "CREATE TABLE " + TABLE_SMS + "("
		         + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NUMBER + " TEXT,"
		         + KEY_BODY + " TEXT," + KEY_STATUS + " INTEGER" + ")"; 
		  // Creo la tabella contenente blacklist
    	String CREATE_BLACKLIST_TABLE = "CREATE TABLE " + TABLE_BLACKLIST + "("
		         + KEY_BLID + " INTEGER PRIMARY KEY," + KEY_BLNUMBER + " TEXT,"
		         + KEY_BLNAME + " TEXT" + ")"; 

	  	db.execSQL(CREATE_SMS_TABLE);
		 	db.execSQL(CREATE_BLACKLIST_TABLE); 
		   	
	 	}
		
	 	@Override
	 	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	 	{
	    // Cancello la vecchia tabella 
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_SMS); 
	
	    // Ricreo la tabella con la nuova versione
	    onCreate(db);
		    
	    // Cancello la vecchia tabella 
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_BLACKLIST); 
  
	    // Ricreo la tabella con la nuova versione
		  onCreate(db);

		}
	}
}
