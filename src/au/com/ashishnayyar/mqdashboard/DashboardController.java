package au.com.ashishnayyar.mqdashboard;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class DashboardController {
	
	@FXML private ComboBox<String> cmbEnv;
	@FXML private TreeView<String> amqDetails;
	@FXML private VBox mainWindow;
	
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
            TreeItem<String> item = new TreeItem<String>(str.getDestination() + "  ["+ str.getSize() +"]");
            
            queues.getChildren().add(item);
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
            System.out.println("Selected Text : " + selectedItem);
			
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
	
}
