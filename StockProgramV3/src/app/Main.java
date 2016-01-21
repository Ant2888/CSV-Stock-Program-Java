package app;
	
import javafx.application.Application;
import javafx.stage.Stage;
import controller.Launcher;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		new Launcher(primaryStage);
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
