package com.m_hi.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class GraphView extends View {

	double div =0;
	int counter = 0;
	int[] counterhistory = new int[1000];

	public GraphView(Context context) {
		super(context);
	}

	public GraphView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public GraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void reset(){
		div = 0;
		counter = 0;
		for(int i=0;i<counterhistory.length;i++){
			counterhistory[i] = 0;
		}
	}

	public void setDiv(double d){
		this.div = d;
//		this.counter++;
		int diff = 4;
		this.counter += diff;
		if (counterhistory.length <= counter){
			counter = 0;
		}
		else{
			int last = counterhistory[counter - diff];
			double up = (d * 10 - last) / diff;
			for(int i=1;i<=diff;i++){
				counterhistory[counter-diff+i] = (int)(last + up * i);
			}
			counterhistory[counter] = (int)d * 10;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// 座標グリッドの生成
		int width = this.getWidth();
		int height = this.getHeight();
		int base = height/2;
		Paint paint = new Paint();
		paint.setColor(Color.argb(75, 255, 255, 255));
		paint.setStrokeWidth(1);
		for (int y = 0; y < height; y = y + 10) {
			canvas.drawLine(0, y, width, y, paint);
		}
		for (int x = 0; x < width; x = x + 10) {
			canvas.drawLine(x, 0, x, height, paint);
		}
		paint.setColor(Color.RED);
		canvas.drawLine(0, base, width, base, paint);


		// グラフの生成
		paint.setColor(Color.YELLOW);
		paint.setStrokeWidth(2);
		for ( int i = 0; i < counterhistory.length - 1 ; i++ ){
			canvas.drawLine(i, base + counterhistory[i], i + 1, base + counterhistory[i + 1], paint);
		}
		
		paint.setColor(Color.RED);
		canvas.drawLine(counter, 0, counter, height, paint);

//		Log.i("GraphView","counter:"+counter);
	}
	
	
}
