package zenn.test.sample.testpedometer.service;

import android.os.Binder;

public class WalkCounterBinder extends Binder {

	WalkCounterService s;
	public WalkCounterBinder(WalkCounterService s) {
		this.s=s;
	}

	public WalkCounterService getService() {
		return s;
	}
}
