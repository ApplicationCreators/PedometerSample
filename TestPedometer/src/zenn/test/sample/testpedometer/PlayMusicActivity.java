package zenn.test.sample.testpedometer;

import zenn.test.sample.testpedometer.WalkActivity.ViewRefresher;
import zenn.test.sample.testpedometer.service.WalkCounterBinder;
import zenn.test.sample.testpedometer.service.WalkCounterOnPlayingBinder;
import zenn.test.sample.testpedometer.service.WalkCounterOnPlayingService;
import zenn.test.sample.testpedometer.service.WalkCounterReceiver;
import zenn.test.sample.testpedometer.service.WalkCounterService;
import zenn.test.sample.testpedometer.utils.BGMPlayer;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PlayMusicActivity extends Activity{
	public static final String TAG = MainActivity.APP_TAG+"PlayMusicActivity";
	
	private BGMPlayer bgm;
	
	ViewRefresher thread;

	private WalkCounterOnPlayingService walkCounterService;
	private final WalkCounterReceiver receiver = new WalkCounterReceiver();
	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			walkCounterService = ((WalkCounterOnPlayingBinder) service).getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			walkCounterService = null;
		}

	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_view);
		
		// タイトルを取得
		Intent it = getIntent();
		String title = it.getStringExtra("title");
		String file = it.getStringExtra("file");
		
		TextView textView = (TextView) findViewById(R.id.title);
		textView.setText(title);
		
		// 戻るボタンの設定
		Button backButton = (Button) findViewById(R.id.back);
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		// BGMを取得
		int resid = getResources().getIdentifier(file, "raw", getPackageName());
		bgm = new BGMPlayer(this, resid);
		bgm.start();
		
		/////// 歩数計を開始
		// サービスを開始
		Intent intent = new Intent(this, WalkCounterOnPlayingService.class);
		startService(intent);
		IntentFilter filter = new IntentFilter(WalkCounterOnPlayingService.ACTION);
		registerReceiver(receiver, filter);

		// サービスにバインド
		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
		
		// 画面表示を行う
		thread = new ViewRefresher();
		thread.start();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		bgm.stop();
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
