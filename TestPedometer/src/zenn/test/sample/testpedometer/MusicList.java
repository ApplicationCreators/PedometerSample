package zenn.test.sample.testpedometer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import zenn.test.sample.testpedometer.activity.MainActivity;
import zenn.test.sample.testpedometer.io.MusicFileHandler;
import zenn.test.sample.testpedometer.io.MusicFileHandler.MusicItem;
import android.content.res.AssetManager;
import android.util.Log;

public class MusicList {
	public static final String TAG = MainActivity.APP_TAG+"MusicList";
	
	private ArrayList<MusicItem> musics;
	
	public MusicList(AssetManager manager, String path) {
		musics = new ArrayList<MusicFileHandler.MusicItem>();
		try {
			InputStream mInput = manager.open(path);
			
			// SAXを使って応答データを構文解析する
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser p = factory.newSAXParser();
			MusicFileHandler parser = new MusicFileHandler();
			// 解析処理の実行
			p.parse(new InputSource(mInput), parser);
			musics = parser.getMusicItems();
		} catch (IOException e) {
			Log.e(TAG, "ERROR! NO ASSET FILE.");
			e.printStackTrace();
		} catch (SAXException e) {
			Log.e(TAG, "ERROR! SAX Failes...");
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			Log.e(TAG, "ParserConfigurationException");
			e.printStackTrace();
		}
	}
	
	public ArrayList<MusicItem> getMusics() {
		return musics;
	}
}
