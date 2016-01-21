package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import data.maths.ObjParser;

/**
 * This class is meant to represent the entierty of the CSV that was loaded in
 * @author Anthony
 * @see CSVLoader
 */
public class CSVObj {

	//Note: YMD format is Year Month Day
	//Dash format is YYYY-MM-DD
	private List<String[]> entries;
	private int numCols;
	private int amountEntries; // Note this DOES NOT include the header
	protected String toString; //giving access to the toString to all the other data
	private HashMap<String, HashMap<String, List<Integer>>> map;

	/**
	 * Constructor. Creates a CSVObj for a given List of String arrays.
	 * @param entries A List of String arrays. Should have been loaded in from the CSVLoader.
	 * @see CSVLoader
	 */
	public CSVObj(List<String[]> entries) {
		this.entries = entries;
		populate();
	}

	private void populate() {
		numCols = entries.get(0).length;
		amountEntries = entries.size() - 1;
		map = ObjParser.getSpecificYearMonth(this);
	}

	/**
	 * Gets the first index of data in the List
	 * @return The header of the CSV file.
	 */
	public String[] getHeader() {
		//the 0th index is ALWAYS the header
		//in standard .csv files 
		return entries.get(0);
	}

	/**
	 * Gets the String array of the given index
	 * @param index The index to retrieve in the object.
	 * @return The data for that given index.
	 */
	public String[] getData(int index) {
		return entries.get(index);
	}

	/**
	 * Gets the amount of columns. This is calculated through the header size and will be the
	 * size of every string array.
	 * @return An int representing the amount of items per String array.
	 */
	public int getAmountOfColumns() {
		return numCols;
	}

	/**
	 * Gets the amount of data inside the List. This does not include the header
	 * @return The amount of data in the List minus one.
	 */
	public int getAmountOfData() {
		return amountEntries;
	}

	/**
	 * 
	 * @return The List in the object.
	 */
	public List<String[]> getList() {
		return entries;
	}

	/**
	 * Gets the index of the date (or closest to it).
	 * @param YMDFormatedDate A Year Month Day formated String.
	 * @return The index closest (if not exact) to a given year month day.
	 */
	public int getClosestValue(String YMDFormatedDate){
		//picks the first index first 
		int indexToPlace = 1;
		String[] toChk = YMDFormatedDate.split(" ");
		//have to check which parts, if any, are missing
		try{
			indexToPlace = findDataIndex(toChk[0], toChk[1], toChk[2]);
		}catch(ArrayIndexOutOfBoundsException exc){
			//means that days/months weren't added
			try{
				//since there are no days we can pick the first day in the month
				indexToPlace = getMap().get(toChk[0]).get(toChk[1]).get(getMap().get(toChk[0]).get(toChk[1]).size()-1);
			}catch(ArrayIndexOutOfBoundsException exc2){
				//if THAT doesn't work than you just gave a year
				indexToPlace = getMostRecentIndexOfYear(toChk[0]);
				//if this doesn't work there as an input err
			}
		}
		return indexToPlace;
	}

	/**
	 * Finds the data at a given date.
	 * @param dashFormat A String in dash format.
	 * @return The String array at the index for the given date.
	 */
	// must be in "YYYY-MM-DD" format
	public String[] findData(String dashFormat) {
		String year = dashFormat.split("-")[0];
		String month = ObjParser.getMonth(dashFormat.split("-")[1]);
		if (map.get(year) != null && map.get(year).get(month) != null) {
			for (Integer num : map.get(year).get(month)) {
				if (this.getData(num)[0].compareTo(dashFormat) == 0) {
					return this.getData(num);
				}
			}
			return null;
		} else {
			return null;
		}
	}

	/**
	 * Finds the index of a given date.
	 * @param year The year of the date.
	 * @param month The month of the date.
	 * @param day The day of the date.
	 * @return The exact index of the date, -1 if not found.
	 */
	public int findDataIndex(String year, String month, String day) {
		//if any of the dates/months given are null we can just return -1 right away.
		if (map.get(year) != null && map.get(year).get(month) != null) {
			for (Integer num : map.get(year).get(month)) {
				//this is where we find the day
				if (Integer.parseInt(this.getData(num)[0].split("-")[2]) == Integer
						.parseInt(day)) {
					return num;
				}
				//if we cant find it the day will never be returned
				//and it will return -1
			}
			return -1;
		}
		return -1;
	}

