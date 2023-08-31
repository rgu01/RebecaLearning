package mdu.se.learner;

import mdu.se.rebeca.statespace.State;
import mdu.se.rebeca.statespace.Transition;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class QLearning <T extends State<T>> extends ReinforcementLearning implements Learner {
	private Set<Map.Entry<String, Integer>> policy = new HashSet<>();
}
