package mdu.se.rebeca.cps;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
}
