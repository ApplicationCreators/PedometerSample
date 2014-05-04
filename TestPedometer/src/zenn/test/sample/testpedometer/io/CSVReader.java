package zenn.test.sample.testpedometer.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import zenn.test.sample.testpedometer.activity.MainActivity;

import android.content.res.AssetManager;
import android.util.Log;

public class CSVReader {
	public static final String TAG = MainActivity.APP_TAG+"CSVReader";
	private ArrayList<String[]> data;

	public CSVReader(AssetManager manager, String path) {
		try {
			InputStream input = manager.open(path);
			InputStreamReader reader = new InputStreamReader(input);
			BufferedReader bufReader = new BufferedReader(reader);
			data = new ArrayList<String[]>();
			String line = null;
			while((line = bufReader.readLine()) != null){
				String[] strings = line.split(",");
				data.add(strings);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public ArrayList<String[]> getData() {
		return data;
	}
}
