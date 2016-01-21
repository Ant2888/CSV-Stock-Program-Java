package data;

import java.util.ArrayList;
import java.util.List;
/**
 * This class is used to represent groupings of CSVData objects to graph
 * @author Anthony
 * @see CSVData
 */
public class CSVInterval {
	
	private CSVObj dataSource;
	private List<CSVData> dataInInterval;

	/**
	 * Constructor. 
	 * @param dataSource The CSVObj that all the CSVData represents.
	 */
	public CSVInterval(CSVObj dataSource) {
		//note this(the required CSVObj) COULD be calculated through 
		//checking just one piece of CSVData but this saves a few
		//comparisons since we should ALWAYS know the obj we're referring to
		this.dataSource = dataSource;
		dataInInterval = new ArrayList<>();
	}

	/**
	 * Adds an entire year(s) of data to the interval from the bound CSVObj.
	 * @param year The year to add.
	 */
	public void addYears(String... year){
		//supports using multiple years
		for(int i=0; i<year.length;i++){
			//just grabs the list from the method in ObjParser
			//and adds all the non-nulls to the list
			List<CSVData> toJoin = dataSource.getDataForYear(year[i]);
			if(toJoin != null){
				dataInInterval.addAll(toJoin);
			}
		}
	}

	/**
	 * Adds and entire month(s) of data to the interval from the bound CSVObj.
	 * @param year The year of the month(s).
	 * @param month The month(s) of the given year to add.
	 */
	public void addMonths(String year, String... month){
		//same stuff as addYears
		for(int i=0; i<month.length;i++){
			List<CSVData> toJoin = dataSource.getDataForMonth(year, month[i]);
			if(toJoin != null){
				dataInInterval.addAll(toJoin);
			}
		}
	}

	/**
	 * Adds a day(s) of data to the interval from the bound CSVObj.
	 * @param year The year for the given day(s).
	 * @param month The month for the given day(s).
	 * @param day The day(s) to add.
	 */
	public void addDays(String year, String month, String... day){
		//same stuff as addYears
		for(int i=0; i<day.length;i++){
			CSVData toAdd = dataSource.getDataForDay(year, month, day[i]);
			if(toAdd != null){
				dataInInterval.add(toAdd);
			}
		}
	}
	
	/**
	 * Takes two data indexes, for a given CSVObj, and adds the distance in between.
	 * @param startIndex The starting index of the data.
	 * @param endIndex The ending index of the data.
	 * @see CSVData
	 */
	public void addDataBetweenIndexes(int startIndex, int endIndex){
		//start HAS to be a more recent date
		for(int i=startIndex; i<=endIndex; i++){
			//just adds from start to end inclusive
			dataInInterval.add(new CSVData(dataSource, i));
		}
	}

	/**
	 * Gets the list of data to add.
	 * @return The List of CSVData .
	 */
	public List<CSVData> getData(){
		return dataInInterval;
	}
	
	/**
	 * Gets the CSVObj that all the data is representing.
	 * @return The CSVObj bound to this object.
	 */
	public CSVObj getBoundObject(){
		return dataSource;
	}

}
