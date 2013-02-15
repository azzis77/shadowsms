package it.myAndroid.ShadowSMS;

public class Sms
{
	private Integer _id;
	private String smsNumber;
	private String smsBody;
	private int smsStatus;
	public static int SMS_UNREAD = 0;
	public static int SMS_READ = 1;
	
	public Sms() 
	{
		
	}
	
	public Sms(Integer id, String smsNumber, String smsBody, int smsStatus)
	{
		this._id = id;
		this.smsNumber = smsNumber;
		this.smsBody = smsBody;
		this.smsStatus = smsStatus;
		
	}
	
	public Sms(String smsNumber, String smsBody, int smsStatus)
	{
		this.smsNumber = smsNumber;
		this.smsBody = smsBody;
		this.smsStatus = smsStatus;
	}
	
	public long getId() 
	{
		return _id;
	}

	public void setId(Integer id) 
	{
		this._id = id;
	}

	public String getSmsNumber() 
	{
		return smsNumber;
	}

	public void setSmsNumber(String smsNumber) 
	{
		this.smsNumber = smsNumber;
	}

	public String getSmsBody() 
	{
		return smsBody;
	}

	public void setSmsBody(String smsBody) 
	{
		this.smsBody = smsBody;
	}
	
	public int getSmsStatus() 
	{
		return smsStatus;
	}

	public void setSmsStatus(int smsStatus) 
	{
		this.smsStatus = smsStatus;
	}
	

}
