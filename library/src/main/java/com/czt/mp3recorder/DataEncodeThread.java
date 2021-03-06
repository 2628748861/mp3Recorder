package com.czt.mp3recorder;

import android.media.AudioRecord;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.czt.mp3recorder.util.LameUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class DataEncodeThread extends HandlerThread implements AudioRecord.OnRecordPositionUpdateListener {
	private StopHandler mHandler;
	private static final int PROCESS_STOP = 1;
	private byte[] mMp3Buffer;
	private FileOutputStream mFileOutputStream;

	private static class StopHandler extends Handler {
		
		private DataEncodeThread encodeThread;
		
		public StopHandler(Looper looper, DataEncodeThread encodeThread) {
			super(looper);
			this.encodeThread = encodeThread;
		}

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == PROCESS_STOP) {
				//处理缓冲区中的数据
				while (encodeThread.processData() > 0);
				// Cancel any event left in the queue
				removeCallbacksAndMessages(null);
				encodeThread.flushAndRelease();
				getLooper().quit();
			}
		}
	}
	File file;
	File directory;
	private int sizeControl;
	/**
	 * Constructor
	 * @throws FileNotFoundException file not found
	 */
	public DataEncodeThread(File directory,int buffer,int sizeControl) throws FileNotFoundException {
		super("DataEncodeThread");
		this.sizeControl=sizeControl;
		this.directory=directory;
		if(!directory.exists())
        {
            directory.mkdirs();
        }
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMddhhmmss");
        this.file=new File(directory, simpleDateFormat.format(new Date())+".mp3");
		this.mFileOutputStream = new FileOutputStream(file);
//		mMp3Buffer = new byte[(int) (7200 + (buffer * 2 * 1.25))];
		mMp3Buffer = new byte[ buffer];
		Log.e("TAG","buffer:"+mMp3Buffer.length);
	}

	@Override
	public synchronized void start() {
		super.start();
		mHandler = new StopHandler(getLooper(), this);
	}

	private void check() {
		if (mHandler == null) {
			throw new IllegalStateException();
		}
	}

	public void sendStopMessage() {
		check();
		mHandler.sendEmptyMessage(PROCESS_STOP);
	}
	public Handler getHandler() {
		check();
		return mHandler;
	}

	@Override
	public void onMarkerReached(AudioRecord recorder) {
		// Do nothing		
	}

	@Override
	public void onPeriodicNotification(AudioRecord recorder) {
		processData();
	}
	/**
	 * 从缓冲区中读取并处理数据，使用lame编码MP3
	 * @return  从缓冲区中读取的数据的长度
	 * 			缓冲区中没有数据时返回0 
	 */
	private int processData() {

		if(file.length()/1024/1024==sizeControl)
		{
			try {
			Log.e("TAG","创建新文件");
			this.mFileOutputStream.close();
				SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMddhhmmss");
			file=new File(directory, simpleDateFormat.format(new Date())+".mp3");
			this.mFileOutputStream = new FileOutputStream(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (mTasks.size() > 0) {
			Task task = mTasks.remove(0);
			short[] buffer = task.getData();
			int readSize = task.getReadSize();
			int encodedSize = LameUtil.encode(buffer, buffer, readSize, mMp3Buffer);
			if (encodedSize > 0){

				try {
					mFileOutputStream.write(mMp3Buffer, 0, encodedSize);
				} catch (IOException e) {
                    e.printStackTrace();
				}
			}
			return readSize;
		}
		return 0;
	}
	
	/**
	 * Flush all data left in lame buffer to file
	 */
	private void flushAndRelease() {
		//将MP3结尾信息写入buffer中
		final int flushResult = LameUtil.flush(mMp3Buffer);
		if (flushResult > 0) {
			try {
				mFileOutputStream.write(mMp3Buffer, 0, flushResult);
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				if (mFileOutputStream != null) {
					try {
						mFileOutputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				LameUtil.close();
			}
		}
	}
	private List<Task> mTasks = Collections.synchronizedList(new ArrayList<Task>());
	public void addTask(short[] rawData, int readSize){
		mTasks.add(new Task(rawData, readSize));
	}
	private class Task{
		private short[] rawData;
		private int readSize;
		public Task(short[] rawData, int readSize){
			this.rawData = rawData.clone();
			this.readSize = readSize;
		}
		public short[] getData(){
			return rawData;
		}
		public int getReadSize(){
			return readSize;
		}
	}
}
