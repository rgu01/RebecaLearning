package mdu.se.learner;

import mdu.se.rebeca.statespace.State;
import mdu.se.rebeca.statespace.Transition;

public interface Learner <T extends State<T>>{
	public Transition<T> bestAction(State<T> state);
}
