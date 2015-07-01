package doginspired.behavior;

import burlap.oomdp.core.GroundedProp;
import burlap.oomdp.core.State;
import burlap.oomdp.core.TerminalFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author James MacGlashan.
 */
public class ConjunctiveGroundedPropTF implements TerminalFunction{

	public List<GroundedProp> gps;

	public ConjunctiveGroundedPropTF(List <GroundedProp> gps){
		this.gps = gps;
	}


	@Override
	public String toString(){
		StringBuffer buf = new StringBuffer();
		buf.append(gps.get(0).toString());
		for(int i = 1; i < gps.size(); i++){
			buf.append(" ^ ").append(gps.get(i).toString());
		}
		return buf.toString();
	}


	@Override
	public boolean isTerminal(State s) {
		for(GroundedProp gp : gps){
			if(!gp.isTrue(s)){
				return false;
			}
		}

		return true;
	}

}
