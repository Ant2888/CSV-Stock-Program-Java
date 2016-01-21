package data.maths;

import static handlers.GlobalVars.GRAPHLOGO;
import static handlers.GlobalVars.HEIGHT;
import static handlers.GlobalVars.WIDTH;
import handlers.Observer;
import handlers.Subject;

import java.util.ArrayList;

import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import data.CSVInterval;

/**
 * This class creates a graph for a given interval and draws+calculates the best fit as well.
 * @author Anthony
 *
 */
public class Grapher implements Subject{
	
	private NumberAxis xAxis;
	private Stage stage;
	private Scene scene;
	private NumberAxis yAxis;
	private LineChart<Number, Number> chart;
	private ArrayList<XYChart.Series<Number, Number>> portfolioArray;
	private BorderPane mainBox;
	private Button details;
	
	//all these are used for the details in the graphdetails class so 
	//we give them protected tags
	protected CSVInterval intervalGraphed;
	protected float[] abFits;
	protected double minAdj;
	protected double maxAdj;
	protected float percentInc;
	protected float avgVal;
	protected int minX;
	protected int maxX;

	private Observer o;
	
	/**
	 * Constructor. Creates and empty chart that can have intervals added to it.
	 * @param o An observer to send its actions to.
	 */
	public Grapher(Observer o) {
		this.o = o;
		createChart();
		init();
	}

	private void init(){
		//just setup the borderpanes and scenes etc etc
		mainBox = new BorderPane();
		mainBox.setCenter(chart);
		mainBox.setBottom(details);
		scene = new Scene(mainBox,(WIDTH/2)+200,HEIGHT/2);
		stage = new Stage();
		stage.getIcons().add(GRAPHLOGO);
		stage.setScene(scene);
		stage.setTitle("Stock Graph");
	}

	private void createChart(){
		//create the necessary labeling and axis stuff
		xAxis = new NumberAxis();
		yAxis = new NumberAxis();
		yAxis.setAutoRanging(false);
		xAxis.setAutoRanging(false);
		xAxis.setLabel("Month");
		yAxis.setLabel("Adj. Close");
		chart = new LineChart<>(xAxis, yAxis);
		portfolioArray = new ArrayList<>();

		//if you click details I get rid of the button and place the details in 
		details = new Button("Details");
		details.setOnAction(e->{
			details.setVisible(false);
			mainBox.setBottom(null);
			mainBox.setRight(new GraphDetails(this));
		});
	}

	private void initTitle(CSVInterval interval){
		intervalGraphed = interval;
		//creates a title for start date and end date
		chart.setTitle(interval.getBoundObject().toString()+": " + interval.getData()
				.get(interval.getData().size()-1).getDate()+" - "+
				interval.getData().get(0).getDate());
	}

	/**
	 * Sets a custom title for the graph.
	 * @param title A String that will represent the title.
	 */
	public void setTitle(String title){
		chart.setTitle(title);
	}

	/**
	 * Adds another interval to the graph. Note any more than one is currently untested and will have unpredicted results.
	 * @param toGraph The interval to graph.
	 * @see CSVInterval
	 */
	public void addPortfolio(CSVInterval toGraph){
		//adds the interval for it to graph
		XYChart.Series<Number, Number> series = new XYChart.Series<>();
		series.setName(toGraph.getBoundObject().toString());
		fillPortfolio(toGraph, series);
	}

