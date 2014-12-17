package com.android2ee.ttsjob.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.android2ee.ttsjob.R;

public class TTSJobActivity extends PreferenceActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/*Button button = (Button) findViewById(R.id.button);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent service = new Intent(TTSJobActivity.this, MyService.class);
				service.putExtra(MyService.KEY_MESSAGE, "mon message de test");
				service.putExtra(MyService.KEY_NAME, "montel");
				TTSJobActivity.this.startService(service); 
			}
		});
		
		Button stopS = (Button) findViewById(R.id.button_stop);
		stopS.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent service = new Intent(TTSJobActivity.this, MyService.class);
				TTSJobActivity.this.stopService(service); 
			}
		});*/
		
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	

}
