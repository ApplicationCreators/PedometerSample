package com.m_hi.android;

import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class AccelerometerAdapter implements SensorEventListener {

	private SensorManager manager;

	private float oldx = 0;
	private float oldy = 0;
	private float oldz = 0;

	private float dx = 0;
	private float dy = 0;
	private float dz = 0;
	private double dv = 0;
	
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

	public void setDv(float dv) {
		this.dv = dv;
	}

	public AccelerometerAdapter(SensorManager manager) {
		List<Sensor> sensors = manager
				.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if (sensors.size() > 0) {
			Sensor s = sensors.get(0);
			manager.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
		}
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
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			dx = (event.values[0] - oldx)/2;
			dy = (event.values[1] - oldy)/2;
			dz = (event.values[2] - oldz)/2;
			dv =Math.sqrt((dx * dx + dy * dy + dz * dz)/2);
			
			oldx = event.values[0];
			oldy = event.values[1];
			oldz = event.values[2];
			
			
		}
	}

}
