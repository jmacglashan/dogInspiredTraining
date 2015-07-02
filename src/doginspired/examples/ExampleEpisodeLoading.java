package doginspired.examples;

import burlap.behavior.singleagent.EpisodeAnalysis;
import burlap.behavior.singleagent.EpisodeSequenceVisualizer;
import burlap.oomdp.auxiliary.StateParser;
import burlap.oomdp.auxiliary.common.StateYAMLParser;
import burlap.oomdp.core.Domain;
import burlap.oomdp.singleagent.common.NullAction;
import burlap.oomdp.visualizer.Visualizer;
import doginspired.domain.sokoban2.Sokoban2Domain;
import doginspired.domain.sokoban2.Sokoban2Visualizer;

import java.util.List;

/**
 * @author James MacGlashan.
 */
public class ExampleEpisodeLoading {

	public static void main(String[] args) {
		Sokoban2Domain soko = new Sokoban2Domain();
		soko.includeDirectionAttribute(true);
		soko.includePullAction(true);

		Domain domain = soko.generateDomain();
		//add a noop action
		domain.addAction(new NullAction("noop", domain, ""));

		//get our visualizer
		Visualizer v = Sokoban2Visualizer.getVisualizer(Sokoban2Domain.ROBOTIMAGESPATH);

		//create a state parser for file loading
		StateParser sp = new StateYAMLParser(domain);

		//load our episodes
		List<EpisodeAnalysis> episodes = EpisodeAnalysis.parseFilesIntoEAList("data/amt_soko_logs_3/1552ac1fd34fdb_1", domain, sp);

		System.out.println("size: " + episodes.size());

		//launch an episode sequence visualizer
		EpisodeSequenceVisualizer evis = new EpisodeSequenceVisualizer(v, domain, episodes);
	}

}
