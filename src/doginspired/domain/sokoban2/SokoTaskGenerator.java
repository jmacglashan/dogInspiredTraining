package doginspired.domain.sokoban2;

import burlap.oomdp.core.*;
import burlap.oomdp.singleagent.common.UniformCostRF;
import doginspired.behavior.ConjunctiveGroundedPropTF;
import doginspired.behavior.TaskDescription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author James MacGlashan.
 */
public class SokoTaskGenerator {

	public static List<TaskDescription> getAllSokoTasksInState(Domain domain, State s){

		PropositionalFunction agentInRoom = domain.getPropFunction(Sokoban2Domain.PFAGENTINROOM);
		PropositionalFunction blockInRoom = domain.getPropFunction(Sokoban2Domain.PFBLOCKINROOM);

		List<TaskDescription> tasks = new ArrayList<>();

		List<GroundedProp> agentProps = agentInRoom.getAllGroundedPropsForState(s);
		for(GroundedProp gp : agentProps){
			if(!gp.isTrue(s)){
				//then it's a valid goal condition
				ConjunctiveGroundedPropTF tf = new ConjunctiveGroundedPropTF(Arrays.asList(gp));
				TaskDescription td = new TaskDescription(new UniformCostRF(), tf);
				tasks.add(td);
			}
		}

		List<GroundedProp> blockProps = blockInRoom.getAllGroundedPropsForState(s);
		for(GroundedProp gp : blockProps){
			if(!gp.isTrue(s)){
				//then it's a valid goal condition
				ConjunctiveGroundedPropTF tf = new ConjunctiveGroundedPropTF(Arrays.asList(gp));
				TaskDescription td = new TaskDescription(new UniformCostRF(), tf);
				tasks.add(td);
			}
		}

		return tasks;
	}

}
