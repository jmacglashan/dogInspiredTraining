package doginspired.behavior;

import burlap.behavior.singleagent.Policy;
import burlap.debugtools.RandomFactory;
import burlap.oomdp.core.AbstractGroundedAction;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.State;
import burlap.oomdp.singleagent.Action;
import burlap.oomdp.singleagent.GroundedAction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author James MacGlashan.
 */
public class NoisyPolicy extends Policy{

	protected Policy srcPolicy;
	protected double noise;
	protected Domain domain;

	public NoisyPolicy(Domain domain, Policy srcPolicy, double noise){
		this.domain = domain;
		this.srcPolicy = srcPolicy;
		this.noise = noise;
	}

	@Override
	public AbstractGroundedAction getAction(State s) {

		double r = RandomFactory.getMapped(0).nextDouble();
		if(r < this.noise){
			List<GroundedAction> gas = Action.getAllApplicableGroundedActionsFromActionList(domain.getActions(), s);
			int ind = RandomFactory.getMapped(0).nextInt(gas.size());
			return gas.get(ind);
		}

		return this.srcPolicy.getAction(s);
	}

	@Override
	public List<ActionProb> getActionDistributionForState(State s) {

		List<ActionProb> aps = this.srcPolicy.getActionDistributionForState(s);
		List<GroundedAction> gas = Action.getAllApplicableGroundedActionsFromActionList(domain.getActions(), s);
		double additionalSelectionProb = this.noise / gas.size();
		if(aps.size() != gas.size()){
			//then we need to add new action probability instances
			List<ActionProb> addedProbs = new ArrayList<>();
			for(GroundedAction ga : gas){
				//find its match
				boolean foundMatch = false;
				for(ActionProb ap : aps){
					if(ap.ga.equals(ga)){
						foundMatch = true;
						ap.pSelection = ap.pSelection*(1. - this.noise) + additionalSelectionProb;
						break;
					}
				}
				if(!foundMatch){
					ActionProb ap = new ActionProb(ga, additionalSelectionProb);
					addedProbs.add(ap);
				}
			}
			for(ActionProb ap : addedProbs){
				aps.add(ap);
			}
			return aps;
		}
		else{
			for(ActionProb ap : aps){
				ap.pSelection = ap.pSelection*(1. - this.noise) + additionalSelectionProb;
			}
			return aps;
		}

	}

	@Override
	public boolean isStochastic() {
		return true;
	}

	@Override
	public boolean isDefinedFor(State s) {
		return srcPolicy.isDefinedFor(s);
	}
}
