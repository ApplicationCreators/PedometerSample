package zenn.test.sample.testpedometer.activity;

import zenn.test.sample.testpedometer.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ResultActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result_view);
		Intent intent = getIntent();
		TextView text_view = (TextView) findViewById(R.id.result_view);
		text_view.setText(intent.getStringExtra("point"));
	}
	
}
