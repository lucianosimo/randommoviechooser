package com.lucianosimo.randommoviechooser;

import com.lucianosimo.topmoviepicker.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends Activity{

	private final int SPLASH_DURATION = 2000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Intent intent = new Intent(Splash.this,RandomMovies.class);
				Splash.this.startActivity(intent);
				Splash.this.finish();				
			}
		},SPLASH_DURATION);
	}
}