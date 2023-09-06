package mdu.se.rebeca.explorer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mdu.se.learner.QLearning;
import mdu.se.learner.ReinforcementLearning;
import mdu.se.rebeca.cps.Controller;
import mdu.se.rebeca.statespace.State;
import mdu.se.rebeca.statespace.StateSpace;
import mdu.se.rebeca.statespace.StateWrapper;
import mdu.se.rebeca.statespace.Trace;
import mdu.se.rebeca.statespace.Transition;
import mdu.se.rebeca.xml.*;

public class StatespaceExplorer<T extends State<T>> {
	private Controller<T> controller;
	private ReinforcementLearning<T> learner;
	List<State<T>> statesGuess = new ArrayList<State<T>>();
	List<Transition<T>> transitionsGuess = new ArrayList<Transition<T>>();
	List<State<T>> statesSafety = new ArrayList<State<T>>();
	List<Transition<T>> transitionsSafety = new ArrayList<Transition<T>>();

	public StatespaceExplorer() {
		controller = new Controller<T>();
		learner = new QLearning<T>();
	}

	public void guess(State<T> source, Trace<T> trace) {
		boolean exhaustive = true;
		Set<String> pros = source.getAtomicPropositions();
		
		this.statesGuess.add(source);
		if (pros.contains("win")) {
			// learn and add
			trace.add(source, null);
			controller.add(trace);
			return;
		}
		if (pros.contains("collision") || trace.contains(source) || source.getOutgoing().isEmpty()) {
			// learn and prune
			trace.add(source, null);
			controller.prune(trace);
			return;
		}
		if (pros.contains("gameover")) {
			// for test
			// controller.add(trace);
			// return;
		}
		for (Transition<T> action : source.getOutgoing()) {
			// when action.getAction() == null, it is a delay
			if (action.getAction() != null && action.getAction().equals("ARRIVE")) {
				exhaustive = false;
				break;
			}
			if (action.getAction() == null) {
				break;
			}
		}
		if (exhaustive) {
			// system's actions
			for (Transition<T> next : source.getOutgoing()) {
				this.transitionsGuess.add(next);
				Trace forGuess = trace.clone();
				forGuess.add(source, next);
				this.guess(next.getDestination(), forGuess);
			}
		} else {
			// environment's actions
			Transition<T> best = this.learner.bestAction(source);
			this.transitionsGuess.add(best);
			Trace forGuess = trace.clone();
			forGuess.add(source, best);
			this.guess(best.getDestination(), forGuess);
		}
	}

	public boolean checkSafety(State<T> source, Trace<T> trace) {
		boolean environment = false, pass = false;
		Set<String> pros = source.getAtomicPropositions();
		
		this.statesSafety.add(source);
		if (pros.contains("win")) {
			trace.add(source, null);
			return true;
		}
		if (pros.contains("collision") || trace.contains(source) || source.getOutgoing().isEmpty()) {
			trace.add(source, null);
			controller.prune(trace);
			return false;
		}
		for (Transition<T> action : source.getOutgoing()) {
			// when action.getAction() == null, it is a delay
			if (action.getAction() != null && action.getAction().equals("ARRIVE")) {
				environment = true;
				break;
			}
		}
		if (!environment) {
			// system's actions
			// guess again if needed
			if (!this.controller.isStateCovered(source)) {
				for (Transition<T> next : source.getOutgoing()) {
					this.transitionsSafety.add(next);
					Trace forGuess = trace.clone();
					forGuess.add(source, next);
					this.guess(next.getDestination(), forGuess);
				}
			}
			// check the controller's actions
			for (Transition<T> next : source.getOutgoing()) {
				if (this.controller.isActionAllowed(source, next)) {
					this.transitionsSafety.add(next);
					Trace forCheck = trace.clone();
					forCheck.add(source, next);
					pass = pass || this.checkSafety(next.getDestination(), forCheck);
				}
			}
		} else {
			// environment's actions
			pass = true;
			for (Transition<T> next : source.getOutgoing()) {
				this.transitionsSafety.add(next);
				Trace forCheck = trace.clone();
				forCheck.add(source, next);
				pass = pass && this.checkSafety(next.getDestination(), forCheck);
				if (!pass) {
					break;
				}
			}
		}
		return pass;
	}

