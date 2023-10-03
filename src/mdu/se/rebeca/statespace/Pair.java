package mdu.se.rebeca.statespace;

public class Pair <T extends State<T>>{
	private State<T> state;
	private Transition<T> action;
	
	public Pair(State<T> key, Transition<T> value) {
		this.state = key;
		this.action = value;
	}
	
	public State<T> getKey() {
		return this.state;
	}
	
	public Transition<T> getValue() {
		return this.action;
	}
}
