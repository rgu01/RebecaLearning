package mdu.se.rebeca.statespace;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Trace <T extends State<T>> {
	public List<Map.Entry<State<T>,Transition<T>>> seq;
	
	public Trace() {
		this.seq = new ArrayList<Map.Entry<State<T>,Transition<T>>> ();
	}
	
	public void add(State<T> state, Transition<T> action) {
		this.seq.add(Pair.of(state, action));
	}
	
	public boolean contains(State<T> state, Transition<T> action) {
		for(Map.Entry<State<T>,Transition<T>> entry : this.seq) {
			if(entry.getKey().equals(state) && entry.getValue().equals(action)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean contains(State<T> state) {
		for(Map.Entry<State<T>,Transition<T>> entry : this.seq) {
			if(entry.getKey().equals(state)) {
				return true;
			}
		}
		return false;
	}
}