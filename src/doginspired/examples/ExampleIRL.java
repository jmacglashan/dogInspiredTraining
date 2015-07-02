package doginspired.examples;

import burlap.behavior.singleagent.EpisodeAnalysis;
import burlap.behavior.singleagent.EpisodeSequenceVisualizer;
import burlap.behavior.singleagent.Policy;
import burlap.behavior.statehashing.NameDependentStateHashFactory;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.GroundedProp;
import burlap.oomdp.core.PropositionalFunction;
import burlap.oomdp.core.State;
import burlap.oomdp.singleagent.common.NullAction;
import burlap.oomdp.singleagent.common.UniformCostRF;
import burlap.oomdp.visualizer.Visualizer;
import doginspired.behavior.ConjunctiveGroundedPropTF;
import doginspired.behavior.TaskDescription;
import doginspired.behavior.taskinference.TaskPolicyProbTuple;
import doginspired.behavior.taskinference.irl.discretebayesian.DiscreteBayesianIRL;
import doginspired.domain.sokoban2.SokoTaskGenerator;
import doginspired.domain.sokoban2.Sokoban2Domain;
import doginspired.behavior.sokoamdp.SokoAMDPPlannerPolicyGen;
import doginspired.domain.sokoban2.Sokoban2Visualizer;

import java.util.Arrays;
import java.util.List;

/**
 * @author James MacGlashan.
 */
public class ExampleIRL {

	public static EpisodeAnalysis generateExampleEpisode(Domain domain){

		//get a classic sokoban state
		State s = Sokoban2Domain.getClassicState(domain);

		//set up goal condition for when the block0 is in room1 (the red basket in the green room)
		PropositionalFunction blockInRoom = domain.getPropFunction(Sokoban2Domain.PFBLOCKINROOM);
		GroundedProp gp = new GroundedProp(blockInRoom, new String[]{"block0", "room1"});
		ConjunctiveGroundedPropTF tf = new ConjunctiveGroundedPropTF(Arrays.asList(gp));

		//create a policy that does fast dynamic planning for the Sokoban domain
		SokoAMDPPlannerPolicyGen pgen = new SokoAMDPPlannerPolicyGen();
		Policy p = pgen.getPolicy(domain, s, new UniformCostRF(), tf, new NameDependentStateHashFactory());

		//evaluate the resulting policy
		EpisodeAnalysis ea = p.evaluateBehavior(s, new UniformCostRF(), tf);

		//return the episode it generated
		return ea;
	}

	public static void visualizeEpisodes(Domain domain, List<EpisodeAnalysis> episodes){
		Visualizer v = Sokoban2Visualizer.getVisualizer(Sokoban2Domain.ROBOTIMAGESPATH);
		EpisodeSequenceVisualizer evis = new EpisodeSequenceVisualizer(v, domain, episodes);
	}


	/**
	 * Walks through an episode and prints the posterior for the possibles tasks given the observed state-action choice.
	 * If useHistory=true then the priors are updated after each observation. If useHistory=false, then
	 * a flat prior on tasks is used to evaluated the effect of each state-action pair in the episode indepdent
	 * of its history.
	 * @param domain
	 * @param ea
	 * @param useHistory
	 */
	public static void iterativeEpisodeInferenceWithHistory(Domain domain, EpisodeAnalysis ea, boolean useHistory){

		//first get the set of tasks over which we will do inference
		List<TaskDescription> tasks = SokoTaskGenerator.getAllSokoTasksInState(domain, ea.getState(0));

		//set up a fast planning algorithm for the sokoban domain for policy generation
		SokoAMDPPlannerPolicyGen pgen = new SokoAMDPPlannerPolicyGen();

		//set up our discrete bayesian IRL for inference
		DiscreteBayesianIRL irl = new DiscreteBayesianIRL(domain, tasks, pgen, new NameDependentStateHashFactory());
		//tell irl about the do nothing (e.g., sit) action
		irl.setNoopAction(domain.getAction("noop"));

		System.out.println("Before any inference, distribution starts at:");
		printTaskDistribution(irl.getTaskDistribution());


		//now iterate through each step of our episode and look at the updated distribution after observing each step
		for(int t = 0; t < ea.maxTimeStep(); t++){
			System.out.println("After step " + t + " (action " + ea.getAction(t).toString() + ")");
			if(useHistory) {
				printTaskDistribution(irl.computeAndUpdatePosteriorFromInteraction(ea.getState(t), ea.getAction(t)));
			}
			else{
				printTaskDistribution(irl.computePosteriorFromInteraction(ea.getState(t), ea.getAction(t)));
			}
		}


	}


	public static void printTaskDistribution(List<TaskPolicyProbTuple> taskDistribution){
		for(TaskPolicyProbTuple tp : taskDistribution){
			System.out.println(tp.toString());
		}
		System.out.println("----");
	}

	public static void main(String[] args) {
		Sokoban2Domain soko = new Sokoban2Domain();
		soko.includePullAction(true);
		soko.includeDirectionAttribute(true);

		Domain domain = soko.generateDomain();
		//add a no operation action to our domain for the action the agent would select when finished
		domain.addAction(new NullAction("noop", domain, ""));

		EpisodeAnalysis ea = generateExampleEpisode(domain);
		//visualizeEpisodes(domain, Arrays.asList(ea));

		iterativeEpisodeInferenceWithHistory(domain, ea, true);





	}

}
