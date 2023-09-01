package mdu.se.rebeca.statespace;

public class Transition <T extends State<T>> {
	
	private T source, destination;
	private String action;
	private String sender;
	private String owner;
	private int weight;
	private int shift;
	
	public T getSource() {
		return source;
	}
	public void setSource(T source) {
		this.source = source;
	}
	public T getDestination() {
		return destination;
	}
	public void setDestination(T destination) {
		this.destination = destination;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public int getShift() {
		return shift;
	}
	public void setShift(int shift) {
		this.shift = shift;
	}

	public Transition<T> clone() {
		Transition<T> retValue = new Transition<T>();
		retValue.action = this.action;
		retValue.destination = this.destination;
		retValue.shift = this.shift;
		retValue.source = source;
		retValue.weight = this.weight;
		return retValue;
	}
	@Override
	public String toString() {
		String str = this.action + ", from: (" + this.source.toString() + "), \nto: (" + this.destination.toString() + ")";
		return str;
	}
}
