package handlers;

import javafx.scene.image.Image;
import data.CSVObj;

public class GlobalVars {
	
	public enum Global_Enums{
		EXIT,
		ADD,
		MOUSE_SECONDARY_LISTVIEW,
		FIND, 
		CREATE_GRAPH, 
		NEW_GRAPH, 
		SELECTED_ITEM_TREE
	}
	//all are explanatory
	//create graph creates a graph from a year interval
	//new graph is for the interval node
	
	public static CSVObj NASDAQ, SNP;
	public static Image PRGMLOGO;
	public static Image GRAPHLOGO;
	
	public static final int WIDTH = 800, HEIGHT = 600;
}
