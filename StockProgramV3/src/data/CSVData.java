package data;

import data.maths.ObjParser;

/**
 * This class is used to represent an individual day of a stock.
 * @author Anthony
 *
 */
public class CSVData {

	private CSVObj parent;
	private int dataIndex;
	private String date; //it is formated as Year Month(non-numeric) Day ALWAYS
	
	/**
	 * Constructor. Calculates date
	 * @param parent The CSVObj to bind to the object.
	 * @param dataIndex The index of data that this object is suppoes to represent.
	 */
	public CSVData(CSVObj parent, int dataIndex) {
		this.parent = parent;
		this.dataIndex = dataIndex;
		calcDate();
	}
	
	//date HAS to be in Year Month(non-numeric) Day here
	//prefer to use this one (saves overhead of calcdate)
	/**
	 * Constructor.
	 * @param parent The CSVObj to bind to the object.
	 * @param dataIndex The index of data that this object is suppoes to represent.
	 * @param date The date of that index (not required, can be calculated).
	 */
	public CSVData(CSVObj parent, int dataIndex, String date){
		this.parent = parent;
		this.dataIndex = dataIndex;
		this.date = date;
	}
	
	private void calcDate(){
		//just calculates the date off of the ObjParsers method of finding the date
		//only happens if one isn't given
		date = "";
		String[] temp = parent.getData(dataIndex)[0].split("-");
		date = temp[0];
		date+= " "+ObjParser.getMonth(temp[1]);
		date+= " "+temp[2];
	}
	
	/**
	 * Gets the date of this object
	 * @return The date in Year Month(non-numeric) Day, format.
	 */
	public String getDate(){
		return date;
	}
	
	/**
	 * Gets the CSVObj bound to this object.
	 * @return The CSVObj that this object is representing data from.
	 */
	public CSVObj getParent(){
		return parent;
	}
	
	/**
	 * Gets the index of this object.
	 * @return The data index that this object is representing.
	 */
	public int getIndex(){
		return dataIndex;
	}
	
	/**
	 * Gets all the data that this class is representing.
	 * @return The String array of all the data.
	 * @see CSVObj
	 */
	public String[] getDataAtIndex(){
		return parent.getData(dataIndex);
	}
	
	@Override
	public String toString(){
		//just use the parents toString + the date
		//so we can still differentiate the parent
		//and this data via toString.
		return parent.toString+": "+date;
	}
}
