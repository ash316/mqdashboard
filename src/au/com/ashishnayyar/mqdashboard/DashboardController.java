package au.com.ashishnayyar.mqdashboard;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class DashboardController {
	
	@FXML private ComboBox<String> cmbEnv;
	
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
}
