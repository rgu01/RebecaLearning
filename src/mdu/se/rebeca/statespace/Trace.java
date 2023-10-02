package mdu.se.rebeca.statespace;

import java.util.ArrayList;
import java.util.List;

public class Trace <T extends State<T>> {
	public List<Pair<T>> seq;
	
	public Trace() {
		this.seq = new ArrayList<Pair<T>> ();
	}
	
	public void add(State<T> state, Transition<T> action) {
		this.seq.add(new Pair<T>(state, action));
	}
	
	public boolean contains(State<T> state, Transition<T> action) {
		for(Pair<T> entry : this.seq) {
			if(entry.getKey().equals(state) && entry.getValue().equals(action)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean contains(State<T> state) {
		for(Pair<T> entry : this.seq) {
			if(entry.getKey().equals(state)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isReachable(String pro) {
		for(Pair<T> entry : this.seq) {
			if(entry.getKey().getAtomicPropositions().contains(pro)) {
				return true;
			}
		}
		return false;
	}
	
	public int distance(Trace<T> other) {
		return 0;
	}
	
	public Trace<T> clone() {
		Trace<T> copy = new Trace<T>();
		for(Pair<T> entry : this.seq) {
			copy.add(entry.getKey(), entry.getValue());
		}
		return copy;
		
	}
}