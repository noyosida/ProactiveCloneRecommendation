package immediateCloneDetection;

import java.util.TimerTask;

import immediateCloneDetection.handlers.SampleHandler;

public class ExcuteCCFX extends TimerTask {

	@Override
	public void run() {
		// TODO 自動生成されたメソッド・スタブ
		SampleHandler.runCCFX();
		SampleHandler.timerFlag = 0;
	}

}
