package zenn.test.sample.testpedometer.model;

import java.util.ArrayList;

import zenn.test.sample.testpedometer.exception.MusicNotFinishedYetException;

public class MedleyMonitor extends ArrayList<PlayingMonitor>{
	
	public String getResult() throws MusicNotFinishedYetException{
		int point = 0;
		int answer_point = 0;
		for(PlayingMonitor monitor : this){
			point += monitor.getResultPoint();
			answer_point += monitor.getAnswerPoint();
		}
		String result = String.valueOf(PlayingMonitor.judgeResult(answer_point, point));
		return "ランク：" + result +" 総歩数：" + point + " アンサー歩数："+answer_point;
	}
}
