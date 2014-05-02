package zenn.test.sample.testpedometer;

import java.util.Date;
import java.util.List;

import zenn.test.sample.testpedometer.service.WalkCounterBinder;
import zenn.test.sample.testpedometer.service.WalkCounterReceiver;
import zenn.test.sample.testpedometer.service.WalkCounterService;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.os.Build;

public class WalkActivity extends Activity{
	public static final String APP_TAG = "TestPedometer.";
	public static final String TAG = APP_TAG+"MainActivity";
	/** Called when the activity is first created. */

	ViewRefresher thread;

	private WalkCounterService walkCounterService;
	private final WalkCounterReceiver receiver = new WalkCounterReceiver();
	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			walkCounterService = ((WalkCounterBinder) service).getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			walkCounterService = null;
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// サービスを開始
		Intent intent = new Intent(this, WalkCounterService.class);
		startService(intent);
		IntentFilter filter = new IntentFilter(WalkCounterService.ACTION);
		registerReceiver(receiver, filter);

		// サービスにバインド
		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

		// いったんアンバインドしてから再度バインド
//		unbindService(serviceConnection);
//		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

		thread = new ViewRefresher();
		thread.start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// リソースがリークし、例外発生しない処置。
		thread.close();
		unbindService(serviceConnection); // バインド解除
		unregisterReceiver(receiver); // 登録解除

	}

	class ViewRefresher extends Thread {
		public static final String TAG = WalkActivity.TAG+".ViewRefresher";
		Handler handler = new Handler();

		TextView counter;
		boolean runflg = true;

		public ViewRefresher() {
			counter = (TextView) findViewById(R.id.counter);
		}

		public void close() {
			runflg = false;
		}

		@Override
		public void run() {
			Log.d(TAG, "run");
			while (runflg) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						try {
							counter.setText(""+walkCounterService.getCounter());
						} catch (Exception e) {
						}
					}
				});
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
