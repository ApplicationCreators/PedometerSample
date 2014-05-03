package zenn.test.sample.testpedometer;

import java.util.ArrayList;

import zenn.test.sample.testpedometer.WalkActivity.ViewRefresher;
import zenn.test.sample.testpedometer.io.CSVReader;
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
	
	ViewRefresher thread;
	
	private ArrayList<String[]> rithmData;
	private ArrayList<Integer> walk_counter;
	private int current_index;
	private long count_accumulate;
	private long end_time;
	private long last_time;
	private long next_time;
	// ラップ用
	private ArrayAdapter<String> adapter;

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
		long length = Integer.parseInt(it.getStringExtra("length")) * 1000;
		
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
		
		// リズムファイルを取得
		AssetManager manager = getAssets();
		CSVReader reader = new CSVReader(manager, "rithm/"+file+".csv");
		
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
		
		/////// ゲームのデータを初期化
		current_index = 0;
		count_accumulate = 0;
		rithmData = reader.getData();
		walk_counter = new ArrayList<Integer>();
		end_time = System.currentTimeMillis() + length;
		
		/////// ゲーム開始
		// 音楽を鳴らす
		bgm.start();
		last_time = System.currentTimeMillis();
		next_time = (long)Integer.parseInt(rithmData.get(0)[0]) * 1000;
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
		unbindService(serviceConnection); // バインド解除
		unregisterReceiver(receiver); // 登録解除
	}
	
	class ViewRefresher extends Thread {
		public static final String TAG = WalkActivity.TAG+".ViewRefresher";
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
			current_theme.setText(String.valueOf(next_time));
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
			if(System.currentTimeMillis() > last_time+ next_time){
				current_index++;
				if(current_index < rithmData.size()){
					int lap_count = (int) (walkCounterService.getCounter() - count_accumulate);
					int lap_ans_diff = 0;
					if(current_index == 1){
						lap_ans_diff = Integer.parseInt(rithmData.get(current_index-1)[1]);
					}else if(current_index > 1){
						lap_ans_diff = Integer.parseInt(rithmData.get(current_index-1)[1])
								- Integer.parseInt(rithmData.get(current_index - 2)[1]);
					}
					adapter.add(next_time+"  "+lap_count+"  "+lap_ans_diff);
					count_accumulate = walkCounterService.getCounter();
					last_time = System.currentTimeMillis();
					next_time = (long)Integer.parseInt(rithmData.get(current_index)[0]) * 1000
									- (long)Integer.parseInt(rithmData.get(current_index - 1)[0]) * 1000;
				}
				else{
					next_time = Long.MAX_VALUE;
				}
				current_theme.setText(String.valueOf(next_time));
			}
		}
		
		public void judgeEnd(){
			if (System.currentTimeMillis() > end_time){
				stopPlaying();
				Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
				startActivity(intent);
			}
		}
	}
}
