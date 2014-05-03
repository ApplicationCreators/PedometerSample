package zenn.test.sample.testpedometer;

import zenn.test.sample.testpedometer.utils.BGMPlayer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PlayMusicActivity extends Activity{
	public static final String TAG = MainActivity.APP_TAG+"PlayMusicActivity";
	
	private BGMPlayer bgm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_view);
		
		// タイトルを取得
		Intent it = getIntent();
		String title = it.getStringExtra("title");
		
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
		bgm = new BGMPlayer(this, R.raw.senbonzakura);
		bgm.start();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		bgm.stop();
	}
}