	private void exploreByController(State<T> source, Trace<T> trace, Set<Trace> traces) {
		boolean environment = false;
		// Set<String> pros = source.getAtomicPropositions();

		// exploration
		for (Transition<T> action : source.getOutgoing()) {
			// when action.getAction() == null, it is a delay
			if (action.getAction() != null && action.getAction().equals("ARRIVE")) {
				environment = true;
				break;
			}
		}
		if (!environment) {
			// system's actions
			if (this.controller.isStateCovered(source)) {
				if (this.controller.isLeaf(source)) {
					trace.add(source, null);
					traces.add(trace);
				} else {
					for (Transition<T> next : source.getOutgoing()) {
						if (this.controller.isActionAllowed(source, next)) {
							Trace<T> forExplore = trace.clone();
							forExplore.add(source, next);
							this.exploreByController(next.getDestination(), forExplore, traces);
						}
					}
				}
			} else {
				boolean temp = this.controller.shouldPrune(source);
				//states that are uncoverred by the controller is not explored.
				for (Transition<T> next : source.getOutgoing()) {
					Trace forGuess = trace.clone();
					forGuess.add(source, next);
					this.guess(next.getDestination(), forGuess);
				}
			}
		} else {
			// environment's actions
			if (source.getOutgoing() == null || source.getOutgoing().size() == 0) {
				trace.add(source, null);
				traces.add(trace);
			} else {
				for (Transition<T> next : source.getOutgoing()) {
					Trace<T> forExplore = trace.clone();
					forExplore.add(source, next);
					this.exploreByController(next.getDestination(), forExplore, traces);
				}
			}
		}
		return;
	}

	public void checkSecurity(State<T> source, String secret, String common) {
		boolean environment = false, pass = false;
		Set<String> pros = source.getAtomicPropositions();
		Set<Trace> traces = new HashSet<Trace>();
		// exploration
		this.exploreByController(source, new Trace(), traces);
		// check
		for (Trace t1 : traces) {
			if (t1.isReachable(secret)) {
				for (Trace t2 : traces) {
					if (!t1.equals(t2) && t2.isReachable(common) && t1.distance(t2) <= 0) {
						this.controller.addSecurity(t1);
					}
				}
			}
		}
		this.controller.replaceSecure();
	}

	public static void main(String arg[]) {
		StateSpaceProcessor<StateWrapper> processor = new StateSpaceProcessor<StateWrapper>();
		StateSpace<StateWrapper> statespace = processor.parse("statespace/Robot.statespace");
		StatespaceExplorer<StateWrapper> explorer = new StatespaceExplorer<StateWrapper>();
		Trace<StateWrapper> trace = new Trace<StateWrapper>();
		explorer.guess(statespace.getInitialState(), trace);
		processor.writePolicy(explorer.controller, "statespace/policyGuess.statespace");
		System.out.println("Phase 1");
		System.out.println("number of states guessed: " + explorer.statesGuess.size() + ", number of transitions guessed: " + explorer.transitionsGuess.size());
		
		explorer.statesGuess.clear();
		explorer.transitionsGuess.clear();
		trace = new Trace<StateWrapper>();
		explorer.checkSafety(statespace.getInitialState(), trace);
		processor.writePolicy(explorer.controller, "statespace/policySafety.statespace");
		System.out.println("Phase 2");
		System.out.println("number of states guessed: " + explorer.statesGuess.size() + ", number of transitions guessed: " + explorer.transitionsGuess.size());
		System.out.println("number of states checked: " + explorer.statesSafety.size() + ", number of transitions checked: " + explorer.transitionsSafety.size());
		
		
		trace = new Trace<StateWrapper>();
		explorer.checkSecurity(statespace.getInitialState(), "justT1", "justT2");
		processor.writePolicy(explorer.controller, "statespace/policySecure.statespace");
		System.out.println("Phase 3");
		
		
		System.out.println("done");
	}
}
