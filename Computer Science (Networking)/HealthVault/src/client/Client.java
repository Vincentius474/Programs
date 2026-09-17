package client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Client extends Application {

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		HealthClientPane rootClientPane = new HealthClientPane();
		Scene scene = new Scene(rootClientPane, 1000, 600);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Health Client | Health_Vault");
		primaryStage.show();
		
	}
	
}