	private void fillPortfolio(CSVInterval toGraph,
			Series<Number, Number> portfolio) {
		initTitle(toGraph);
		//gets the first year to scale everything off of
		String firstYear = toGraph.getData().get(toGraph.getData().size()-1).getDate()
				.split(" ")[0];
		
		//need to normalize these variables since we're creating more of a function
		//then just a stat plot
		double max = Double.parseDouble(toGraph.getData().get(0).getDataAtIndex()[4]);
		double min = Double.parseDouble(toGraph.getData().get(0).getDataAtIndex()[4]);
		//also want to grab a max and min for custom resizing
		//since JavaFx's LineChart autoresizing sucks
		float[] normalized = new float[toGraph.getData().size()];

		//down below is nasty because I wanted to do it all in one pass.
		//it essentially is setting the max min variables, getting the y val
		//which will be the adj close., then normalizing the month and day, and 
		//adding it to a delta of 12 times how many years you are away from the start.
		//so as to allow multi-year functionality.
		for(int i=0;i<toGraph.getData().size(); i++){
			float y = Float.parseFloat(toGraph.getData().get(i).getDataAtIndex()[4]);
			if(y>max) max = y;
			else if(y<min) min = y; 
			//just normalizes the data and determines the delta
			String date = toGraph.getData().get(i).getDate();
			int yearDelta = Math.abs(Integer.parseInt(date.split(" ")[0])-Integer.parseInt(firstYear));
			int scaledMonth = ObjParser.getNumericMonth(date.split(" ")[1]) + 12*yearDelta;
			int day = Integer.parseInt(date.split(" ")[2]);
			normalized[i] = ObjParser.combineMD(scaledMonth, day);

			portfolio.getData().add(new XYChart.Data<Number, Number>(normalized[i],y));
		}
		//setting the protected vals
		minAdj = min;
		maxAdj = max;
		maxAdj = (int)(maxAdj*100);
		maxAdj/=100;
		minAdj = (int)(minAdj*100);
		minAdj/=100;
		
		minX = (int)(Math.ceil(normalized[normalized.length-1]));
		maxX = (int)(Math.floor(normalized[0]));
		
		//add the data and resize everything
		portfolioArray.add(portfolio);
		resizeYBound(max, min);
		resizeXBound(maxX,minX);
		updateGraph();//this allows the support of multigraphs though it is currently
		//not used

		//this is just getting the bestfit and calculates the delta in the best fit
		//for the given interval (percent change)
		abFits = calcABFit(portfolio);
		Series<Number, Number> bestFit = new XYChart.Series<>();
		bestFit.setName("Best Fit");
		int x1 = (int)(Math.ceil(normalized[0]));
		int x2 = (int)(Math.floor(normalized[normalized.length-1]));
		float y1 = abFits[1]*x1+abFits[0];
		float y2 = abFits[1]*x2+abFits[0];
		//note y2 is the original number while y1 is the change 
		percentInc = (y1-y2)/Math.abs(y2);
		percentInc = (int)(percentInc*10000);
		percentInc /=100;
		
		float middleX = ((int)(Math.floor(normalized[normalized.length-1])) + 
				(int)(Math.ceil(normalized[0])))/2F;
		
		avgVal = abFits[1]*middleX+abFits[0];
		avgVal = (int)(avgVal*100);
		avgVal /=100;
		for(int i = minX; i<=maxX; i++){
			bestFit.getData().add(new XYChart.Data<Number, Number>(i,abFits[1]*i+abFits[0]));
			//it needs the coords x,f(x) and f(x) is just i*bVal + aVal
		}
		portfolioArray.add(bestFit);
		updateGraph();
	}

	//this is mainly for getting the bestFit for references like NDX or GSPC
	//and comparing them to the ABFit of the interval given
	/**
	 * Gets the A and B values of the Best Fit Regression.
	 * @param toRetrieve The Interval to get the Best Fit for.
	 * @return A float array with size 2. Index 0: The A val, Index 1: The B val.
	 */
	public float[] calcABFit(CSVInterval toRetrieve){
		//to get the AB we need to fake graph it so we need to get all x,y values
		//then the best fit is figured from the overloaded method below
		XYChart.Series<Number, Number> series = new XYChart.Series<>();
		String firstYear = toRetrieve.getData().get(toRetrieve.getData().size()-1).getDate()
				.split(" ")[0];
		float[] normalized = new float[toRetrieve.getData().size()];

		for(int i=0;i<toRetrieve.getData().size(); i++){
			float y = Float.parseFloat(toRetrieve.getData().get(i).getDataAtIndex()[4]);
			String date = toRetrieve.getData().get(i).getDate();
			int yearDelta = Math.abs(Integer.parseInt(date.split(" ")[0])-Integer.parseInt(firstYear));
			int scaledMonth = ObjParser.getNumericMonth(date.split(" ")[1]) + 12*yearDelta;
			int day = Integer.parseInt(date.split(" ")[2]);
			normalized[i] = ObjParser.combineMD(scaledMonth, day);

			series.getData().add(new XYChart.Data<Number, Number>(normalized[i],y));
		}
		return calcABFit(series);
	}

