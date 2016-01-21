package data;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.opencsv.CSVReader;

/**
 * This class is for loading in the .csv files.
 * @author Anthony
 */
public class CSVLoader{
	
	private CSVReader reader;
	private List<String[]> entries;
	private CSVObj obj;
	private String fileName;
	
	/**
	 * Constructor. Creates a file reader for the given path.
	 * @param path The targeted path.
	 * @throws IOException If one of numerous errors happen.
	 * @see FileReader
	 */
	public CSVLoader(String path) throws IOException{
		fileName = new File(path).getName();
		reader = new CSVReader(new FileReader(path));
		createObj();
	}
	
	/**
	 * Constructor. 
	 * @param file The file to retrieve.
	 * @throws IOException If one of numerous errors happen.
	 */
	public CSVLoader(File file) throws IOException {
		fileName = file.getName();
		reader = new CSVReader(new FileReader(file));
		createObj();
	}
	
	//just create an object that can be grabbed
	//throw the list ino a new CSVObj and set the toString
	private void createObj() throws IOException{
		entries = reader.readAll();
		obj = new CSVObj(entries);
		//formats the toString to everything before .csv
		obj.toString = fileName.substring(0,fileName.length()-4);
		reader.close();
	}
	
	/**
	 * Gets the retrieved, or current, CSVObj.
	 * @return The CSVObj in this object.
	 */
	public CSVObj getCSVObj(){
		return obj;
	}
	
	/**
	 * Loads a new csv in (so as to not have to initialize a new class).
	 * @param path The targeted path.
	 * @throws IOException If one of numerous errors happen.
	 */
	public void loadNewCSV(String path) throws IOException{
		blankObj();
		reader = new CSVReader(new FileReader(path));
		fileName = new File(path).getName();
		createObj();
	}
	
	/**
	 * Loads a new csv in (so as to not have to initialize a new class).
	 * @param file The file to retrieve.
	 * @throws IOException If one of numerous errors happen.
	 */
	public void loadNewCSV(File file) throws IOException{
		blankObj();
		reader = new CSVReader(new FileReader(file));
		fileName = file.getName();
		createObj();
	}
	
	private void blankObj(){
		//blank the current data so as to not mix anything up and 
		//still give null pointers if nothing was obtained
		entries = null;
		obj = null;
		fileName = null;
	}
	
	/**
	 * Gets the file name
	 * @return The name of the loaded file minus .csv .
	 */
	public String getFileName(){
		return fileName;
	}
	
}
