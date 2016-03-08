package au.com.ashishnayyar.mqdashboard.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="configs")
@XmlAccessorType(XmlAccessType.FIELD)
public class Configs {

	@XmlElement(name = "config", type = Config.class)
	List<Config> configs = new ArrayList<>();

	public List<Config> getConfigs() {
		return configs;
	}

	public void setConfigs(List<Config> configs) {
		this.configs = configs;
	}

}
