package mdu.se.rebeca.explorer;
import java.util.Set;

import mdu.se.learner.QLearning;
import mdu.se.learner.ReinforcementLearning;
import mdu.se.rebeca.cps.Controller;
import mdu.se.rebeca.statespace.State;
import mdu.se.rebeca.statespace.StateSpace;
import mdu.se.rebeca.statespace.StateWrapper;
import mdu.se.rebeca.statespace.Trace;
import mdu.se.rebeca.statespace.Transition;
import mdu.se.rebeca.xmlparser.*;

public class StatespaceExplorer <T extends State<T>>{
	private Controller<T> controller;
	private ReinforcementLearning<T> learner;
	
	public StatespaceExplorer() {
		controller = new Controller<T>();
		learner = new QLearning<T>();
	}
	
	public void guess(State<T> source, Trace<T> trace) {
		boolean exhaustive = true;
		Set<String> pros = source.getAtomicPropositions();
		if(pros.contains("win")) {
			//learn and add
			controller.add(trace);
			return;
		}
		if(pros.contains("collision") || trace.contains(source) || source.getOutgoing().isEmpty()) {
			//learn and prune
			controller.prune(trace);
			return;
		}
		if(pros.contains("gameover")) {
			//for test
			//controller.add(trace);
			return;
		}
		for(Transition<T> action : source.getOutgoing()) {
			//when action.getAction() == null, it is a delay
			if(action.getAction() != null && action.getAction().equals("ARRIVE")) {
				exhaustive = false;
				break;
			}
		}
		if(exhaustive) {
			//system's actions
			for(Transition<T> next : source.getOutgoing()) {
				trace.add(source, next);
				this.guess(next.getDestination(), trace);
			}
		} else {
			//environment's actions
			Transition<T> best = this.learner.bestAction(source);
			trace.add(source, best);
			this.guess(best.getDestination(), trace);
		}
	}
	
	public boolean checkSafety(State<T> source, Trace<T> trace) {
		boolean environment = false, pass = false;
		Set<String> pros = source.getAtomicPropositions();
		if(pros.contains("win")) {
			return true;
		}
		if(pros.contains("collision") || trace.contains(source) || source.getOutgoing().isEmpty()) {
			controller.prune(trace);
			return false;
		}
		for(Transition<T> action : source.getOutgoing()) {
			//when action.getAction() == null, it is a delay
			if(action.getAction() != null && action.getAction().equals("ARRIVE")) {
				environment = true;
				break;
			}
		}
		if(!environment) {
			//system's actions
			for(Transition<T> next : source.getOutgoing()) {
				trace.add(source, next);
				this.guess(next.getDestination(), trace);
			}
		} else {
			//environment's actions
			pass = true;
			for(Transition<T> next : source.getOutgoing()) {
				trace.add(source, next);
				pass = pass && this.checkSafety(next.getDestination(), trace);
				if(!pass) {
					break;
				}
			}
		}
		return pass;
	}
	
	public void checkSecurity(State<T> source, Trace<T> trace) {
		
	}
	
	public static void main(String arg[]) {
		StateSpace<StateWrapper> statespace = StatespaceParser.parse("statespace/RobotEasy.statespace");
		StatespaceExplorer<StateWrapper> explorer = new StatespaceExplorer<StateWrapper>();
		Trace<StateWrapper> trace = new Trace<StateWrapper>();
		explorer.guess(statespace.getInitialState(), trace);
		explorer.checkSafety(statespace.getInitialState(), trace);
		explorer.checkSecurity(statespace.getInitialState(), trace);
	}
}
