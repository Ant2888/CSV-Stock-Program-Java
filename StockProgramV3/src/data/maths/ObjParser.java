package data.maths;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import data.CSVObj;

/**
 * This class acts as a static class to be used throughout the project and thus has methods for every part.
 * @author Anthony
 *
 */
public class ObjParser {

	//debating culling or keeping methods
	public static int[] ParseDateYMD(String date) throws NumberFormatException{
		String[] ymd = date.split("-");
		int[] ymdParsed = new int[ymd.length];
		for(int i=0; i<ymd.length; i++){
			ymdParsed[i] = Integer.parseInt(ymd[i]);
		}
		return ymdParsed;
	}
	
	//not being used but it is there if need be
	public static long getLongDateFormat(String date){
		String splitConcat = "";
		for(int i=0; i<date.split("-").length;i++){
			splitConcat += date.split("-")[i];
		}
		return Long.parseLong(splitConcat);
	}

	public static String[] getDate(String[] data){
		return data[0].split("-");
	}

	//function that normalizes a given month and day(wrt the months numerical val) as oppose to 
	//just graphing it via date
	public static float combineMD(String month, String day){
		return combineMD(getNumericMonth(month), Integer.parseInt(day));
	}
	public static float combineMD(int month, int day){
		float dayF;
		if(month%13 == 1 || month%13 == 3 || month%13 == 5 ||
				month%13 == 7 || month%13 == 8 || 
				month%13 == 10 || month%13 == 12){
			if(day == 31) dayF = .99F;
			else dayF = (day/31F);
		}else if(month%13 == 2){
			if(day==28) dayF = .99F;
			else dayF = ((day/28F));
		}else{
			if(day==30) dayF = .99F;
			else dayF = (day/30F);
		}
		return ((float)month)+dayF;
	}

	//if i decide to just convert the string and go about it that way
	public static void convertDate(CSVObj obj){
		for(int i=1; i<obj.getAmountOfData();i++){
			obj.getData(i)[0] = String.valueOf(getLongDateFormat(obj.getData(i)[0]));
		}
	}
	//stores all indexes for a particular key, year, into a sorted list
	//list is already sorted so nlogn doesn't apply
	//only year isn't sorted since it's a hashmap but 
	//we can get around it
	public static HashMap<String, List<Integer>> getAmountOfYears(CSVObj obj){
		HashMap<String, List<Integer>> years = new HashMap<>();
		for(int i=1; i<obj.getAmountOfData();i++){
			String yearToCheck = getDate(obj.getData(i))[0];
			if(years.get(yearToCheck) == null){
				List<Integer> toAdd = new ArrayList<>();
				int j=i; 
				while(j<obj.getAmountOfData() &&
						getDate(obj.getData(j))[0].compareTo(yearToCheck)==0){
					toAdd.add(j);
					j++;
				}
				years.put(yearToCheck, toAdd);
				i=j-1;
			}
		}
		return years; //todo
	}

	//creates a hashmap of year keys that give a value of a map
	//with month keys and give a value of where all the data for the months
	//are located
	public static HashMap<String, HashMap<String,List<Integer>>> getSpecificYearMonth(CSVObj obj){
		HashMap<String, HashMap<String, List<Integer>>> yearsWithMonths = new HashMap<>();

		for(int i=1; i<obj.getAmountOfData();i++){
			String yearToCheck = getDate(obj.getData(i))[0];
			if(yearsWithMonths.get(yearToCheck) == null){
				//fill the year
				HashMap<String,List<Integer>> monthsWithIndexes = new HashMap<>();
				while(getDate(obj.getData(i))[0].compareTo(yearToCheck)==0 &&
						i<obj.getAmountOfData()){
					String monthToCheck = getMonth(getDate(obj.getData(i))[1]);
					if(monthsWithIndexes.get(monthToCheck)==null
							&& (getDate(obj.getData(i))[0].compareTo(yearToCheck)==0)){
						List<Integer> toAdd = new ArrayList<>();
						while(i<obj.getAmountOfData() && (getMonth(getDate(obj.getData(i))[1]).compareTo(monthToCheck)==0)){
							toAdd.add(i);
							i++;
						}
						monthsWithIndexes.put(monthToCheck, toAdd);
					}
				}
				yearsWithMonths.put(yearToCheck, monthsWithIndexes);
			}
		}
		return yearsWithMonths;
	}

	public static int getNumericMonth(String month){
		String[] months = new String[]{
				"January", "February", "March", "April",
				"May", "June", "July", "August",
				"September", "October", "November",	"December"
		};
		for(int i=0; i< months.length;i++){
			if(months[i].compareToIgnoreCase(month)==0)
				return i;
		}
		return -1;
	}
	//returns the month
	public static String getMonth(int index){
		String[] months = new String[]{
				"January", "February", "March", "April",
				"May", "June", "July", "August",
				"September", "October", "November",	"December"
		};
		return months[index-1];
	}
	public static String getMonth(String num){
		return getMonth(Integer.parseInt(num));
	}
}
