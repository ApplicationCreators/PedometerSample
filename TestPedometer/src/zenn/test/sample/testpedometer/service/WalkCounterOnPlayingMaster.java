package zenn.test.sample.testpedometer.service;

import java.util.Date;
import java.util.List;

import zenn.test.sample.testpedometer.MainActivity;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class WalkCounterOnPlayingMaster implements SensorEventListener {
	public static final String TAG = MainActivity.APP_TAG+"WalkCounterMaster";

	private SensorManager manager;

	private float oldx = 0f;
	private float oldy = 0f;
	private float oldz = 0f;

	private float dx = 0f;
	private float dy = 0f;
	private float dz = 0f;

	// 重複カウント防止用フラグ
	boolean counted = true;
	// 歩数カウンター
	long counter = 0;

	// ベクトル量の増加フラグ
	boolean vectorUp = true;
	// １つ前のベクトル量
	double oldVectorSize = 0;
	// 現在ベクトル量
	double vectorSize = 0;
	// ピックアップベクトル量
	double picupVectorSize = 0;
	// ベクトル変化を検知した時間
	long changeTime = 0;

	// 閾値
	double threshold = 15;
	// 軸方向転換の最小閾値
	double thresholdMin = 1;
	// ベクトル変化検出しない時間の閾値
	long thresholdTime = 190;

	// X軸加速方向
	boolean vecx = true;
	// Y軸加速方向
	boolean vecy = true;
	// Z軸加速方向
	boolean vecz = true;
	// 加速度方向の転換回数
	int vecchangecount = 0;

	public float getDx() {
		return dx;
	}

	public float getDy() {
		return dy;
	}

	public float getDz() {
		return dz;
	}

	public double getVectorSize() {
		return vectorSize;
	}

	public long getCounter() {
		return counter;
	}

	public WalkCounterOnPlayingMaster(SensorManager manager) {
		this.counter = 0;
		List<Sensor> sensors = manager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if (sensors.size() > 0) {
			Sensor s = sensors.get(0);
			manager.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	public void stopSensor() {
		if (manager != null)
			manager.unregisterListener(this);
		manager = null;
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			Log.v(TAG, "onSensorChanged.TYPE_ACCELEROMETER");
			// 増加量
			dx = event.values[0] - oldx;
			dy = event.values[1] - oldy;
			dz = event.values[2] - oldz;
			// ベクトル量をピタゴラスの定義から求める。
			// が正確な値は必要でなく、消費電力から平方根まで求める必要はない
			// vectorSize = Math.sqrt((double)(dx*dx+dy*dy+dz*dz));
			vectorSize = (dx * dx + dy * dy + dz * dz);

			// ベクトル計算を厳密に行うと計算量が上がるため、簡易的な方向を求める。
			// 一定量のベクトル量があり向きの反転があった場合（多分走った場合）
			// vecchangecountはSENSOR_DELAY_NORMALの場合、200ms精度より
			// 加速度変化が検出できないための専用処理。精度を上げると不要
			// さらに精度がわるいことから、連続のベクトル変化は検知しない。
			long dt = new Date().getTime() - changeTime;
			boolean dxx = Math.abs(dx) > thresholdMin && vecx != (dx >= 0);
			boolean dxy = Math.abs(dy) > thresholdMin && vecy != (dy >= 0);
			boolean dxz = Math.abs(dz) > thresholdMin && vecz != (dz >= 0);
			if (vectorSize > threshold && dt > thresholdTime
					&& (dxx || dxy || dxz)) {
				vecchangecount++;
				changeTime = new Date().getTime();
			}

			// ベクトル量がある状態で向きが２回（上下運動とみなす）変わった場合
			// または、ベクトル量が一定値を下回った（静止とみなす）場合、カウント許可
			if (vecchangecount > 1 || vectorSize < 1) {
				counted = false;
				vecchangecount = 0;
			}

			// カウント許可で、閾値を超えるベクトル量がある場合、カウント
			if (!counted && vectorSize > threshold) {
				counter++;
				counted = true;
				vecchangecount = 0;
				Log.d(TAG,"counter:" + counter);
			}

			// カウント自の加速度の向きを保存
			vecx = dx >= 0;
			vecy = dy >= 0;
			vecz = dz >= 0;

			// 状態更新
			oldVectorSize = vectorSize;

			// 加速度の保存
			oldx = event.values[0];
			oldy = event.values[1];
			oldz = event.values[2];
		}
	}

}
