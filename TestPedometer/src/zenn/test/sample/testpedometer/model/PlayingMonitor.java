package zenn.test.sample.testpedometer.model;

import java.util.ArrayList;

import zenn.test.sample.testpedometer.io.MusicFileHandler.MusicItem;

public class PlayingMonitor {
	// 音楽データ
	private MusicItem music_item;
	// リズムのデータファイル
	private ArrayList<RithmData> rithm_data;

	// 歩数データ
	private ArrayList<WalkData> walk_data;
	// ゲーム開始時間
	private long start_time;
	// ゲーム終了時間
	private long end_time;
	// 最後のフェーズが終わった時間
	private long last_phase_end_time;
	// 現在のインデックス
	private int current_index;
	// 現在までのトータル歩数
	private long accumulated_count;
	
	public PlayingMonitor(MusicItem music_item, ArrayList<String[]> rithmData, long system_time) {
		this.music_item = music_item;
		this.current_index = 0;
		this.start_time = system_time;
		this.end_time = system_time + Long.parseLong(music_item.length) * 1000;
		this.last_phase_end_time = start_time;
		this.rithm_data = new ArrayList<PlayingMonitor.RithmData>();
		long last_phase_end_time = 0;	// 各フェーズ間の時間を求めるのに利用する
		long answer_accumulated_count = 0;		// 各フェーズ間の歩数を求めるのに利用する
		for(String[]data : rithmData){
			RithmData rithm = new RithmData();
			long next_end_time = Long.parseLong(data[0]) * 1000;
			rithm.phase_interval = next_end_time - last_phase_end_time;
			last_phase_end_time = next_end_time;
			long ans_count = Long.parseLong(data[1]);
			rithm.answer_walk_count = ans_count - answer_accumulated_count;
			answer_accumulated_count = ans_count;
			rithm.kind = data[2];
			rithm_data.add(rithm);
		}
		this.walk_data = new ArrayList<PlayingMonitor.WalkData>();
		this.accumulated_count = 0;
	}
	
	/**
	 * 次のフェーズの時間になっているかどうかを判定する。
	 * 次のフェーズになっているなら、フェーズの情報を整理する。
	 * @param system_time 現在の時間
	 * @return 次のフェーズに行ったかどうか
	 */
	public boolean judgeNext(long system_time, long walk_count){
		if(current_index < rithm_data.size()
				&& system_time > rithm_data.get(current_index).phase_interval + last_phase_end_time){
			// 最後のフェーズ終了時間を保存
			last_phase_end_time += rithm_data.get(current_index).phase_interval;
			// インデックスを更新
			current_index++;
			// 歩数ﾃﾞｰﾀを更新
			WalkData walk = new WalkData(walk_count - accumulated_count);
			walk_data.add(walk);
			accumulated_count = walk_count;
			return true;
		}
		else{
			return false;
		}
	}
	/**
	 * ゲームが終わったかの判定を行う。
	 * @param system_time
	 * @return
	 */
	public boolean judgeEnd(long system_time){
		if(current_index >= rithm_data.size() && system_time > end_time)
			return true;
		else
			return false;
	}
	
	public long getLastWalkCount(){
		if(current_index == 0)
			return 0;
		else
			return walk_data.get(current_index-1).count;
	}
	
	public int getNextPhaseInterval(){
		return (int) rithm_data.get(current_index).phase_interval / 1000;
	}
	public int getLastPhaseInterval(){
		if (current_index == 0)
			return 0;
		else
			return (int) rithm_data.get(current_index-1).phase_interval / 1000;
	}
	public long getLastAnswerCount(){
		if (current_index == 0)
			return 0;
		else
			return rithm_data.get(current_index-1).answer_walk_count; 
	}
	
	public String getResult(){
		if(walk_data.size() == rithm_data.size()){
			int point = 0;
			int answer_point = 0;
			for(int i=0;i<walk_data.size();i++){
				point += (int)rithm_data.get(i).answer_walk_count
						- (int)Math.abs(walk_data.get(i).count - rithm_data.get(i).answer_walk_count);
				answer_point += (int)rithm_data.get(i).answer_walk_count;
			}
			return judgeResult(answer_point, point)+" : "+String.valueOf(point);
		}else{
			return null;
		}
	}
	
	public String getLastPhaseResult(){
		if(current_index == 0 || current_index >= rithm_data.size())
			return null;
		else{
			int answer_count = (int) rithm_data.get(current_index - 1).answer_walk_count;
			int record_count = (int) walk_data.get(current_index - 1).count;
			int judge = judgeResult(answer_count, record_count);
			if(judge <= 2)
				return "Great";
			else if(judge <= 3)
				return "Good";
			else{
				if(record_count > answer_count)
					return "Fast";
				else {
					return "Slow";
				}
			}
		}
	}
	
	/**
	 * 答えと実績から、評価を計算して返す
	 * @return
	 */
	private int judgeResult(int answer, int record){
		double error_rate = Math.abs(1.0 - (double)record/answer);
		if (error_rate == 0)        return 0;	// S+
		else if (error_rate < 0.05) return 1;	// S
		else if (error_rate < 0.10) return 2;	// A
		else if (error_rate < 0.20) return 3;	// B
		else if (error_rate < 0.30) return 4;	// C
		else if (error_rate < 0.40) return 5;	// D
		else if (error_rate < 0.50) return 6;	// E
		else                        return 7;	// F
	}
	
	class WalkData{
		// 歩数カウント
		public long count;
		public WalkData(long count) {
			this.count = count;
		}
	}
	class RithmData{
		// 時間データ。フェーズ間の秒数を保持する（時間ではない）
		public long phase_interval;
		// 解答歩数データ
		public long answer_walk_count;
		// フェーズ種類データ
		public String kind;
	}
}
