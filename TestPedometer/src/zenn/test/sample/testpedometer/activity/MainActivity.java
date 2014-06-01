package zenn.test.sample.testpedometer.activity;

import zenn.test.sample.testpedometer.MusicList;
import zenn.test.sample.testpedometer.R;
import zenn.test.sample.testpedometer.io.MusicFileHandler.MusicItem;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends Activity{
	public static final String APP_TAG = "TestPedometer.";
	public static final String TAG = APP_TAG+"MainActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play_list);
		
		// アッセットへのアクセス
		AssetManager assetManager = getAssets();
		final MusicList musicList = new MusicList(assetManager, "lists/music_list.xml");
		
		// リストを用意する
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		for(MusicItem item : musicList.getMusics()){
			adapter.add(item.title);
		}
		ListView listView = (ListView) findViewById(R.id.lap);
		listView.setAdapter(adapter);
		// クリックしたら音楽プレイ画面へ行くようにする
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Log.d(TAG, "arg2 : "+ arg2+" arg3 : "+arg3);
				Intent intent = new Intent(getApplicationContext(), PlayMusicActivity.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				MusicItem item =  musicList.getMusics().get((int)arg3);
				intent.putExtra("title",item.title);
				intent.putExtra("file", item.file);
				intent.putExtra("length", item.length);
				startActivity(intent);
			}
		});
		
		Button medleyButton = (Button) findViewById(R.id.medley_button);
		medleyButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), MedleyMainActivity.class);
				startActivity(intent);
			}
		});
		
	}
}

