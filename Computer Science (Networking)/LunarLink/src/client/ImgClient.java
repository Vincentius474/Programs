package client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ImgClient  extends Application {

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		ImgClientPane rootClientPane = new ImgClientPane(primaryStage);
		Scene scene = new Scene(rootClientPane, 950, 850);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Lunar Link [ Image Client ]");
		primaryStage.setOnCloseRequest((e) -> {
			System.exit(0);
		});
		primaryStage.show();
		
		
	}
}
