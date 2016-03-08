package au.com.ashishnayyar.mqdashboard.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="config")
public class Config {

	@XmlElement
	private String username;
	@XmlElement
	private String password;
	@XmlElement
	private String jmxroot;
	@XmlElement
	private String host;
	@XmlElement
	private String name;
	@XmlElement
	private String url;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getJmxroot() {
		return jmxroot;
	}
	public void setJmxroot(String jmxroot) {
		this.jmxroot = jmxroot;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Override
	public String toString() {
		return getName();
	}
	
	
}
