# dogInspiredTraining

You can compile this project using ant; simply type `ant` from the command line in the repo's directory. However, it is strongly recommened that you use an IDE like IntelliJ. 

There are two example pieces of code to test. The first (src/doginspired/examples/ExampleIRL.java) demonstrates how to use the discrete Bayesian IRL system to get the reward function posterior conditioned on a state-action pair (or a history of state-action pairs). To run it from the command line, use:

`java -cp lib/*:build doginspired.examples.ExampleIRL`

This code creates a simple example trajectory in our clean up world domain, and reasons over four possible reward functions. To view the trajectory it's reasoning over, uncomment the line `visualizeEpisodes(domain, Arrays.asList(ea));` in the main method. The four reard functions correspond to the agent going to one of two rooms or the chair being taken to one of two rooms. Given the example trajectory, the code will report the new reward posterior after each state-action pair in the sequence.

For the State Informativeness analsyis, we have a choice to condition the prior on the history or always use a flat prior. By default this code conditions on the whole history. However, you can force it to always use the flat prior for each time step by not updating the prior to the new posterior, which can be indcated by changing the true flag in the line `iterativeEpisodeInferenceWithHistory(domain, ea, true);` to false.

I have also included example code for loading episodes from files saved to disk, such as the ones from our AMT experiment. That example code is in src/doginspired/examples/ExampleEpisodeLoading.java. To run it from the command line use:

`java -cp lib/*:build doginspired.examples.ExampleEpisodeLoading`

Included in the repo is at least some version of the AMT data that we have, and that demonstration code will load one of the sessions. I'm not sure if it's the latest. However, the point is merely to show how to load episodes into BURLAP and the data can always be replaced with something new.

Hopefully this code will illustrate the main necessary pieces to put together the state informative analysis, without having to know the inner workings of how the probabilities are computed and how planning is performed. However, it might be worth looking deeper in the referenced code to see how it works.
