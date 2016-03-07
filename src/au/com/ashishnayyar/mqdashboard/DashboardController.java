package au.com.ashishnayyar.mqdashboard;

import java.awt.ScrollPane;

import javax.management.openmbean.CompositeData;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class DashboardController {
	
	@FXML private ComboBox<String> cmbEnv;
	@FXML private TreeView<String> amqDetails;
	@FXML private VBox mainWindow;
	@FXML private TableView<MessageDetails> messageTable;
	@FXML private Label messageHeader;
	@FXML private TableView<String> messageContents;
	
	private final ImageView queueIcon = new ImageView (new Image(getClass().getResourceAsStream("queue.png"))
    );
	private JMXClient client = null;
	
	public DashboardController() {
		client = new JMXClient();
		client.connect();
		
	}
	public void populateCombo() {
		ObservableList<String> environments = FXCollections.observableArrayList();
		environments.add("DEV");
		environments.add("SIT");
		environments.add("PREPROD-1");
		environments.add("PREPROD-2");
		environments.add("PROD-1");
		environments.add("PROD-2");
		cmbEnv.setValue("Select Environment");
		cmbEnv.setItems(environments);
		 
	}
	
	public void connect() {
		System.out.println(cmbEnv.getValue());
	}
	
	public void populateAMQTree() {
	
		try {
			client.discoverQueuesAndTopics();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		TreeItem<String> rootItem = new TreeItem<String> ("AMQ");
		TreeItem<String> queues = new TreeItem<String> ("Queues");
		TreeItem<String> topics = new TreeItem<String> ("Topics");
		rootItem.getChildren().add(queues);
		rootItem.getChildren().add(topics);
        rootItem.setExpanded(true);
        queues.setExpanded(true);
        
        for (JMXDetailsDO str : client.getQueues()) {
            TreeItem<String> item = new TreeItem<String>(str.getDestination());
            queues.getChildren().add(item);
            item.getChildren().add(new TreeItem<String>("SIZE [" + str.getSize() + "]"));
        }
        for (JMXDetailsDO str : client.getTopics()) {
            TreeItem<String> item = new TreeItem<String> (str.getDestination());            
            topics.getChildren().add(item);
        }
        amqDetails.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>() {
		
        	@Override
		public void changed(ObservableValue<? extends TreeItem<String>> observable, TreeItem<String> oldValue,
				TreeItem<String> newValue) {
			TreeItem<String> selectedItem = newValue;
			
			if(selectedItem != null) {
				System.out.println("Selected Text : " + selectedItem.getValue());
				CompositeData allMessages[] = client.getAllMessages(selectedItem.getValue());
				ObservableList<MessageDetails> messages = FXCollections.observableArrayList();
				messageHeader.setText("ALL MESSAGES ON : " + selectedItem.getValue());
				for(CompositeData currentMessage : allMessages) {
					MessageDetails message = new MessageDetails();
					message.setContents(currentMessage.get("Text").toString());
					message.setMessageId(currentMessage.get("JMSMessageID").toString());
					message.setTimeStamp(currentMessage.get("JMSTimestamp").toString());
					message.setPriority(currentMessage.get("JMSPriority").toString());
					message.setExpiration(currentMessage.get("JMSExpiration").toString());
					messages.add(message);
				}
				showMessages(messages);				
			}
            
		}

	});
        ContextMenu cm = new ContextMenu();
        MenuItem refresh = new MenuItem("Refresh");
        refresh.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	populateAMQTree();
            }
        });
        
        cm.getItems().add(refresh);
        amqDetails.setContextMenu(cm);
        amqDetails.setRoot(rootItem);
	}
	
	private void showMessages(ObservableList<MessageDetails> allMessages) {
		if(!allMessages.isEmpty()) {
			((TableColumn)messageTable.getColumns().get(0)).setCellValueFactory(new PropertyValueFactory<MessageDetails, String>("messageId"));
			((TableColumn)messageTable.getColumns().get(1)).setCellValueFactory(new PropertyValueFactory<MessageDetails, String>("expiration"));
			((TableColumn)messageTable.getColumns().get(2)).setCellValueFactory(new PropertyValueFactory<MessageDetails, String>("timeStamp"));
			((TableColumn)messageTable.getColumns().get(3)).setCellValueFactory(new PropertyValueFactory<MessageDetails, String>("priority"));
		}
		
		messageTable.setItems(allMessages);
		messageTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			
		});
	}
	
	public static class MessageDetails {
		String contents;
		String messageId;
		String timeStamp;
		String priority;
		String expiration;
		
		public String getContents() {
			return contents;
		}
		public void setContents(String cntents) {
			this.contents = cntents;
		}
		public String getMessageId() {
			return messageId;
		}
		public void setMessageId(String messageId) {
			this.messageId = messageId;
		}
		public String getTimeStamp() {
			return timeStamp;
		}
		public void setTimeStamp(String timeStamp) {
			this.timeStamp = timeStamp;
		}
		public String getPriority() {
			return priority;
		}
		public void setPriority(String priority) {
			this.priority = priority;
		}
		public String getExpiration() {
			return expiration;
		}
		public void setExpiration(String expiration) {
			this.expiration = expiration;
		}
		
	}
}
