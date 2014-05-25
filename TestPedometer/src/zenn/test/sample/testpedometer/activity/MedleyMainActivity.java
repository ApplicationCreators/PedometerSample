package zenn.test.sample.testpedometer.activity;

import zenn.test.sample.testpedometer.MusicList;
import zenn.test.sample.testpedometer.R;
import zenn.test.sample.testpedometer.io.MusicFileHandler.MedleyItem;
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

public class MedleyMainActivity extends Activity{
	public static String TAG = MainActivity.APP_TAG+"MedleyMainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play_list);
		
		// アッセットへのアクセス
		AssetManager assetManager = getAssets();
		final MusicList musicList = new MusicList(assetManager, "lists/music_list.xml");
		
		// リストを用意する
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		for(MedleyItem item : musicList.getMedleys()){
			adapter.add(item.name);
		}
		ListView listView = (ListView) findViewById(R.id.lap);
		listView.setAdapter(adapter);
		// クリックしたら音楽プレイ画面へ行くようにする
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Log.d(TAG, "arg2 : "+ arg2+" arg3 : "+arg3);
				Intent intent = new Intent(getApplicationContext(), PlayMedleyActivity.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				MedleyItem item =  musicList.getMedleys().get((int)arg3);
				intent.putExtra("name",item.name);
				intent.putExtra("ids", item.ids);
				startActivity(intent);
			}
		});
		
		Button button = (Button) findViewById(R.id.medley_button);
		button.setText("Music");
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(intent);
			}
		});
	}
}
