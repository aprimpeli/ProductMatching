package UnitConversion;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;






import org.jsoup.Jsoup;

import UnitConversion.DateUtil;
import UnitConversion.GeoCoordinateParser;
import UnitConversion.NumericParser;
import UnitConversion.URLParser;
import UnitConversion.UnitParser;
import UnitConversion.SubUnit;



/**
 * Guesses the type of the attributes
 * 
 * @author petar
 * 
 */
public class ColumnTypeGuesser {
	public static enum ColumnDataType {
		numeric, string, coordinate, date, link, bool, unknown, unit, list, custom, longstring
	};

	private UnitParser unitParser;

	public ColumnTypeGuesser() {
		unitParser = new UnitParser();
	}

	/**
	 * use for rough type guesssing
	 * 
	 * @param columnValue
	 *            is the value of the column
	 * @param columnHeader
	 *            is the header of the column, often contains units
	 *            abbreviations
	 * @param useUnit
	 *            the typeGuesser will try to find units
	 * @param unit
	 *            the returning unit (if found)
	 * @return
	 */
	public ColumnDataType guessTypeForValue(String columnValue, String columnHeader, boolean useUnit, SubUnit unit) {

		if (columnValue.matches("^\\{.+\\|.+\\}$"))
			return ColumnDataType.list;

		if (useUnit) {

		
	        SubUnit unitS = null;
			if (columnHeader != null) {
				
				unitS = unitParser.parseUnit(columnValue + " " + extractUnitAbbrFromHeader(columnHeader));
			}
			if (unitS == null) {
				unitS = unitParser.parseUnit(columnValue);
			}
			if (unitS != null) {
				unit.setAbbrevations(unitS.getAbbrevations());
				unit.setBaseUnit(unitS.getBaseUnit());
				unit.setConvertible(unitS.isConvertible());
				unit.setName(unitS.getName());
				unit.setNewValue(unitS.getNewValue());
				unit.setRateToConvert(unitS.getRateToConvert());
				return ColumnDataType.unit;
			}
			
			
			
		}

		try {
			Date date = DateUtil.parse(columnValue);
			if (date != null)
				return ColumnDataType.date;
		} catch (Exception e) {

		}

		if (Boolean.parseBoolean(columnValue))
			return ColumnDataType.bool;

		if (URLParser.parseURL(columnValue))
			return ColumnDataType.link;

		if (GeoCoordinateParser.parseGeoCoordinate(columnValue))
			return ColumnDataType.coordinate;

		if (NumericParser.parseNumeric(columnValue)) {
			return ColumnDataType.numeric;
		}

		return ColumnDataType.string;

	}

	/**
	 * Returns the value from brackets
	 * 
	 * @param header
	 * @return
	 */
	private static String extractUnitAbbrFromHeader(String header) {
		if (header.matches(".*\\(.*\\).*"))
			return header.substring(header.indexOf("(") + 1, header.indexOf(")"));

		return header;
	}

	public static void main(String[] args) throws IOException {
		// initialize the type guesser
		ColumnTypeGuesser g = new ColumnTypeGuesser();

//		// list
//		System.out.println(g.guessTypeForValue("{value1|value2}", null, false, null));
//
//		// numeric
//		System.out.println(g.guessTypeForValue("1,256", null, false, null));
//		System.out.println(g.guessTypeForValue("1,256.05", null, false, null));
//
//		// date
//		System.out.println(g.guessTypeForValue("january 12", null, false, null));
//
//		// coordinate
//		System.out.println(g.guessTypeForValue("41.1775 20.6788", null, false, null));
//		
		
		// memory in text
//		System.out.println(g.guessTypeForValue("32 GB", null, true, subUnit));

		// guess units
		//first get the part of the columnValue that refers to a value with its unit of measurement
		System.out.println("Start");
		String text=Jsoup.parse(
				fileToText("C:/Users/Johannes/Google Drive/Master_Thesis/2.ProfilingOfData/LabelledDataProfiling/HTML_Pages/Unified_extra/headphones/node1dd3a71382fd4ecc7ed26dfea156a8.html")).text();
		text=text.toLowerCase();
		Pattern pattern= Pattern.compile("(?:\\.*+\\d)++ *+[^ \\d]++");
		Matcher extractedValues= pattern.matcher(Jsoup.parse(text).text());
		List<String> valuesToBeReplaced = new ArrayList<String>();
		List<String> newValues = new ArrayList<String>();
		
		while(extractedValues.find()){
			String tobeConverted=extractedValues.group(0);
			//take out the 2g, 3g, 4g so that they are not mixed with weights
			if (tobeConverted.replaceAll(" ", "").equals("2g") || tobeConverted.replaceAll(" ", "").equals("3g") 
					|| tobeConverted.replaceAll(" ", "").equals("4g"))	continue;
				
			       
	        SubUnit subUnit = new SubUnit();
	        
	        if(!g.guessTypeForValue(tobeConverted, null, true, subUnit).equals(ColumnDataType.unit)) continue;
			String baseUnit = subUnit.getBaseUnit().getName();
			String normalizedValue = subUnit.getNewValue();
			
			
			
			valuesToBeReplaced.add(tobeConverted);
			newValues.add(normalizedValue+subUnit.getBaseUnit().getMainUnit().getName());
			
			System.out.println(tobeConverted+" was converted to: " + normalizedValue + " "
					+ subUnit.getBaseUnit().getMainUnit().getName() + " " + baseUnit);
		}
		
		for (int i=0;i<valuesToBeReplaced.size();i++)
			text = text.replace(valuesToBeReplaced.get(i),newValues.get(i));
				
		System.out.println(text);
	}
	
	public static String fileToText (String filepath) throws IOException{
		File f = new File(filepath);
		if (!f.exists()) {
		    System.out.println("The file could not be found: "+filepath);
		    System.exit(0);
		}
		byte[] encoded = Files.readAllBytes(Paths.get(filepath));
		  return new String(encoded, StandardCharsets.UTF_8);	
	}
	
}
