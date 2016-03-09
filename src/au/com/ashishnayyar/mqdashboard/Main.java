package au.com.ashishnayyar.mqdashboard;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		try {

			FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
			VBox root = (VBox) loader.load();

			Scene scene = new Scene(root);
			scene.getStylesheets().add("application.css");
			DashboardController controller = loader.<DashboardController>getController();
			controller.populateCombo();
			primaryStage.setScene(scene);
			primaryStage.setTitle("AMQ Explorer");
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
}
