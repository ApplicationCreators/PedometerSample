package zenn.test.sample.testpedometer;

import java.util.Date;

import zenn.test.sample.testpedometer.io.DBUtils;
import zenn.test.sample.testpedometer.utils.WalkCounterUtils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class DatabaseHelper extends SQLiteOpenHelper {

	public DatabaseHelper(Context context) {
		super(context, "WalkCounter", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.beginTransaction();
		try {
			SQLiteStatement stmt;
			// 歩数情報作成
			db.execSQL("CREATE TABLE WORK_COUNTER_INFO (N_CNT INTEGER, REG_DATE TEXT);");
			stmt = db
					.compileStatement("INSERT INTO WORK_COUNTER_INFO VALUES(0, ?);");
			String date = WalkCounterUtils.dateformat("yyyyMMddHHmmss",
					new Date(), 0);
			stmt.bindString(1, date);
			stmt.executeInsert();

			// 歩数履歴作成
			db.execSQL("CREATE TABLE WORK_COUNTER_HISTORY (HISTORY_DATE TEXT PRIMARY KEY, H_CNT INTEGER)");

			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
	}

	/**
	 * 現在歩数を取得します。
	 *
	 * @return 現在歩数
	 */
	public long selectCount() {
		final SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery("SELECT N_CNT FROM WORK_COUNTER_INFO;", null);
		c.moveToFirst();
		long ret = c.getLong(0);
		c.close();
		DBUtils.dbTablesOutputLog(this);
		return ret;
	}

	/**
	 * 指定した過去日までの履歴を取得します。
	 *
	 * @param days
	 *            過去に取得する日
	 * @return １次（１：カウント、２：登録日）、２次（取得情報）
	 */
	public String[][] selectHistoryList(int days) {
		final SQLiteDatabase db = this.getReadableDatabase();
		String date = WalkCounterUtils
				.dateformat("yyyyMMdd", new Date(), -days);
		String sql = "SELECT H_CNT,HISTORY_DATE "
				+ "FROM WORK_COUNTER_HISTORY WHERE HISTORY_DATE > ? "
				+ "ORDER BY HISTORY_DATE DESC;";
		Cursor c = db.rawQuery(sql, new String[] { date });
		c.moveToFirst();
		int size = c.getCount();
		String[][] list = new String[2][size];
		for (int i = 0; i < list.length; i++) {
			for (int j = 0; j < list[i].length; j++) {
				list[i][j] = c.getString(0);
				list[i][j] = c.getString(1);
				c.moveToNext();
			}
		}
		c.close();
		return list;
	}

	/**
	 * 歩数を更新します。
	 *
	 * @param counter
	 *            更新歩数
	 */
	public void updateCount(long counter) {
		final SQLiteDatabase db = this.getReadableDatabase();
		db.beginTransaction();
		try {
			String date = WalkCounterUtils.dateformat("yyyyMMddHHmmss",
					new Date(), 0);
			db.execSQL(
					"UPDATE WORK_COUNTER_INFO SET N_CNT = ?, REG_DATE = ?; ",
					new String[] { "" + counter, date });
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
}
