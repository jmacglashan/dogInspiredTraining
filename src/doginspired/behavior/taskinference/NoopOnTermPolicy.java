package doginspired.behavior.taskinference;

import burlap.behavior.singleagent.Policy;
import burlap.oomdp.core.AbstractGroundedAction;
import burlap.oomdp.core.State;
import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.singleagent.Action;
import burlap.oomdp.singleagent.GroundedAction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author James MacGlashan.
 */
public class NoopOnTermPolicy extends Policy {

	GroundedAction noopAction;
	TerminalFunction tf;
	Policy srcPolicy;

	public NoopOnTermPolicy(Action noop, TerminalFunction tf, Policy srcPolicy) {
		if(noop != null) {
			this.noopAction = new GroundedAction(noop, "");
		}
		this.tf = tf;
		this.srcPolicy = srcPolicy;
	}

	@Override
	public AbstractGroundedAction getAction(State s) {

		if(this.noopAction != null && this.tf.isTerminal(s)) {
			return this.noopAction;
		}

		return this.srcPolicy.getAction(s);
	}

	@Override
	public List<ActionProb> getActionDistributionForState(State s) {

		if(this.noopAction != null && this.tf.isTerminal(s)) {
			List<ActionProb> aps = new ArrayList<ActionProb>(1);
			aps.add(new ActionProb(noopAction, 1.));
			return aps;
		}

		return this.srcPolicy.getActionDistributionForState(s);
	}

	@Override
	public boolean isStochastic() {
		return this.srcPolicy.isStochastic();
	}

	@Override
	public boolean isDefinedFor(State s) {
		if(this.noopAction != null && this.tf.isTerminal(s)) {
			return true;
		}
		return this.srcPolicy.isDefinedFor(s);
	}

}
