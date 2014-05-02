package net.sample14_01_01_00;

import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.util.Log;

public class MyRenderer implements GLSurfaceView.Renderer {
	
	private static final int GAME_INTERVAL = 60;//制限時間は60秒
	
	private long mStartTime;//開始時間
	private boolean mGameOverFlag;//ゲームオーバーであるか
	
	private Handler mHandler = new Handler();//ハンドラー
	
	// コンテキスト
	private Context mContext;
	
	// 効果音
	private MySe mSe;
	
	public MyRenderer(Context context) {
		this.mContext = context;
		this.mSe = new MySe(context);
		startNewGame();
	}
	
	public void startNewGame() {
		this.mStartTime = System.currentTimeMillis();//開始時間を保持します
		
	}
	
	//描画を行う部分を記述するメソッドを追加する
	public void renderMain(GL10 gl) {
		// 経過時間を計算する
		int passedTime = (int) (System.currentTimeMillis() - mStartTime) / 1000;
		int remainTime = GAME_INTERVAL - passedTime;// 　残り時間を計算する
		if (remainTime <= 0) {
			remainTime = 0;// 残り時間がマイナスにならないようにする
			if (!mGameOverFlag) {
				mGameOverFlag = true;// ゲームオーバー状態にする
				// Global.mainActivity.showRetryButton()をUIスレッド上で実行する
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						Global.mainActivity.showRetryButton();
					}
				});
			}
		}
		Random rand = Global.rand;

	 
		// 背景を描画する
//		GraphicUtil.drawTexture(gl, 0.0f, 0.0f, 2.0f, 3.0f, mBgTexture, 1.0f, 1.0f, 1.0f, 1.0f);
	 
	}
	
	
//	//画面がタッチされたときに呼ばれるメソッド
//	public void touched(float x, float y) {
//		Log.i(getClass().toString(), String.format("touched! x = %f, y = %f", x, y));
//		MyTarget[] targets = mTargets;
//		if (!mGameOverFlag) {
//			// すべての標的との当たり判定をします
//			for (int i = 0; i < TARGET_NUM; i++) {
//				if (targets[i].isPointInside(x, y)) {
//					// 標的をランダムな位置に移動します
//					float dist = 2.0f;// 画面中央から2.0fはなれた円周上の点
//					float theta = (float) Global.rand.nextInt(360) / 180.0f * (float) Math.PI;// 適当な位置
//					targets[i].mX = (float) Math.cos(theta) * dist;
//					targets[i].mY = (float) Math.sin(theta) * dist;
//					mScore += 100;// 100点加算します
//					Log.i(getClass().toString(), "score = " + mScore);
//					mSe.playHitSound();
//				}
//			}
//		}
//	}

	public void subtractPausedTime(long pausedTime) {
		mStartTime += pausedTime;
	}
	
	public long getStartTime() {
		return mStartTime;
	}
	 

}
