package doginspired.behavior.taskinference.irl.discretebayesian;

import burlap.behavior.singleagent.Policy;
import burlap.behavior.statehashing.StateHashFactory;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.State;
import burlap.oomdp.singleagent.Action;
import burlap.oomdp.singleagent.GroundedAction;
import doginspired.behavior.NoisyPolicy;
import doginspired.behavior.PolicyGenerator;
import doginspired.behavior.TaskDescription;
import doginspired.behavior.taskinference.NoopOnTermPolicy;
import doginspired.behavior.taskinference.TaskPolicyProbTuple;

import java.util.ArrayList;
import java.util.List;

/**
 * @author James MacGlashan.
 */
public class DiscreteBayesianIRL {

	protected Domain planningDomain;
	protected List<TaskPolicyProbTuple> taskDistribution;
	protected PolicyGenerator policyGenerator;
	protected Action noopAction = null;
	protected StateHashFactory hashingFactory;
	protected double policyNoise = 0.1;


	public DiscreteBayesianIRL(Domain domain, List <TaskDescription> tasks, PolicyGenerator policyGenerator, StateHashFactory hashingFactory){
		this.planningDomain = domain;
		double uni = 1. / tasks.size();
		this.taskDistribution = new ArrayList<>(tasks.size());
		for(TaskDescription td : tasks){
			this.taskDistribution.add(new TaskPolicyProbTuple(td, uni));
		}
		this.hashingFactory = hashingFactory;

	}

	public void setPolicyNoise(double policyNoise) {
		this.policyNoise = policyNoise;
	}

	public void setNoopAction(Action noopAction){
		this.noopAction = noopAction;
	}

	public List<TaskPolicyProbTuple> getTaskDistribution(){
		return this.taskDistribution;
	}

	public TaskPolicyProbTuple getMostLikelyTask(){
		double maxP = Double.NEGATIVE_INFINITY;
		TaskPolicyProbTuple max = null;
		for(TaskPolicyProbTuple task : this.taskDistribution){
			if(task.getProb() > maxP){
				maxP = task.getProb();
				max = task;
			}
		}
		return max;
	}

	public List<TaskPolicyProbTuple> computePosteriorFromInteraction(State s, GroundedAction ga){

		double normalization = 0.;
		double [] proportionalPosterior = new double[this.taskDistribution.size()];
		for(int i = 0; i < this.taskDistribution.size(); i++){
			TaskPolicyProbTuple tp = this.taskDistribution.get(i);
			double l = this.likelihood(s, ga, tp);
			proportionalPosterior[i] = l * tp.getProb();
			normalization += proportionalPosterior[i];
		}

		List<TaskPolicyProbTuple> posterior = new ArrayList<>(this.taskDistribution.size());
		for(int i = 0; i < this.taskDistribution.size(); i++) {
			TaskPolicyProbTuple tp = this.taskDistribution.get(i);
			posterior.add(new TaskPolicyProbTuple(tp.getTask(), tp.getPolicy(), tp.getProb()));
		}

		return posterior;
	}

	public List<TaskPolicyProbTuple> computeAndUpdatePosteriorFromInteraction(State s, GroundedAction ga){
		this.taskDistribution = this.computePosteriorFromInteraction(s, ga);
		return this.taskDistribution;
	}

	public double likelihood(State s, GroundedAction ga, TaskPolicyProbTuple tp){

		//set the policy if it is not already set
		if(tp.getPolicy() == null){
			Policy sourcePolicy = this.policyGenerator.getPolicy(this.planningDomain, s, tp.getTask().rf, tp.getTask().tf, this.hashingFactory);
			if(this.noopAction != null){
				sourcePolicy = new NoopOnTermPolicy(this.noopAction, tp.getTask().tf, sourcePolicy);
			}
			Policy noisyPolicy = new NoisyPolicy(this.planningDomain, sourcePolicy, this.policyNoise);
			tp.setPolicy(noisyPolicy);
		}

		return tp.getPolicy().getProbOfAction(s, ga);

	}


}
