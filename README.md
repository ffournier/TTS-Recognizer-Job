TTSJob
======

# Library

This is an library to use the TextToSpeech and SpeechRecognizer.

You can create an set of Job (Listen , then Talk) in depends of the answer of each Job.

You can active job all time, when you have an Headset, HeadSetBt, or none.

// TODO create documention and link it

#Use It
First to use this library you must create Job what you need in your application


##Job

Create a new class which extended the class Job

Next into this class create a String key

You need to pass in constructor 

  * the key you has created, this key is used to found this job in TTS.
  * the message who is read
  * if we have a recognizer after the TTS
  * (optionnal)  and the number of retry that this job can do it.

Add the negative and positive results if you need them, you can customize this answer by override the method onResult. Issue is pending to add matches result and answer directly in Job

```JAVA
public class JobReadSMS extends Job {
	
	public static final String UTTERANCE_MESSAGE_SMS_TAKEN = "com.android2ee.audiolistener.message_taken";
	
	public JobReadSMS(String message) {
		super(UTTERANCE_MESSAGE_SMS_TAKEN, message,  true);
		ArrayList<String> listPositive = new ArrayList<String>();
		listPositive.add("oui");
		listPositive.add("ouais");
		ArrayList<String> listNegative = new ArrayList<String>();
		listNegative.add("non");
		setResults(listPositive, listNegative);
	}
	
	public JobReadSMS(String message, int retry) {
		super(UTTERANCE_MESSAGE_SMS_TAKEN, message,  true, retry);
		ArrayList<String> listPositive = new ArrayList<String>();
		listPositive.add("oui");
		listPositive.add("ouais");
		ArrayList<String> listNegative = new ArrayList<String>();
		listNegative.add("non");
		setResults(listPositive, listNegative);
	}

}
```

##Service

Next create the Service which implement TTSJobService to treat your jobs.

```JAVA
public class MyService extends TTSJobService
```

Next override abstract method

getMetaData return the new Oject create by the Bundle getting by Service

this Object must contains a unique String key which be used to know the type of this object

```JAVA
@Override
	protected POJOObject getMetaData(Bundle bundle) {
		Log.e("SmsReceiver", "getMetaData");
		if (bundle != null) {
			if ( bundle.containsKey(KEY_MESSAGE)) {
				String message = bundle.getString(KEY_MESSAGE);
				String phoneNumber = bundle.getString(KEY_NAME);
				String name = getContact(phoneNumber);
				Log.e("SmsReceiver", "getMetaData Message " + phoneNumber + "   " + name);
				return new POJOMessage(message, phoneNumber, name);
			}
		}
		return null;
	}
```

Next Add you Jobs
In this method you get the object you has created before, (cool to cast it).

Next create your jobs and return it

```JAVA
	@Override
	protected Jobs addJobs(POJOObject object) {
		Jobs jobs = null;
		// test type
		if (POJOMessage.isSMSType(object)) {
		  // cast it
			POJOMessage message = (POJOMessage) object;
			// create jobs
			jobs = new Jobs();
			// create job
			JobReceiveSMS jobReceiveSMS = new JobReceiveSMS(getString(R.string.info_name, message.getValidateName()), MAX_RETRY);
			JobReadSMS jobReadSMS = new JobReadSMS(message.getMessage() + ". Voulez vous envoyer un message à " +  message.getValidateName() + " ?", MAX_RETRY);
			JobSendSMS jobSendSMS = new JobSendSMS(message.getPhoneNumber(), MAX_RETRY);
			JobSentSMS jobSentSMS = new JobSentSMS();
			// add son job
			jobSendSMS.addSonJob(JobAnswer.NOT_FOUND, jobSentSMS);
			jobReadSMS.addSonJob(JobAnswer.POSITIVE_ANSWER, jobSendSMS);
			jobReceiveSMS.addSonJob(JobAnswer.POSITIVE_ANSWER, jobReadSMS);
			// add next job
			jobs.addJob(jobReceiveSMS);
			JobEndSMS jobEnd = new JobEndSMS();
			// add jobs
			jobs.addJob(jobEnd);
		}
		return jobs;
	}
```

isBluetooth is indicate to the TTSJobService that the Bluetooth is taken ?

```JAVA
	@Override
	protected boolean isBluetooth() {
		return true;
	}
```

isPreferenceLanguage is for the recognizer language if we take the language in preference of device.
*(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE)*

null is default

```JAVA
	@Override
	protected Boolean isPreferenceLanguage() {
		return null;
	}
```

getTimeAfterStop only works in ICS.. that the timer for recognizer.

```JAVA
	@Override
	protected Long getTimeAfterStop() {
		return (long) (15 * 1000);
	}
```

##POJOObject
Then create your own POJOObject to save information for your jobs before start them.

Extends the new class of POJOObject

```JAVA
public class POJOMessage extends POJOObject {
```

Create unique key for this class and pass it to the constructor

```JAVA
public static final String KEY_SMS = "treatSMS";

public POJOMessage(String message, String phoneNumber, String name) {
	super(KEY_SMS);
	this.message = message;
	this.phoneNumber = phoneNumber;
	this.name = name;
}
```
	
##Activity
Finish by construct your activity, by integrate the preference in the library.

For it you just need to place the fragement in your activity.xml, this fragment will treat the preference type and mic ;).

```XML
<fragment
	    android:name="com.android2ee.ttsjob.activity.MyPreferences"
	    android:layout_width="match_parent"
	    android:layout_height="match_parent"
	    android:id="@+id/preferences_fragment"
</fragment>
```

##BootCompletedIntentReceiver
This Broadcast is used to start the service at boot.

YOu just need to extend the broadcast of TTSJobBootCompletedIntentReceiver and return the intent of your own service.

```JAVA
public class MyBootCompletedIntentReceiver extends TTSJobBootCompletedIntentReceiver {

	protected Intent callService(Context context) {
		return new Intent(context, MyService.class);
	}

}
```

# Sample

There is a sample which show how to use it , this sample can read sms received and sent a new sms to the contact.


