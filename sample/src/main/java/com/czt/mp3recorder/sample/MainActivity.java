
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


public class MainActivity extends Activity {


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

//		File ta=Environment.getExternalStorageDirectory();
//		for (File file:ta.listFiles())
//		{
//			if(file.getName().endsWith(".mp3"))
//			{
//				file.delete();
//			}
//		}



		Button startButton = (Button) findViewById(R.id.StartButton);
		startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MP3Recorder.getInstance().start(new File(Environment.getExternalStorageDirectory(),"/temp"),70,1);

			}
		});
		Button stopButton = (Button) findViewById(R.id.StopButton);
		stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MP3Recorder.getInstance().stop();
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MP3Recorder.getInstance().stop();
	}
}
