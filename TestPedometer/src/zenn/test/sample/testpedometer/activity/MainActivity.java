package zenn.test.sample.testpedometer.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import zenn.test.sample.testpedometer.MusicList;
import zenn.test.sample.testpedometer.R;
import zenn.test.sample.testpedometer.io.MusicFileHandler.MusicItem;
import zenn.test.sample.testpedometer.service.WalkCounterBinder;
import zenn.test.sample.testpedometer.service.WalkCounterReceiver;
import zenn.test.sample.testpedometer.service.WalkCounterService;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.os.Build;

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
		
	}
}

