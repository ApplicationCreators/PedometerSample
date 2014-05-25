package zenn.test.sample.testpedometer.utils;

import zenn.test.sample.testpedometer.activity.MainActivity;
import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

public class BGMPlayer {
	public static final String TAG = MainActivity.APP_TAG+"BGMPlayer";
	private MediaPlayer mBgm;
	
	public BGMPlayer(Context context, int resid) {
		// BGMファイルを読み込む
		this.mBgm = MediaPlayer.create(context, resid);
		this.mBgm.setLooping(false);		// ループしないようにする
//		this.mBgm.setVolume(1.0f, 1.0f);	// 左右のボリュームを最大にする
		this.mBgm.setVolume(0.8f, 0.8f);	// 左右のボリュームを最大にする
	}
	
	// BGMを再生する
	public void start(){
		if(!mBgm.isPlaying()){
			mBgm.seekTo(0);
			mBgm.start();
		}
	}
	// BGMを停止する
	public void stop(){
		if(mBgm.isPlaying()){
			mBgm.stop();
			mBgm.prepareAsync();
		}
	}
	
	// BGMをリリースする
	public void release(){
		if(!mBgm.isPlaying()){
			mBgm.release();
		}
		else{
			Log.e(TAG, "RELEASE ERROR!!");
		}
	}
	
	public void change(int resid){
		mBgm.setAudioSessionId(resid);
	}
}