	/**
	 * Finds the index of a given date via dash format.
	 * @param dashFormat A String that represents the date in dash format.
	 * @return The exact index of the date, -1 if not found.
	 */
	// this is "YYYY-MM-DD" format
	public int findDataIndex(String dashFormat) {
		//does the same stuff as the first part (since this is overloaded)
		String year = dashFormat.split("-")[0];
		String month = ObjParser.getMonth(dashFormat.split("-")[1]);
		if (map.get(year) != null && map.get(year).get(month) != null) {
			for (Integer num : map.get(year).get(month)) {
				if (this.getData(num)[0].compareTo(dashFormat) == 0) {
					return num;
				}
			}
			return -1;
		} else {
			return -1;
		}
	}

	/**
	 * Gets the first day in a year.
	 * @param year The year to retrieve the index.
	 * @return The most recent data index value of a given year, -1 if not found.
	 */
	public int getMostRecentIndexOfYear(String year){
		//if the year doesnt exist that it doesnt exist
		if(map.get(year) == null) return -1;
		for(int i=12; i>=1; i--){
			//just go through the months until
			//we find the most recent one
			//so starting january this will have to loop 12 times
			//but right now it will go through it once
			List<Integer> month = map.get(year).get(ObjParser.getMonth(i));
			if(month == null);
			else {
				//note 0 is the most recent (highest date)
				//while month.size()-1 is the first day in the month
				return month.get(0);
			}
		}
		return -1;
	}

	/**
	 * Gets the days List for a given year, month.
	 * @param year The year for a given month.
	 * @param month The month to get the list.
	 * @return A List of Integer's for an entire month that represent the data index values in the CSVObj.
	 */
	public List<Integer> getListAtMonth(String year, String month) {
		if (map.get(year) == null)
			return null;
		return map.get(year).get(month);
	}

	/**
	 * Gets the CSVData for a given day.
	 * @param year The year for a given day.
	 * @param month The month for a given day.
	 * @param day The day.
	 * @return A new CSVData that contains that dates data index and the CSVObj.
	 */
	public CSVData getDataForDay(String year, String month, String day) {
		return new CSVData(this, findDataIndex(year, month, day), year + " "
				+ month + " " + day);
	}

	/**
	 * Gets the CSVData for a given month.
	 * @param year The year for a given month.
	 * @param month The month.
	 * @return A List of CSVData for all the days in a month
	 */
	public List<CSVData> getDataForMonth(String year, String month) {
		if (map.get(year) == null)
			return null;
		else if (map.get(year).get(month) == null)
			return null;
		List<CSVData> toReturn = new ArrayList<>();
		for (Integer temp : map.get(year).get(month)) {
			toReturn.add(new CSVData(this, temp));
		}
		return toReturn;
	}

	/**
	 * Gets the CSVData for a given year.
	 * @param year The year.
	 * @return A List of CSVData for all the days in the year.
	 */
	public List<CSVData> getDataForYear(String year) {
		if (map.get(year) == null)
			return null;
		List<CSVData> toReturn = new ArrayList<>();
		for (int i = 12; i >= 1; i--) {
			List<Integer> month = map.get(year).get(ObjParser.getMonth(i));
			if(month != null){
				for (Integer temp : month) {
					toReturn.add(new CSVData(this, temp));
				}
			}
		}
		return toReturn;
	}
	
	public List<CSVData> getDataForYear(int year) {
		return getDataForYear(String.valueOf(year));
	}

	/**
	 * Gets the map in the object.
	 * @return A HashMap with key String: years and value of a HashMap with key String: months and value of a List of Integers that are the dataindexes of each day.
	 */
	public HashMap<String, HashMap<String, List<Integer>>> getMap() {
		return map;
	}

	@Override
	public String toString() {
		return toString;
	}
}
