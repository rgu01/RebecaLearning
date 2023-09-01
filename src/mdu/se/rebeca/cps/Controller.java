package mdu.se.rebeca.cps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mdu.se.rebeca.statespace.Pair;
import mdu.se.rebeca.statespace.State;
import mdu.se.rebeca.statespace.Trace;
import mdu.se.rebeca.statespace.Transition;

public class Controller<T extends State<T>> {
	// private Set<Map.Entry<State<T>, Transition<T>>> winning;
	// private Set<Map.Entry<State<T>, Transition<T>>> losing;
	private HashMap<State<T>, List<Transition<T>>> winning;
	private HashMap<State<T>, List<Transition<T>>> losing;

	public Controller() {
		this.winning = new HashMap<State<T>, List<Transition<T>>>();
		this.losing = new HashMap<State<T>, List<Transition<T>>>();
	}
	
	public HashMap<State<T>, List<Transition<T>>> getPolicy() {
		return this.winning;
	}

	public void add(Trace<T> trace) {
		State<T> state = null;
		Transition<T> action = null;
		for (Map.Entry<State<T>, Transition<T>> entry : trace.seq) {
			state = entry.getKey();
			action = entry.getValue();
			if (this.winning.containsKey(state)) {
				if (!this.winning.get(state).contains(action)) {
					this.winning.get(state).add(action);
				}
			} else {
				List<Transition<T>> actions = new ArrayList<Transition<T>>();
				actions.add(action);
				this.winning.put(entry.getKey(), actions);
			}
		}
	}

	public void prune(Trace<T> trace) {
		State<T> state = null;
		Transition<T> action = null;
		List<Transition<T>> actions = new ArrayList<Transition<T>>();
		for (Map.Entry<State<T>, Transition<T>> entry : trace.seq) {
			state = entry.getKey();
			action = entry.getValue();
			if (this.losing.containsKey(state)) {
				if (!this.losing.get(state).contains(action)) {
					this.losing.get(state).add(action);
				}
			} else {
				actions.add(action);
				this.losing.put(entry.getKey(), actions);
			}
		}
	}

	public boolean isStateCovered(State<T> state) {
		return this.winning.containsKey(state);
	}

	public boolean isActionAllowed(State<T> state, Transition<T> action) {
		return this.winning.containsKey(state) && this.winning.get(state).contains(action);
	}

	public String toString() {
		String str = "";

		return str;
	}
}
