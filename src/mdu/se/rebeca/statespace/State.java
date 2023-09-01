package mdu.se.rebeca.statespace;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class State <T extends State<T>> {
	protected String id;
	protected int time;
	protected Set<Transition<T>> incomming = new HashSet<Transition<T>>(), outgoing = new HashSet<Transition<T>>();
	protected Set<String> atomicPropositions = new HashSet<String>();
	protected List<String> variables = new ArrayList<String>();
		
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public Set<Transition<T>> getIncomming() {
		return incomming;
	}
	public Set<Transition<T>> getOutgoing() {
		return outgoing;
	}
	public Set<String> getAtomicPropositions() {
		return atomicPropositions;
	}
	public boolean addVariable(String name, String type, String value) {
		return this.variables.add(name + ": " + value);
	}
	public boolean changeVariable(String value) {
		int last = variables.size() - 1;
		String key = this.variables.get(last);
		this.variables.remove(last);
		return this.variables.add(key + value);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("unchecked")
		State<T> other = (State<T>) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		String str = this.id + "," + this.time + ",{";
		for(String var : this.variables) {
			str += "," + var + "";
		}
		str += "},[";
		for(String pros : this.atomicPropositions) {
			str += "," + pros;
		}
		str += "]";
		return str;
	}
}
