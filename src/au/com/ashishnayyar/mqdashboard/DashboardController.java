package au.com.ashishnayyar.mqdashboard;


import java.util.Optional;

import javax.management.openmbean.CompositeData;

import au.com.ashishnayyar.mqdashboard.xml.Config;
import au.com.ashishnayyar.mqdashboard.xml.Configs;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class DashboardController {
	
	@FXML private ComboBox<Config> cmbEnv;
	@FXML private TreeView<String> amqDetails;
	@FXML private VBox mainWindow;
	@FXML private TableView<MessageDetails> messageTable;
	@FXML private Label messageHeader;
	@FXML private Pane msgContents;
	@FXML private Button purgeButton;
	@FXML private TextField txtFilter;
	
	private String selectedQueue;
	
	private final ImageView queueIcon = new ImageView (new Image(getClass().getResourceAsStream("queue.png")));
	
	private JMXClient client = null;
	
	public DashboardController() {
		System.out.println("Inside Constructor " + this);
		client = new JMXClient();
		
	}
	public void populateCombo() {
		ObservableList<Config> environments = FXCollections.observableArrayList();
		Configs allConfigs = LoadConfig.getAllConfigs();
		
		if(!allConfigs.getConfigs().isEmpty()) {
			for(Config config: allConfigs.getConfigs()) {
				environments.add(config);
			}
		}
		
		cmbEnv.setItems(environments);
		/*cmbEnv.valueProperty().addListener(new ChangeListener<String>() {
	        @Override public void changed(ObservableValue ov, String t, String t1) {
	            System.out.println(ov);
	              System.out.println(t);
	              System.out.println(t1);
	          }    
	      });*/
	}
	
	public void connect() {
		Config config = cmbEnv.getValue();
		client.connect(config.getUrl());
		System.out.println(config.getUrl());
		populateAMQTree();
	}

	public void filterQueues() {
		if(null != txtFilter.getText() && txtFilter.getText().trim().length() >= 2) {
			populateAMQTree();
		}
	}
	private void discoverQueues() {
		try {
			client.discoverQueuesAndTopics(txtFilter.getText());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void populateAMQTree() {

		discoverQueues();
		
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
        }
        queues.setValue(queues.getValue() + " ["+queues.getChildren().size() +"]");
        
        for (JMXDetailsDO str : client.getTopics()) {
            TreeItem<String> item = new TreeItem<String> (str.getDestination());            
            topics.getChildren().add(item);
        }
        topics.setValue(topics.getValue() + " ["+topics.getChildren().size() +"]");
        
        amqDetails.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>() {
		
        	@Override
		public void changed(ObservableValue<? extends TreeItem<String>> observable, TreeItem<String> oldValue,
				TreeItem<String> newValue) {
			TreeItem<String> selectedItem = newValue;
			
			if(selectedItem != null) {
				System.out.println("Selected Text : " + selectedItem.getValue());
				CompositeData allMessages[] = client.getAllMessages(selectedItem.getValue());
				ObservableList<MessageDetails> messages = FXCollections.observableArrayList();
				messageHeader.setText("ALL MESSAGES ON : " + selectedItem.getValue() + " [SIZE : " + allMessages.length +"]");
				selectedQueue = selectedItem.getValue();
				
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
	
	public void purgeQueue() {
		System.out.println("selectedQueue "+ selectedQueue);
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText("Purge Queue ?");
		alert.setContentText("Are you sure you want to purge "+ selectedQueue +" ? This will remove all the messages from the queue and can't be undone.");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			client.purgeQueue(selectedQueue);
		} 
	}
	private void showMessages(ObservableList<MessageDetails> allMessages) {
		if(!allMessages.isEmpty()) {
			((TableColumn)messageTable.getColumns().get(0)).setCellValueFactory(new PropertyValueFactory<MessageDetails, String>("messageId"));
			((TableColumn)messageTable.getColumns().get(1)).setCellValueFactory(new PropertyValueFactory<MessageDetails, String>("expiration"));
			((TableColumn)messageTable.getColumns().get(2)).setCellValueFactory(new PropertyValueFactory<MessageDetails, String>("timeStamp"));
			((TableColumn)messageTable.getColumns().get(3)).setCellValueFactory(new PropertyValueFactory<MessageDetails, String>("priority"));
		}
		
		msgContents.getChildren().clear();
		messageTable.setItems(allMessages);
		if(allMessages.size() > 0) {
			purgeButton.setVisible(true);
		} else {
			purgeButton.setVisible(false);
		}
		messageTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if(newSelection != null) {
				TextArea messageContents = new TextArea(newSelection.getContents());
				
				messageContents.setEditable(false);
				messageContents.setPrefWidth(msgContents.getPrefWidth());
				messageContents.setPrefHeight(msgContents.getPrefHeight());
				
				msgContents.getChildren().add(messageContents);
			}
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
