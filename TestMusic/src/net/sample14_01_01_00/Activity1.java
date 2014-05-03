package net.sample14_01_01_00;

import android.app.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.media.*;
import android.media.MediaPlayer.*;

public class Activity1 extends Activity {
	private Button[] varButton = new Button[3];
	private MediaPlayer myMediaPlayer;
	private final static int WC = LinearLayout.LayoutParams.WRAP_CONTENT;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LinearLayout varLinearLayout = new LinearLayout(this);
		varLinearLayout.setOrientation(LinearLayout.VERTICAL);
		setContentView(varLinearLayout);

		for (int i = 0; i < varButton.length; i++) {
			varButton[i] = new Button(this);
			varButton[i].setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
		}

		varButton[0].setText("PLAY -");
		varButton[1].setText("PAUSE");
		varButton[2].setText("STOP -");

		varButton[0].setEnabled(true);
		varButton[1].setEnabled(false);
		varButton[2].setEnabled(false);

		for (int i = 0; i < varButton.length; i++) {
			varLinearLayout.addView(varButton[i]);
			varButton[i].setOnClickListener(new SampleClickListener());
		}
	}

//	public void onResume() {
//		super.onResume();
//		myMediaPlayer = MediaPlayer.create(this, R.raw.miku);
//		myMediaPlayer.setOnCompletionListener(new SampleCompletionListener());
//	}

	public void onPause() {
		super.onPause();
		myMediaPlayer.release();
	}

	class SampleCompletionListener implements OnCompletionListener {
		public void onCompletion(MediaPlayer mp) {
			varButton[0].setEnabled(true);
			varButton[1].setEnabled(false);
			varButton[2].setEnabled(false);
		}
	}

	class SampleClickListener implements OnClickListener {
		public void onClick(View v) {
			if (v == varButton[0]) {
				varButton[0].setEnabled(false);
				varButton[1].setEnabled(true);
				varButton[2].setEnabled(true);
				myMediaPlayer.start();
			} else if (v == varButton[1]) {
				if (myMediaPlayer.isPlaying()) {
					varButton[0].setEnabled(true);
					varButton[1].setEnabled(true);
					varButton[2].setEnabled(true);
					myMediaPlayer.pause();
				} else {
					varButton[0].setEnabled(true);
					varButton[1].setEnabled(true);
					varButton[2].setEnabled(true);
					myMediaPlayer.start();
				}
			} else if (v == varButton[2]) {
				varButton[0].setEnabled(true);
				varButton[1].setEnabled(false);
				varButton[2].setEnabled(false);
				myMediaPlayer.pause();
				myMediaPlayer.seekTo(0);
			}
		}
	}
}