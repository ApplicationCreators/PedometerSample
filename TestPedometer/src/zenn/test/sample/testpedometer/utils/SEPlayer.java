package zenn.test.sample.testpedometer.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class SEPlayer {
	
	private SoundPool mSoundPool;	// SoundPool
	private int mHitSound;			// 読み込んだ効果音オブジェクト
	
	public SEPlayer(Context context, int resid) {
		// SoundPoolを生成
		this.mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		this.mHitSound = mSoundPool.load(context, resid, 1);
	}
	
	public void playHitSound(){
		mSoundPool.play(mHitSound, 1.0f, 1.0f, 1, 0, 1.0f);
	}
}
