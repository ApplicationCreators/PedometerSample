package com.m_hi.android;

import android.app.Activity;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class WalkCounterActivity extends Activity {
	/** Called when the activity is first created. */
	AccelerometerAdapter ad;
	GraphCounter thread;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		SensorManager manager = (SensorManager) getSystemService(SENSOR_SERVICE);
		ad = new AccelerometerAdapter(manager);
		thread = new GraphCounter(ad);
		thread.start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ad.stopSensor();
		thread.close();
	}

	class GraphCounter extends Thread {
		AccelerometerAdapter ad;
		Handler handler = new Handler();

		TextView tvx;
		TextView tvy;
		TextView tvz;
		TextView tvv;
		GraphView grx;
		GraphView gry;
		GraphView grz;
		GraphView grv;
		boolean runflg = true;

		public GraphCounter(AccelerometerAdapter ad) {
			this.ad = ad;
			grx = (GraphView) findViewById(R.id.graphViewX);
			gry = (GraphView) findViewById(R.id.graphViewY);
			grz = (GraphView) findViewById(R.id.graphViewZ);
			grv = (GraphView) findViewById(R.id.graphViewV);
		}

		public void close() {
			runflg = false;
		}

		public void run() {
			while (runflg) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						grx.setDiv(ad.getDx());
						gry.setDiv(ad.getDy());
						grz.setDiv(ad.getDz());
						grv.setDiv(ad.getDv());
						grx.invalidate();
						gry.invalidate();
						grz.invalidate();
						grv.invalidate();
					}
				});
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}