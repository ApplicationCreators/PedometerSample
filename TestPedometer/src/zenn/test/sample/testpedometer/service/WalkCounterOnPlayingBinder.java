package zenn.test.sample.testpedometer.service;

import android.os.Binder;

public class WalkCounterOnPlayingBinder extends Binder {

	WalkCounterOnPlayingService s;
	public WalkCounterOnPlayingBinder(WalkCounterOnPlayingService s) {
		this.s=s;
	}

	public WalkCounterOnPlayingService getService() {
		return s;
	}
}
