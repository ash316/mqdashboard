package au.com.ashishnayyar.mqdashboard;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import au.com.ashishnayyar.mqdashboard.xml.Configs;

public class LoadConfig {

	public static Configs getAllConfigs()  {
		
		try {
			File file = new File("config/appConfig.xml");
	        JAXBContext jaxbContext = JAXBContext.newInstance(Configs.class);
	        Unmarshaller jaxbMarshaller = jaxbContext.createUnmarshaller();

	        Configs allConfigs = (Configs) jaxbMarshaller.unmarshal(file);
	        return allConfigs;	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
      }
	
	public static void main(String args[]) throws Exception {
		getAllConfigs();
	}
}
