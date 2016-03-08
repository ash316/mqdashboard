package au.com.ashishnayyar.mqdashboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class JMXClient {

	MBeanServerConnection connection = null;
	List<JMXDetailsDO> queues;
	List<JMXDetailsDO> topics;
	
	public void discoverQueuesAndTopics() throws Exception {
		
		ObjectName name = new ObjectName("org.apache.activemq:*");
        Set<ObjectName> amq = connection.queryNames(name,null);
        System.out.println("Total "+ amq.size());
        
        if(amq.size() > 0)  {
        	String bName = amq.iterator().next().getCanonicalName().split(",")[0];
        	Set<ObjectName> queues =  connection.queryNames(new ObjectName(bName+",type=Broker,destinationType=Queue,destinationName=*"),null);
         	System.out.println("Total Queues : "+ queues.size());
         	
         	this.queues = new ArrayList<>(queues.size());
         	for(ObjectName objName: queues) {
         		JMXDetailsDO jmx = parseString(objName.getCanonicalName());
         		jmx.setSize(connection.getAttribute(objName, "QueueSize").toString());
         		this.queues.add(jmx);
         	}
         	Set<ObjectName> topics =  connection.queryNames(new ObjectName(bName+",type=Broker,destinationType=Topic,destinationName=*"),null);
         	System.out.println("Total Topics : "+ topics.size());
         	
         	this.topics = new ArrayList<>(topics.size());
         	
         	for(ObjectName objName: topics) {
         		this.topics.add(parseString(objName.getCanonicalName()));
         	}
         }
	}
	
	public CompositeData[] getAllMessages(String queueName) {
		try {
			
			ObjectName objName = new ObjectName("org.apache.activemq:type=Broker,brokerName=amq,destinationType=Queue,destinationName="+queueName);
			Set<ObjectInstance> instance = connection.queryMBeans(new ObjectName("org.apache.activemq:type=Broker,destinationType=Queue,destinationName="+queueName), null);
			CompositeData allMessages[] = (CompositeData[])connection.invoke(objName, "browse", null, null);
			System.out.println(allMessages.length);
			return allMessages;
		} catch (Exception e) {
			System.err.println(queueName + "Item not found in the JMX Tree");
			e.printStackTrace();
		}
		return null;
	}
	
	public void purgeQueue(String queueName) {
		try {
			
			ObjectName objName = new ObjectName("org.apache.activemq:type=Broker,brokerName=amq,destinationType=Queue,destinationName="+queueName);
			Set<ObjectInstance> instance = connection.queryMBeans(new ObjectName("org.apache.activemq:type=Broker,destinationType=Queue,destinationName="+queueName), null);
			System.out.println(connection.invoke(objName, "purge", null, null));
			System.out.println("Queue "+ queueName + " purged");
		} catch (Exception e) {
			System.err.println(queueName + "Item not found in the JMX Tree");
			e.printStackTrace();
		}
	}
	public void connect(String jmxurl) {
		try {
			HashMap<String, String[]> env = new HashMap();
			String[] credentials = new String[] { "admin" , "admin" };
			env.put("jmx.remote.credentials", credentials);

			JMXServiceURL url = new JMXServiceURL(jmxurl);
			JMXConnector jmxc = JMXConnectorFactory.connect(url, env);
			connection = jmxc.getMBeanServerConnection();
			
			System.out.println("Successfully Connected to JMX:");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private JMXDetailsDO parseString(String str) {
		//System.out.println(str);
		String allInfo[] = str.split(",");
		JMXDetailsDO jmxDetailsDO = new JMXDetailsDO();
				
		for(int i=0;i<allInfo.length;i++) {
			String type[] = allInfo[i].split("=");
			if(type.length > 1 && type[0].equals("destinationName")) {
				jmxDetailsDO.setDestination(type[1]);
			} 
		}
		return jmxDetailsDO;
	}
	
	public static void main(String args[]) {
		
		try {
			
            JMXClient client = new JMXClient();
            client.connect("service:jmx:rmi:///jndi/rmi://vtuvaina013.swglg01.local:1098/karaf-root");
            
            client.connection.addNotificationListener(new ObjectName("org.apache.activemq:type=Broker,brokerName=amq"), new NotificationListener() {
    			
    			@Override
    			public void handleNotification(Notification notification, Object handback) {
    				System.out.println("Notification Received.." + notification.getType());
    				
    			}
    		}, null, null);
            String domains[] = client.connection.getDomains();
            for (int i = 0; i < domains.length; i++) {
            	if(domains[i].equals("org.apache.activemq")) {
            		System.out.println("Active MQ Found");
            		break;
            	}
            }
            
            ObjectName name = new ObjectName("org.apache.activemq:*");
            Set<ObjectName> amq = client.getConnection().queryNames(name,null);
            System.out.println("Total "+ amq.size());
            if(amq.size() > 0)  {
            	String bName = amq.iterator().next().getCanonicalName().split(",")[0];
            	Set<ObjectName> queues=  client.getConnection().queryNames(new ObjectName(bName+",type=Broker,destinationType=Queue,destinationName=*"),null);
            	System.out.println("Total Queues : "+ queues.size());
            	Set<ObjectName> topics =  client.getConnection().queryNames(new ObjectName(bName+",type=Broker,destinationType=Topic,destinationName=*"),null);
            	System.out.println("Total Topics : "+ topics.size());
            }
           
            System.out.println("\nBye! Bye!");
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		
    }

	private MBeanServerConnection getConnection() {
		return connection;
	}


	public List<JMXDetailsDO> getQueues() {
		Collections.sort(queues);
		return queues;
	}

	public void setQueues(List<JMXDetailsDO> queues) {
		this.queues = queues;
	}

	public List<JMXDetailsDO> getTopics() {
		return topics;
	}

	public void setTopics(List<JMXDetailsDO> topics) {
		this.topics = topics;
	}
	
	
}
