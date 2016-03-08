package au.com.ashishnayyar.mqdashboard;

public class JMXDetailsDO implements Comparable<JMXDetailsDO>{

	private String type;
	private String destination;
	private String consumerId;
	private String size;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getConsumerId() {
		return consumerId;
	}

	public void setConsumerId(String consumerId) {
		this.consumerId = consumerId;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	@Override
	public int compareTo(JMXDetailsDO o) {
		if(getDestination() != null) {
			return getDestination().compareTo(o.getDestination()); 
		}
		return 0;
	}
	
	
}
