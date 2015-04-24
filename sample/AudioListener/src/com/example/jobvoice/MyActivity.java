package com.example.jobvoice;

import com.example.jobvoice.R;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MyActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button button = (Button) findViewById(R.id.button);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent service = new Intent(MyActivity.this, MyService.class);
				service.putExtra(MyService.KEY_NAME, MyService.KEY_SMS);
				service.putExtra(MyService.KEY_MESSAGE, "mon message de test");
				service.putExtra(MyService.KEY_PHONE_NUMBER, "montel");
				MyActivity.this.startService(service); 
			}
		});
		
		Button stopS = (Button) findViewById(R.id.button_stop);
		stopS.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent service = new Intent(MyActivity.this, MyService.class);
				MyActivity.this.stopService(service); 
			}
		});
		
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
