package doginspired.behavior.taskinference;

import burlap.behavior.singleagent.Policy;
import doginspired.behavior.TaskDescription;

/**
 * @author James MacGlashan.
 */
public class TaskPolicyProbTuple {

	public TaskDescription task;
	public Policy policy;
	public double prob;


	public TaskPolicyProbTuple(TaskDescription task, double prob) {
		this.task = task;
		this.prob = prob;
	}

	public TaskPolicyProbTuple(TaskDescription task, Policy policy, double prob) {
		this.task = task;
		this.policy = policy;
		this.prob = prob;
	}

	public TaskDescription getTask() {
		return task;
	}

	public void setTask(TaskDescription task) {
		this.task = task;
	}

	public double getProb() {
		return prob;
	}

	public void setProb(double prob) {
		this.prob = prob;
	}

	public Policy getPolicy() {
		return policy;
	}

	public void setPolicy(Policy policy) {
		this.policy = policy;
	}

	@Override
	public String toString() {
		return this.getProb() + ": " + this.task.toString();
	}
}
