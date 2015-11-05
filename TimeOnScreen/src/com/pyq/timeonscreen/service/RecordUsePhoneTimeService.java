package com.pyq.timeonscreen.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.pyq.timeonscreen.Constants;

public class RecordUsePhoneTimeService extends Service {

	private static final String END_RECORDTIME_THREAD_ACTION = "com.example.viewpagerdemo.service.endrecordtimereceiver";
	private static final String START_RECORDTIME_THREAD_ACTION = "com.example.viewpagerdemo.service.startrecordtimereceiver";
	private long saveTimeInterval = 5; // 保存时间到本地的间隔，单位秒（s）
	private Editor mSpEdit;
	private SharedPreferences mSp;
	private boolean screenOn;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("testthread", "service thread id is "
				+ Thread.currentThread().getId());

		screenOn = true;
		mSp = getSharedPreferences("config", MODE_PRIVATE);

		mSpEdit = mSp.edit();

		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_TIME_TICK);
		filter.addAction(END_RECORDTIME_THREAD_ACTION);
		filter.addAction(START_RECORDTIME_THREAD_ACTION);

		registerReceiver(new ScreenStateReceiver(), filter);

		recordTime();
	}

	private void recordTime() {
		new Thread(new Runnable() {

			public void run() {
				Log.i("testtime", " start record....");

				Log.i("testthread", "********************thread id is "
						+ Thread.currentThread().getId());

				long usePhoneTime = mSp.getLong("usephone_time_today", 0); // 使用手机的时间，单位秒（s）

				while (screenOn) {

					SystemClock.sleep(1000);
					usePhoneTime += 1;
					Log.i("testtime", "time is " + usePhoneTime);

					Intent intent = new Intent(
							Constants.RECORD_TIME_RECEIVER_ACTION);
					intent.putExtra("time", usePhoneTime);
					sendBroadcast(intent);

					if (usePhoneTime % saveTimeInterval == 0) {// save time
						mSpEdit.putLong("usephone_time_today", usePhoneTime);
						mSpEdit.apply();
					}
				}
				Log.i("testtime", " end record....");
			}
		}).start();
	}

	class ScreenStateReceiver extends BroadcastReceiver {

		private static final String HISTORY_START = "history";

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (Intent.ACTION_SCREEN_ON.equals(action)
					|| START_RECORDTIME_THREAD_ACTION.equals(action)) { // Screen
																		// on

				if (Intent.ACTION_SCREEN_ON.equals(action)) {
					saveDayTime();
				}

				screenOn = true;
				recordTime();
			} else if (Intent.ACTION_SCREEN_OFF.equals(action)
					|| END_RECORDTIME_THREAD_ACTION.equals(action)) { // Screen
																		// off
				screenOn = false;
			} else if (Intent.ACTION_TIME_TICK.equals(action)) {
				// allday usephone time to be saved

				saveDayTime();

				Log.i("testtime", "time changed");
			}
		}

		/**
		 * judge time now and save allday time here
		 */
		private void saveDayTime() {
			Date date = new Date();

			int year = date.getYear() + 1900;
			int month = date.getMonth() + 1;
			int day = date.getDay() + 1;

			int hours = date.getHours();
			int minutes = date.getMinutes();

			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd");

			String format = dateFormat.format(date);

			String saveTime = year + "" + month + day;

			Log.i("testtime", "now time is : " + year + "-" + month + "-" + day
					+ "  " + minutes + "/" + hours);
			Log.i("testtime", "simple : " + format);

			String lastSaveTime = mSp.getString("lastsavetime", "");

			if ((!TextUtils.isEmpty(lastSaveTime) && !saveTime
					.equals(lastSaveTime)) || (hours == 0 && minutes == 0)) { // saveTime

				long time = mSp.getLong("usephone_time_today", 0);
				mSpEdit.putString("lastsavetime", saveTime);

				mSpEdit.putLong(HISTORY_START + format, time);

				mSpEdit.putLong("usephone_time_today", 0);

				mSpEdit.apply();

				new Thread(new Runnable() {

					public void run() {

						if (screenOn) {
							Intent intent = new Intent(
									END_RECORDTIME_THREAD_ACTION);
							sendBroadcast(intent); // 关掉recordTime子线程

							SystemClock.sleep(1000);
							intent = new Intent(START_RECORDTIME_THREAD_ACTION);
							sendBroadcast(intent); // 启动recordTime子线程
						}
					}
				}).start();

				Log.i("testtime", "*****save all day time***");
			}

		}
	}

}
