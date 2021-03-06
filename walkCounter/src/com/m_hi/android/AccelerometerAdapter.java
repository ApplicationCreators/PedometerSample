package com.m_hi.android;

import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class AccelerometerAdapter implements SensorEventListener {

	private SensorManager manager;

	private double threashold = -5;

	private float oldx = 0;
	private float oldy = 0;
	private float oldz = 0;

	private boolean initialized = false;
	private double[] initial_vecor = new double[3];
	private double init_vector_length = 0;

	private float dx = 0;
	private float dy = 0;
	private float dz = 0;
	private double dv = 0;


	// 歩数について
	private boolean walk = false;
	private boolean go_back = false;	// 歩き判定から、cosが1.0近くに戻ったか
	private double last_cos = 1.0;
	private long last_time;
	private long interval;

	public float getDx() {
		return dx;
	}

	public void setDx(float dx) {
		this.dx = dx;
	}

	public float getDy() {
		return dy;
	}

	public void setDy(float dy) {
		this.dy = dy;
	}

	public float getDz() {
		return dz;
	}

	public void setDz(float dz) {
		this.dz = dz;
	}

	public double getDv() {
		return dv;
	}

	public boolean getWalk(){
		if(walk){
			walk = false;
			return true;
		}
		else
			return false;
	}

	public void setDv(float dv) {
		this.dv = dv;
	}

	public void setThreadshold(double value){
		this.threashold = value;
	}
	public double getThreashold(){
		return this.threashold;
	}

	public AccelerometerAdapter(SensorManager manager) {
		List<Sensor> sensors = manager
				.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if (sensors.size() > 0) {
			Sensor s = sensors.get(0);
			manager.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
		}
		last_time = System.currentTimeMillis();
		interval = 100;
	}

	public void stopSensor() {
		if ( manager != null )
			manager.unregisterListener(this);
		manager = null;
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if(System.currentTimeMillis() - last_time > interval){
			last_time = System.currentTimeMillis();
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				dx = (event.values[0] - oldx)/2;
				dy = (event.values[1] - oldy)/2;
				dz = (event.values[2] - oldz)/2;

				if(!initialized){
					// 初期値を設定
					//				double amount = (event.values[0] * event.values[0] + event.values[1] * event.values[1] + event.values[2] * event.values[2]);
					//				Log.d("Accelero", ""+amount);
					initial_vecor[0] = event.values[0];
					initial_vecor[1] = event.values[1];
					initial_vecor[2] = event.values[2];
					init_vector_length = Math.sqrt(event.values[0] * event.values[0] + event.values[1] * event.values[1] + event.values[2] * event.values[2]);
					dv = 0;
					initialized = true;
				}
				else {
					// 内積を求める
					/////// 現在の値を利用する
//					double naiseki = event.values[0] * initial_vecor[0] + event.values[1] * initial_vecor[1] + event.values[2] * initial_vecor[2];
//					double current_length = Math.sqrt(event.values[0] * event.values[0] + event.values[1] * event.values[1] + event.values[2] * event.values[2]);
					/////// 変化値を利用する
					double naiseki = dx * initial_vecor[0] + dy * initial_vecor[1] + dz * initial_vecor[2];
					double current_length = Math.sqrt(dx * dx + dy * dy + dz * dz);
					double cos = naiseki/(init_vector_length * current_length);
					
					if(current_length > 3)
						dv = cos * 10.0;
					else {
						dv = 10;
					}
					Log.d("Accelero", ""+dv);
					//				dv =Math.sqrt((dx * dx + dy * dy + dz * dz)/2);
				}
				//			dv =Math.sqrt((dx * dx + dy * dy + dz * dz)/2);

				if(go_back && dv < threashold){
					go_back = false;
					walk = true;
					/////// 現在の値を利用する
//					initial_vecor[0] = event.values[0];
//					initial_vecor[1] = event.values[1];
//					initial_vecor[2] = event.values[2];
//					init_vector_length = Math.sqrt(event.values[0] * event.values[0] + event.values[1] * event.values[1] + event.values[2] * event.values[2]);
					/////// 変化値を利用する
					initial_vecor[0] = dx;
					initial_vecor[1] = dy;
					initial_vecor[2] = dz;
					init_vector_length = Math.sqrt(dx * dx + dy * dy + dz * dz);
				}
				if(!go_back && dv > threashold){
					go_back = true;
				}

				oldx = event.values[0];
				oldy = event.values[1];
				oldz = event.values[2];


			}
		}
	}

}
