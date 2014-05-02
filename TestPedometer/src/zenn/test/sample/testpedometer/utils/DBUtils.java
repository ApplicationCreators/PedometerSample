package zenn.test.sample.testpedometer.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBUtils {

	public static void dbTablesOutputLog(SQLiteOpenHelper handler) {
		final SQLiteDatabase db = handler.getReadableDatabase();
		String sql = "select * from sqlite_master;";
		Cursor c = db.rawQuery(sql, new String[] {});
		c.moveToFirst();
		String[] colname = c.getColumnNames();
		int rsize = c.getCount();
		Map<String, String> map = new HashMap<String, String>();
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < rsize; i++) {
			for (int j = 0; j < colname.length; j++) {
				String value = c.getString(c.getColumnIndex(colname[j]));
				map.put(colname[j], value);
			}
			Log.d("DATABASE_VIEW", "table info:::" + map.toString());
			if ("table".equals(map.get("type"))) {
				list.add(map.get("name"));
			}
			c.moveToNext();
		}
		c.close();

		for (int x = 0; x < list.size(); x++) {
			String sql2 = "select * from " + list.get(x) + ";";
			Cursor c2 = db.rawQuery(sql2, new String[] {});
			c2.moveToFirst();
			Map<String, String> map2 = new HashMap<String, String>();
			String[] colname2 = c2.getColumnNames();
			int rsize2 = c2.getCount();
			for (int i = 0; i < rsize2; i++) {
				for (int j = 0; j < colname2.length; j++) {
					String value = c2.getString(c2.getColumnIndex(colname2[j]));
					map2.put(colname2[j], value);
				}
				Log.d("DATABASE_VIEW", list.get(x) + ":::" +map2.toString());
				c2.moveToNext();
			}
			c2.close();
		}
	}
}
