package controller;

import static handlers.GlobalVars.PRGMLOGO;
import handlers.BadEventException;
import handlers.GlobalVars.Global_Enums;
import handlers.Observer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Platform;
import nodes.FindNode;
import nodes.IntervalNode;
import nodes.MainContainer;
import data.CSVData;
import data.CSVInterval;
import data.CSVObj;
import data.files.StockAdder;
import data.maths.Grapher;

/**
 * This class is the controller between the views and models, passing data back and forth
 * via a Observer Pattern.
 * @author Anthony
 *
 */
public class Controller implements Observer{

	private MainContainer view;
	private ArrayList<CSVObj> objectArray;

	/**
	 * Constructor that attaches the main view (of the MVC) to it.
	 * @param view The main view of the App.
	 */
	public Controller(MainContainer view) {
		this.view = view;
		view.addObserver(this);
		view.startView();
		view.getStage().getIcons().add(PRGMLOGO);
		objectArray = new ArrayList<>();
	}

	@Override
	public void updates(Object args) {
		//currently supports a GE throw
		//interval node throw
		//and findnode throw but easily capable of implementing anything see blow
		if(args instanceof Global_Enums){
			try {
				decodeEvent((Global_Enums)args);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if(args instanceof IntervalNode){//<---
			graphInterval((IntervalNode)args);
		}else if(args instanceof FindNode){
			grabFind((FindNode)args);
		}
	}

	private void decodeEvent(Global_Enums args) throws BadEventException, IOException{
		//method is used to decode what is thrown from the subject. 
		//this decodes any thrown enums, most should be self-explanatory.
		switch(args){
		case EXIT:
			Platform.exit();
			System.exit(0);
			break;
		case ADD:
			addStock();
			break;
		case NEW_GRAPH:
			graphPrompt();
			break;
		case CREATE_GRAPH:
			createGraph(view.getParentInNode());
			break;
		case FIND:
			startFind();
			break;
		case SELECTED_ITEM_TREE:
			setSelectedTree();
			break;
		default:
			//a.k.a not programmed in
			throw new BadEventException(args);
		}
	}

	private void startFind(){
		//just creates a new find node for the user to input what they want
		new FindNode(view);
	}

	private void grabFind(FindNode args){
		//will be thrown if the user finishes selecting a date
		//the subject will throw the findnode class and here is where
		//it is then parsed
		String date = args.getDate(); //need to get the date selected
		String boundNode = args.getSelectedObj(); // and the obj associated

		CSVObj boundObj = null; //since FindNode only returns the obj in String form
		//we need to find the ACTUAL obj hence the for loop
		for(CSVObj temp: objectArray){
			if(temp.toString().compareTo(boundNode)==0){
				boundObj = temp;
				break;
			}
		}

		//if we didn't find it there was either an error or they just selected
		//nothing
		if(boundObj == null) return;

		//if the user inputed NOTHING than we can just get the most recent index (0 is the header
		//so 1 is the first piece of data)
		if(date.replaceAll(" ", "").compareTo("")==0){
			view.placeDataInView(new CSVData(boundObj, 1));
		}else{
			//otherwise we will use the function to determine the closest index
			view.placeDataInView(new CSVData(boundObj,boundObj.getClosestValue(date)));
		}
	}

	private void setSelectedTree(){
		//this method will set the data in the view
		//to any FULL date clicked
		String selected = view.getTreeSelectedData(); //getter for date clicked
		String stock = view.getTreeSelectedRoot(); //getter for the stock name

		CSVObj parent = null; //similar to the grabFind method need to find the actual reference
		for(CSVObj temp: objectArray){
			if(temp.toString().compareToIgnoreCase(stock)==0){
				parent = temp;
				break;
			}
		}
		if(parent == null) return; //means that you selected either stock or the parent -- could parse that
		//but currently limiting it to just selecting a specific date

		String[] format = selected.split(" ");
		if(format.length != 3) return; //means a FULL date wasn't selected

		int index = parent.findDataIndex(format[0], format[1], format[2]);
		CSVData toPlace = new CSVData(parent, index, selected);
		view.placeDataInView(toPlace);
	}

	/**
	 * Creates a filechooser and allows the user to select some stock.csv.
	 * If the file is not found the program simply discards the FNFException.
	 * Otherwise it is added to the necesary views.
	 */
	public void addStock(){
		try {
			//just creates a new selector (glorified filechooser)
			StockAdder selector;
			selector = new StockAdder(view.getStage());
			CSVObj obj = selector.getLoadedObj();
			//adds the object
			objectArray.add(obj);
			view.addCSVOBj(obj);
		} catch (FileNotFoundException e) {}
		catch(IOException e){
			e.printStackTrace();
		}
	}

	/**
	 * Removes any given CSVObj object. Currently not implemented.
	 * @param obj The CSVObj to remove.
	 */
	public void removeStock(CSVObj obj){
		//simply removes it from the view and objectArrays
		//not sure if it is 100% safe (not sure if the view will
		//update properly)
		objectArray.remove(obj);
		view.removeCSVObj(obj);
	}

	/**
	 * Creates a graph for the most recent year of a given CSVObj.
	 * @param obj The CSVObj to graph the most recent year of.
	 */
	public void createGraph(CSVObj obj){
		//this is just for viewing the year
		Grapher graphs = new Grapher(this);
		CSVInterval interval = new CSVInterval(obj);
		CSVData ar = view.getDataInNode();
		//need to create an interval and then fill it with just
		//from the beginning to the end of the most recent year
		interval.addYears(ar.getDate().split(" ")[0]);
		graphs.addPortfolio(interval);
		graphs.showGraphs(false);
	}

	/**
	 * Creates a graph for the most recent year of a given CSVInterval.
	 * @param csv The CSVInterval (filled with a proper CSVObj parent) to graph.
	 */
	public void createGraph(CSVInterval csv){
		//this is for the general case
		Grapher graphs = new Grapher(this);
		graphs.addPortfolio(csv);
		graphs.showGraphs(false);
	}

	/**
	 * This will create a graph given two specific dates via the IntervalNode class.
	 * If no dates are selected then it will assume the whole entire dataset. 
	 * If one date is selected it will go from there to the most recent.
	 * @param intervalNode IntervalNode to get the interval from.
	 */
	public void graphInterval(IntervalNode intervalNode){
		String obj = intervalNode.getSelectedObj();
		String fromDate = intervalNode.getFromDate();
		String toDate = intervalNode.getToDate();

		//just incase they left from empty or didn't input anything at all
		//(adds a comparison but can save the for loop)
		if(obj == null) return;

		//have to find the obj
		CSVObj toBind = null;
		for(CSVObj temp: objectArray){
			if(temp.toString().compareTo(obj)==0){
				toBind = temp;
				break;
			}
		}
		//need to get the indexes that were selected of that interval
		//this will allow you to graph years if you want as well
		int fromIndex = 0;
		int toIndex = 0;
		CSVInterval interval = new CSVInterval(toBind);

		if(fromDate.replaceAll(" ", "").compareTo("")==0){
			fromIndex = 1; //if nothing was inputed than most recent
		}else{
			//need to find the most recent index
			fromIndex = toBind.getClosestValue(fromDate);
		}
		//let the use just not select a TO 
		if(toDate.replaceAll(" ", "").compareTo("")==0){
			toIndex = 1;
		}else{
			toIndex = toBind.getClosestValue(toDate);
		}

		if(fromDate.replaceAll(" ", "").compareTo("")==0 &&
				toDate.replaceAll(" ", "").compareTo("")==0){
			fromIndex =1;
			toIndex = toBind.getAmountOfData();
		}

		//the method requires that the shorted number be first
		if(toIndex > fromIndex){
			interval.addDataBetweenIndexes(fromIndex, toIndex);
		}else{
			interval.addDataBetweenIndexes(toIndex, fromIndex);
		}
		createGraph(interval);
	}

	/**
	 * The method to be called when an interval is wanted to be created
	 * @see IntervalNode
	 */
	public void graphPrompt(){
		new IntervalNode(view);
	}
}