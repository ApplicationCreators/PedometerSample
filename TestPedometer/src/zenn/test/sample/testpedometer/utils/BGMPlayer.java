package zenn.test.sample.testpedometer.utils;

import android.content.Context;
import android.media.MediaPlayer;

public class BGMPlayer {
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
	

}
