package zenn.test.sample.testpedometer.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class WalkCounterReceiver extends BroadcastReceiver {

	public WalkCounterReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// サービスからブロードキャストされたIntentを受け取った際に呼び出されるコールバックメソッド
		String action = intent.getAction();
		// 電源オン時に呼び出される。
		if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
			Toast.makeText(context, "起動をしたのでサービスを開始します。", 1200).show();
			context = context.getApplicationContext();
			Intent service = new Intent(context, WalkCounterService.class);
			context.startService(service);
		}
	}
}
