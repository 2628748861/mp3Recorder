
package com.czt.mp3recorder.sample;

import java.io.File;
import java.io.IOException;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.czt.mp3recorder.MP3Recorder;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;

public class MainActivity extends Activity {

	private MP3Recorder mRecorder = new MP3Recorder(new File(Environment.getExternalStorageDirectory(),"test.mp3"));

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		new RxPermissions(this)
				.request(Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE)
		.subscribe(new Consumer<Boolean>() {
			@Override
			public void accept(Boolean aBoolean) throws Exception {
				Toast.makeText(MainActivity.this, "授权结果:"+aBoolean, Toast.LENGTH_SHORT).show();

			}
		});

		Button startButton = (Button) findViewById(R.id.StartButton);
		startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					mRecorder.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		Button stopButton = (Button) findViewById(R.id.StopButton);
		stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mRecorder.stop();
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mRecorder.stop();
	}
}
