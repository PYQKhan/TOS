package com.pyq.timeonscreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.example.viewpagerdemo.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
	private RecordTimeChangedReceiver mReceiver;
	private TextView mTvDisplayTime;
	private SharedPreferences mSp;
	private Editor mSpEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		init();

		Intent serviceIntent = new Intent();
		serviceIntent.setAction(Constants.RECORD_TIME_SERVICE_ACTION);
		startService(serviceIntent);

		Log.i("testthread", "UI thread id is " + Thread.currentThread().getId());

	}

	@Override
	protected void onStart() {
		super.onStart();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Constants.RECORD_TIME_RECEIVER_ACTION);
		mReceiver = new RecordTimeChangedReceiver();

		registerReceiver(mReceiver, intentFilter);

	}

	@Override
	protected void onStop() {
		super.onStop();

		unregisterReceiver(mReceiver);
	}

	private void init() {

		mSp = getSharedPreferences("config", MODE_PRIVATE);
		mSpEdit = mSp.edit();

		mTvDisplayTime = (TextView) findViewById(R.id.main_tv_displaytime);
		ListView lvHistory = (ListView) findViewById(R.id.main_lv_history);

		lvHistory.setAdapter(new HistoryAdapter());

	}

	public void onClick(View arg0) {
		switch (arg0.getId()) {

		default:
			break;
		}
	}

	private String formatTime(long time) {
		String result = "";

		long hours = time / 60 / 60;
		long minute = (time - (hours * 60 * 60)) / 60;
		long second = time - (hours * 60 * 60 + minute * 60);

		result = hours + " : " + minute + " : " + second;

		return result;
	}

	class HistoryAdapter extends BaseAdapter {
		private List<String> historyData = new ArrayList<String>();

		public HistoryAdapter() {
			Map<String, Object> spValues = (Map<String, Object>) mSp.getAll();

			Set<Entry<String, Object>> entrySet = spValues.entrySet();

			for (Entry<String, Object> entry : entrySet) {
				String key = entry.getKey();
				Object value = entry.getValue();

				if (key.startsWith("history")) {
					String va = key + "    " + formatTime((Long) value);
					historyData.add(va);
				}
			}
		}

		public int getCount() {
			return historyData.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			TextView tv = new TextView(MainActivity.this);

			tv.setText(historyData.get(position));

			return tv;
		}
	}

	class RecordTimeChangedReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (Constants.RECORD_TIME_RECEIVER_ACTION
					.equals(intent.getAction())) {
				long useTime = intent.getLongExtra("time", 0);

				mTvDisplayTime.setText(formatTime(useTime));
			}
		}
	}
}