	//returns slope of the best fit in a val b val format where
	//b is the slope and a is the y intercept
	/**
	 * Gets the A and B values of the Best Fit Regression.
	 * @param portfolio The series to retrieve the Best Fit of.
	 * @return A float array with size 2. Index 0: The A val, Index 1: The B val.
	 */
	public float[] calcABFit(XYChart.Series<Number, Number> portfolio){
		//this just figures out the best fit regression. It's a simple
		//formula (simple meaning just a quick plug and chug).
		int n = portfolio.getData().size();

		//it takes 4 summations so I did them all in one pass below
		float sumY = 0;
		float sumX2 = 0;
		float sumX = 0;
		float sumXY = 0;

		for(int i=0; i<n; i++){
			//The hack needed to turn a number into anything is ridiculas
			//it will not let me cast Number into ANYTHING. Its toString() happens
			//to be the number it represents though so I just did that.
			//Maybe you can figure it out :P.
			float x =  Float.parseFloat(""+portfolio.getData().get(i).getXValue());
			float y =  Float.parseFloat(""+portfolio.getData().get(i).getYValue());
			sumY += y;
			sumX += x;
			sumX2 += (x*x);
			sumXY += (x*y);
		}
		//formula for bestfit
		float bVal = ((n*sumXY)-((sumX)*(sumY)))/((n*sumX2)- (sumX*sumX));
		float aVal = ((sumY*sumX2)-(sumX*sumXY))/((n*sumX2)-(sumX*sumX));
		return new float[]{aVal,bVal};
	}

	/**
	 * Updates the graph. This is called internally mostly everywhere but for changing if symbols are shown or not.
	 */
	public void updateGraph(){
		for(Series<Number, Number> temp: portfolioArray){
			if(!chart.getData().contains(temp)){
				chart.getData().add(temp);
			}
		}
	}

	/**
	 * Gets the series that are in the graph.
	 * @return An ArrayList of series that are in the portfolio.
	 */
	public ArrayList<XYChart.Series<Number, Number>> getPortfolio(){
		return portfolioArray;
	}

	/**
	 * Displays the graph (with symbols or not).
	 * @param showSymbols Shows each plot or not.
	 */
	public void showGraphs(boolean showSymbols){
		chart.setCreateSymbols(showSymbols);
		stage.show();
	}

	/**
	 * Gets the current calculated best fit.
	 * @return A float array with size 2. Index 0: The A val, Index 1: The B val.
	 */
	public float[] currentCalcFit(){
		return abFits;
	}

	private void resizeYBound(double upperBound, double lowerBound){
		yAxis.setLowerBound((int)Math.floor(lowerBound));
		yAxis.setUpperBound((int)Math.ceil(upperBound));
	}

	private void resizeXBound(double upperBound, double lowerBound){
		xAxis.setLowerBound(lowerBound);
		xAxis.setUpperBound(upperBound);
	}

	//class may only have on observer at a time so we require an observer on initialization
	@Override
	public void addObserver(Observer o) {
	}

	@Override
	public void removeObserver(Observer o) {
	}

	@Override
	public void notifyObserver(Object args) {
		o.updates(args);
	}
}
