package com.m_hi.android;

import android.app.Activity;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WalkCounterActivity extends Activity {
	/** Called when the activity is first created. */
	AccelerometerAdapter ad;
	GraphCounter thread;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button startButton = (Button) findViewById(R.id.startButton);
		Button stopButton = (Button) findViewById(R.id.stopButton);
		Button resetButton = (Button) findViewById(R.id.resetButton);
		TextView walkCount = (TextView) findViewById(R.id.walkcount);
		startButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(thread != null)
					thread.restart();
				else {
					startDrawingGraph();
				}
			}
		});
		stopButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(thread != null)
					thread.onceStop();
			}
		});
		resetButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ad.stopSensor();
				thread.reset();
				thread.close();
				thread = null;
			}
		});
		walkCount.setText("0");
		startDrawingGraph();
	}
	
	private void startDrawingGraph(){
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
		boolean running = true;
		
		TextView walk_count_view;
		int count = 0;

		public GraphCounter(AccelerometerAdapter ad) {
			this.ad = ad;
			grx = (GraphView) findViewById(R.id.graphViewX);
			gry = (GraphView) findViewById(R.id.graphViewY);
			grz = (GraphView) findViewById(R.id.graphViewZ);
			grv = (GraphView) findViewById(R.id.graphViewV);
			walk_count_view =  (TextView) findViewById(R.id.walkcount);
		}

		public void close() {
			running = false;
			runflg = false;
		}
		
		public void reset(){
			grx.reset();
			gry.reset();
			grz.reset();
			grv.reset();
		}
		

		public void onceStop(){
			running = false;
		}
		public void restart(){
			running = true;
		}

		public void run() {
			while (runflg) {
				while(running){
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
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}