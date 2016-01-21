package controller;

import static handlers.GlobalVars.GRAPHLOGO;
import static handlers.GlobalVars.NASDAQ;
import static handlers.GlobalVars.PRGMLOGO;
import static handlers.GlobalVars.SNP;

import java.io.IOException;

import javafx.scene.image.Image;
import javafx.stage.Stage;
import nodes.MainContainer;
import data.CSVLoader;

/**
 * Launcher class creates and initializes all resources necesary (or not) for use through the program.
 * Note that it will not fail to create the view but it CAN fail to load in the resources so code
 * should not be programmed to assume otherwise.
 * @author Anthony
 *
 */
public class Launcher {
	//** class is meant to throw into the controller. Just an intermediate so the Driver (Main) class isn't over-populated aka independent
	//private Stage stage;
	
	/**
	 * Constructor that loads the resources and sends the Stage to the controller
	 * @param stage The stage that the App will run on.
	 */
	public Launcher(Stage stage) {
		//this.stage = stage;
		//create the view and controller and data here
		loadResources();
		new Controller(new MainContainer(stage));
	}
	
	private void loadResources(){
		ClassLoader cLoad = getClass().getClassLoader();
		try {
			CSVLoader csv = new CSVLoader(cLoad.getResource("GSPC.csv").getFile());
			SNP = csv.getCSVObj();
			csv.loadNewCSV(cLoad.getResource("NDX.csv").getFile());
			NASDAQ = csv.getCSVObj();
			PRGMLOGO = new Image(cLoad.getResource("prgmLogo.png").toExternalForm());
			GRAPHLOGO = new Image(cLoad.getResource("graphLogo.png").toExternalForm());
		} catch (IOException e) {
			System.out.println("ERR RETRIEVING RESOURCES");
			e.printStackTrace();
		}
	}
}
