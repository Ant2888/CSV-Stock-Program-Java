package data.maths;

import static handlers.GlobalVars.HEIGHT;
import handlers.GlobalVars;

import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import data.CSVData;
import data.CSVInterval;

/**
 * This class places more details of a current graph into the view.
 * @author Anthony
 *
 */
public class GraphDetails extends AnchorPane{

	private Grapher grapher;
	private boolean isAccurate = true;
	
	/**
	 * Constructor. Creates the details from a graph.
	 * @param grapher A graph to grab the details from.
	 */
	public GraphDetails(Grapher grapher) {
		this.grapher = grapher;
		init();
	}
	
	private void init(){
		setPrefSize(200, HEIGHT/2);
		Label header = new Label("Details");
		Label percentInc = new Label("Percent Increase");
		Label avgStock = new Label("Avg. Stock Price");
		Label maxAdj = new Label("Max Adj. Close");
		Label minAdj = new Label("Min Adj. Close");
		Label ndq = new Label("NASDAQ Trend For Interval");
		
		setAnchor(header,82,14);
		setAnchor(percentInc,7,74);
		setAnchor(avgStock,9,112);
		setAnchor(maxAdj,15,150);
		setAnchor(minAdj,17,188);
		setAnchor(ndq,27,226);
		getChildren().addAll(header, percentInc, avgStock, maxAdj, minAdj, ndq);
		calculateData();
	}
	
	private void calculateData(){
		//note I realised way to late that NDQ is actually NDX but oh well
		//not like it really matters
		Label percent = new Label();
		Label avg = new Label(String.valueOf(grapher.avgVal));
		Label max = new Label(String.valueOf(grapher.maxAdj));
		Label min = new Label(String.valueOf(grapher.minAdj)); 
		Label ndq = new Label();
		
		float ndqVal = getNDQTrend();
		//get the ndq's trend value
		String ndqString = String.valueOf(ndqVal)+"%";
		
		if(isAccurate);//if it's not accurate we don't want to use it.
		else{
			ndqString+= "\n* Intervals do not fully intersect";
		}
		ndq.setText(ndqString);
		
		//just simple coloring pending on how positive or neg
		if(ndqVal > 0 && ndqVal < 5){
			ndq.setStyle("-fx-text-fill: rgb(102,238,133)");
		}else if(ndqVal > 5){
			ndq.setStyle("-fx-text-fill: green");
		}else if(ndqVal > -5){
			ndq.setStyle("-fx-text-fill: rgb(255,128,128)");
		}else if(ndqVal == 0){
			ndq.setStyle("-fx-text-fill: black");
		}else{
			ndq.setStyle("-fx-text-fill: red");
		}
		
		//same as NDX stuff except for OUR graph
		float percInc = grapher.percentInc;
		percent.setText(String.valueOf(percInc)+"%");
		if(percInc > 0 && percInc < 5){
			percent.setStyle("-fx-text-fill: rgb(102,238,133)");
		}else if(percInc > 5){
			percent.setStyle("-fx-text-fill: green");
		}else if(percInc > -5){
			percent.setStyle("-fx-text-fill: rgb(255,128,128)");
		}else if(percInc == 0){
			percent.setStyle("-fx-text-fill: black");
		}else{
			percent.setStyle("-fx-text-fill: red");
		}
		
		setAnchor(percent, 118, 74);
		setAnchor(avg, 118, 112);
		setAnchor(max, 118, 150);
		setAnchor(min, 118, 188);
		setAnchor(ndq, 86, 251);
		getChildren().addAll(percent,avg,max,min,ndq);
	}
	
	private float getNDQTrend(){
		List<CSVData> data = grapher.intervalGraphed.getData();
		CSVInterval ndqInterval = new CSVInterval(GlobalVars.NASDAQ);
		
		try{
		ndqInterval.addDataBetweenIndexes(data.get(0).getIndex(), data.get(data.size()-1).getIndex());
		}catch(IndexOutOfBoundsException e){
			//this can only happen if we have no recent data or the other interval is too far away
			//so we'll just calc the whole thing
			isAccurate = false; //need to let them know that this isn't entirely accurate
			return 0;
		}
		
		float[] abVal = grapher.calcABFit(ndqInterval);
		
		//slope is bVal*month + aVal
		//so the change is y2-y1/y1 * 100
		float y1 = abVal[1]*grapher.minX+abVal[0];
		float y2 = abVal[1]*grapher.maxX+abVal[0];
		
		//simple decimal removing
		return ((int)(((y2-y1)/y1)*10000))/100F;
	}
	
	private void setAnchor(Node toAnchr, double x, double y){
		AnchorPane.setLeftAnchor(toAnchr, x);
		AnchorPane.setTopAnchor(toAnchr, y);
	}

}
