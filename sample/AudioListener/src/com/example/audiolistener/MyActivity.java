package com.example.audiolistener;

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
				service.putExtra(MyService.KEY_MESSAGE, "mon message de test et je ne sais pas quand très ... , car il me faut un message super long, que je ne pourrais pas dire deux fois. Tu vois ce n'est pas plus compliqué pour cela il ne faut pas dire plus .merci de ta compréhension !");
				service.putExtra(MyService.KEY_NAME, "montel");
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
