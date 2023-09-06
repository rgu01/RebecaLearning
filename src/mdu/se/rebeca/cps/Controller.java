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
	private HashMap<State<T>, List<Transition<T>>> secure;

	public Controller() {
		this.winning = new HashMap<State<T>, List<Transition<T>>>();
		this.losing = new HashMap<State<T>, List<Transition<T>>>();
		this.secure = new HashMap<State<T>, List<Transition<T>>>();
	}
	
	public void replaceSecure() {
		this.winning = null;
		this.winning = (HashMap<State<T>, List<Transition<T>>>) secure.clone();
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
	
	public void addSecurity(Trace<T> trace) {
		State<T> state = null;
		Transition<T> action = null;
		for (Map.Entry<State<T>, Transition<T>> entry : trace.seq) {
			state = entry.getKey();
			action = entry.getValue();
			if (this.secure.containsKey(state)) {
				if (!this.secure.get(state).contains(action)) {
					this.secure.get(state).add(action);
				}
			} else {
				List<Transition<T>> actions = new ArrayList<Transition<T>>();
				actions.add(action);
				this.secure.put(entry.getKey(), actions);
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
	
	public boolean shouldPrune(State<T> state) {
		return this.losing.containsKey(state);
	}
	
	public boolean isLeaf(State<T> state) {
		return this.winning.get(state).size() == 1 && this.winning.get(state).get(0) == null;
	}

	public boolean isActionAllowed(State<T> state, Transition<T> action) {
		boolean result = false;
		
		if(this.winning.containsKey(state) && this.winning.get(state).contains(action)) {
			//boolean temp1 = this.losing.containsKey(state);
			//boolean temp2 = this.losing.get(state).contains(action);
			//if(!this.losing.containsKey(state) || !this.losing.get(state).contains(action)) {
			//	result = true;
			//}
			result = true;
		}
		
		return result;
	}

	public String toString() {
		String str = "";

		return str;
	}
}
