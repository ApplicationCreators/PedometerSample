package zenn.test.sample.testpedometer.activity;

import java.util.HashMap;

import zenn.test.sample.testpedometer.R;
import zenn.test.sample.testpedometer.activity.MainActivity;
import zenn.test.sample.testpedometer.io.CSVReader;
import zenn.test.sample.testpedometer.io.MusicFileHandler.MusicItem;
import zenn.test.sample.testpedometer.model.PlayingMonitor;
import zenn.test.sample.testpedometer.service.WalkCounterOnPlayingBinder;
import zenn.test.sample.testpedometer.service.WalkCounterOnPlayingService;
import zenn.test.sample.testpedometer.service.WalkCounterReceiver;
import zenn.test.sample.testpedometer.utils.BGMPlayer;
import zenn.test.sample.testpedometer.utils.SEPlayer;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class PlayMusicActivity extends Activity{
	public static final String TAG = MainActivity.APP_TAG+"PlayMusicActivity";
	
	private BGMPlayer bgm;
	private SEPlayer se_player;
	private HashMap<String, Integer> se_map;
	
	ViewRefresher thread;
	
	PlayingMonitor monitor;
	
	// ラップ用
	private ArrayAdapter<String> adapter;

	private WalkCounterOnPlayingService walkCounterService;
	private WalkCounterReceiver receiver = new WalkCounterReceiver();
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
		MusicItem music_item = new MusicItem();
		music_item.title = it.getStringExtra("title");
		music_item.file = it.getStringExtra("file");
		music_item.length = it.getStringExtra("length");
		
		Log.d(TAG, "length : "+music_item.length);
		
		TextView textView = (TextView) findViewById(R.id.title);
		textView.setText(music_item.title);
		
		// 戻るボタンの設定
		Button backButton = (Button) findViewById(R.id.back);
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		// BGMを取得
		int resid = getResources().getIdentifier(music_item.file, "raw", getPackageName());
		bgm = new BGMPlayer(this, resid);
		
		// SEの用意
		se_map = new HashMap<String, Integer>();
		se_player = new SEPlayer(this);
		se_map.put("Great", se_player.registerSE(R.raw.great));
		se_map.put("Good", se_player.registerSE(R.raw.good));
		se_map.put("Slow", se_player.registerSE(R.raw.fast));
		se_map.put("Fast", se_player.registerSE(R.raw.slowly));
		
		// リズムファイルを取得
		AssetManager manager = getAssets();
		CSVReader reader = new CSVReader(manager, "rithm/"+music_item.file+".csv");
		
		// PlayingMonitorの初期化。
		monitor = new PlayingMonitor(music_item, reader.getData(), System.currentTimeMillis());
		
		/////// 歩数計を開始
		// サービスを開始
		Intent intent = new Intent(this, WalkCounterOnPlayingService.class);
		startService(intent);
		IntentFilter filter = new IntentFilter(WalkCounterOnPlayingService.ACTION);
		registerReceiver(receiver, filter);
		// サービスにバインド
		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

		///// ラップの表示
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		ListView lapView = (ListView) findViewById(R.id.lap);
		lapView.setAdapter(adapter);
		
		/////// ゲーム開始
		// 音楽を鳴らす
		bgm.start();
		// 画面表示を行う
		thread = new ViewRefresher();
		thread.start();
	}
	
	@Override
	protected void onPause() {
		Log.d(TAG, "onPause");
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		Log.d(TAG, "onStop");
		super.onStop();

	}
	
	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
		stopPlaying();
	}
	
	public void stopPlaying(){
		Log.d(TAG, "stopPlaying");
		bgm.stop();
		// リソースがリークし、例外発生しない処置。
		thread.close();
		if(serviceConnection != null){
			unbindService(serviceConnection); // バインド解除
			serviceConnection = null;
		}
		if(receiver != null){
			unregisterReceiver(receiver); // 登録解除
			receiver = null;
		}
	}
	
	class ViewRefresher extends Thread {
		public static final String TAG = PlayMusicActivity.TAG+".ViewRefresher";
		Handler handler = new Handler();

		TextView counter;
		TextView current_theme;
		
		boolean runflg = true;

		public ViewRefresher() {
			counter = (TextView) findViewById(R.id.counter);
			current_theme = (TextView) findViewById(R.id.current_theme);
		}

		public void close() {
			runflg = false;
		}

		@Override
		public void run() {
			Log.d(TAG, "run");
			current_theme.setText(String.valueOf(monitor.getNextPhaseInterval()));
			while (runflg) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						try {
							// 次のフェーズの判定
							judgeNext();
							counter.setText(""+walkCounterService.getCounter());
							// 終了判定
							judgeEnd();
						} catch (Exception e) {
						}
					}
				});
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		public void judgeNext(){
			if(monitor.judgeNext(System.currentTimeMillis(), walkCounterService.getCounter())){
				adapter.add(monitor.getLastPhaseInterval()+"  "+monitor.getLastWalkCount()+"  "+monitor.getLastAnswerCount());
				current_theme.setText(String.valueOf(monitor.getNextPhaseInterval()));
				String result = monitor.getLastPhaseResult();
				Integer se = se_map.get(result);
				if(se != null){
					Log.d(TAG, "Play SE "+result);
					se_player.play(se.intValue());
				}
			}
		}
		
		public void judgeEnd(){
			if (monitor.judgeEnd(System.currentTimeMillis())){
				stopPlaying();
				Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
				intent.putExtra("point", monitor.getResult());
				startActivity(intent);
			}
		}
	}
}
