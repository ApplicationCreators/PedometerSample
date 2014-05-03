package zenn.test.sample.testpedometer.service;

import zenn.test.sample.testpedometer.DatabaseHelper;
import zenn.test.sample.testpedometer.MainActivity;
import zenn.test.sample.testpedometer.WalkCounterMaster;
import android.app.Service;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


public class WalkCounterOnPlayingService extends Service {
	public static final String TAG = MainActivity.APP_TAG+"WalkCounterOnPlayingService";

	public static final String ACTION = "Walk Counter On Playing Service";
	private WalkCounterOnPlayingMaster master;

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		super.onCreate();
		Toast toast = Toast.makeText(getApplicationContext(), "onCreate()",
				Toast.LENGTH_SHORT);
		toast.show();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.d(TAG, "onStart");
		super.onStart(intent, startId);
		SensorManager manager = (SensorManager) getSystemService(SENSOR_SERVICE);
		master = new WalkCounterOnPlayingMaster(manager);
		Toast toast = Toast.makeText(getApplicationContext(), "onStart()",
				Toast.LENGTH_SHORT);
		toast.show();

	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
		Toast toast = Toast.makeText(getApplicationContext(), "onDestroy()",
				Toast.LENGTH_SHORT);
		toast.show();
		master.stopSensor();
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind");
		// onUnbind(Intent intent)の戻りをfalseにし、再バインドされた場合。
		Toast toast = Toast.makeText(getApplicationContext(), "onBind()",
				Toast.LENGTH_SHORT);
		toast.show();
		return new WalkCounterOnPlayingBinder(this);
	}

	@Override
	public void onRebind(Intent intent) {
		Log.d(TAG, "onRebind");
		// onUnbind(Intent intent)の戻りをtrueにし、再バインドされた場合。
		Toast toast = Toast.makeText(getApplicationContext(), "onRebind()",
				Toast.LENGTH_SHORT);
		toast.show();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(TAG, "onUnbind");
		Toast toast = Toast.makeText(getApplicationContext(), "onUnbind()",
				Toast.LENGTH_SHORT);
		toast.show();
		// アンバインド後に再度バインドすると、再びサービスに接続しますが、
		// このときにService#onBind(Intent)メソッドが呼び出されるか、
		// Service#onRebind(Intent)メソッドが呼び出されるかは、
		// Service#onUnbind(Intent)メソッドで返すbooleanの値をtrueにするかfalseにするかで選択

		return true; // 再度クライアントから接続された際に onRebind を呼び出させる場合は true を返す
	}

	public long getCounter() {
		return (master==null)?-1:master.getCounter();
	}


}
