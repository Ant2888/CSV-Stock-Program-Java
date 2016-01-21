package data.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import data.CSVLoader;
import data.CSVObj;

/**
 * This class creates a FileChooser to select a CSV
 * @author Anthony
 *
 */
public class StockAdder{
	
	private Stage stage;
	private FileChooser fc;
	private CSVObj obj;
	
	/**
	 * Constructor. Creates a FileChooser and CSVObj after a file has been selected.
	 * @param stage A Stage to attach to.
	 * @throws IOException If one of numerous errors happen.
	 */
	public StockAdder(Stage stage) throws IOException{
		this.stage =stage;
		fc = new FileChooser();
		obj = loadCSVObj(getFile());
	}
	
	/**
	 * Loads a csv from a new instance of a CSVLoader
	 * @param file The file selected
	 * @return A new CSVObj.
	 * @throws IOException If one of numerous errors happen.
	 */
	public CSVObj loadCSVObj(File file) throws IOException{
		return (new CSVLoader(file.getPath())).getCSVObj();
	}
	
	/**
	 * Gets the file that was selected 
	 * @return A File that is the path that was selected from the user.
	 * @throws FileNotFoundException If the file that was attempted to load is not found.
	 */
	public File getFile() throws FileNotFoundException{
		fc.setTitle("Select Stock File");
		fc.getExtensionFilters().add(
				new ExtensionFilter("Comma-separated values", "*.csv"));
		File selected = fc.showOpenDialog(stage);
		if(selected == null){
			throw new FileNotFoundException();
		}else{
			return selected;
		}
	} 
	
	/**
	 * Gets the object that was loaded from the file selected
	 * @return The CSVObj loaded.
	 */
	public CSVObj getLoadedObj(){
		return obj;
	}
}
