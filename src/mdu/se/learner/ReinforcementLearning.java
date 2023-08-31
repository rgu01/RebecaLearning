package mdu.se.learner;

import mdu.se.rebeca.statespace.State;
import mdu.se.rebeca.statespace.Transition;

public class ReinforcementLearning <T extends State<T>> implements Learner<T>{
	@Override
	public Transition<T> bestAction(State<T> state) {
		int random = (int)(Math.random() * (state.getOutgoing().size() - 1));
		Object action = state.getOutgoing().toArray()[random];
		if(action instanceof Transition) {
			return (Transition<T>)action;
		}
		return null;
	}

}
