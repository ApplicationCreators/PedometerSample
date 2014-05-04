package zenn.test.sample.testpedometer.utils;

import java.util.ArrayList;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class SEPlayer {
	
	private SoundPool mSoundPool;	// SoundPool
	private Context context;
	private ArrayList<Integer> se_sounds;	// 読み込んだ効果音オブジェクト
	
	public SEPlayer(Context context) {
		// SoundPoolを生成
		this.mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		this.context = context;
		this.se_sounds = new ArrayList<Integer>();
	}
	
	public int registerSE(int resid){
		se_sounds.add(mSoundPool.load(context, resid, 1));
		return se_sounds.size() - 1;
	}
	
	public void play(int index){
		mSoundPool.play(se_sounds.get(index), 1.0f, 1.0f, 1, 0, 1.0f);
	}
}
