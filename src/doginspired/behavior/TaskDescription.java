package doginspired.behavior;

import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.singleagent.RewardFunction;

/**
 * @author James MacGlashan.
 */

public class TaskDescription{

	public RewardFunction rf;
	public TerminalFunction tf;

	public TaskDescription(){

	}

	public TaskDescription(RewardFunction rf, TerminalFunction tf) {
		this.rf = rf;
		this.tf = tf;
	}

	@Override
	public String toString(){
		return this.tf.toString();
	}

}
