package mdu.se.rebeca.cps;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import mdu.se.rebeca.statespace.Pair;
import mdu.se.rebeca.statespace.State;
import mdu.se.rebeca.statespace.Trace;
import mdu.se.rebeca.statespace.Transition;

public class Controller <T extends State<T>>{
	private Set<Map.Entry<State<T>,Transition<T>>> winning;
	private Set<Map.Entry<State<T>,Transition<T>>> losing;

	public Controller () {
		this.winning = new HashSet<Map.Entry<State<T>,Transition<T>>>();
		this.losing = new HashSet<Map.Entry<State<T>,Transition<T>>>();
	}
	
	public void add(Trace<T> trace) {
		for(Map.Entry<State<T>,Transition<T>> entry : trace.seq) {
			if(!this.winning.contains(entry)) {
				this.winning.add(entry);
			}
		}
	}
	
	public void prune(Trace<T> trace) {
		for(Map.Entry<State<T>,Transition<T>> entry : trace.seq) {
			if(!this.losing.contains(entry)) {
				this.losing.add(entry);
			}
		}
	}
	
	public boolean isStateCovered(State<T> state) {
		boolean result = false;
		for(Map.Entry<State<T>,Transition<T>> entry : this.winning) {
			if(entry.getKey().equals(state)) {
				result = true;
				break;
			}
		}
		return result;
	}
	
	public boolean isActionAllowed(State<T> state, Transition<T> action) {
		Map.Entry<State<T>,Transition<T>> pair = Pair.of(state, action);	
		return this.winning.contains(pair);
	}
}
