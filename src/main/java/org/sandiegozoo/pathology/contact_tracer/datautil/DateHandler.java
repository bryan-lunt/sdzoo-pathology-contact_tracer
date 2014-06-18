package org.sandiegozoo.pathology.contact_tracer.datautil;

import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DateHandler {
	
	
	/* STATIC STUFF */
	
	public static List<DateFormat> master_date_formats = new ArrayList<DateFormat>();
	
	static {
		
		DateFormat one_format;
		
		one_format = new SimpleDateFormat("yyyy-MM-dd");
		master_date_formats.add(one_format);
		
		one_format = new SimpleDateFormat("M/d/yyyy");
		master_date_formats.add(one_format);
		
		one_format = new SimpleDateFormat("MMM d, yyyy");
		master_date_formats.add(one_format);
		
	}
	
	
	/* MEMBER STUFF */
	
	public static List<DateFormat> date_formats = new ArrayList<DateFormat>();
	
	public DateHandler(){
		date_formats.addAll(master_date_formats);
	}
	
	/**
	 * This object will parse multiple date formats.
	 * Create one for each column you are using, and it will keep track of which format worked.
	 * Assuming a column is 
	 * 
	 * 
	 * @param in A string to parse.
	 * @return
	 * @throws Exception
	 */
	public Calendar parse(String in) throws Exception {
		Calendar ret_cal = new GregorianCalendar();
		
		if(null == in){return ret_cal;}
		String in_trim = in.trim();
		if(in_trim.equals("") || in_trim.equals("0")){
			return ret_cal;
		}
		
		//Unroll the loop once
		try{
			ret_cal.setTime(date_formats.get(0).parse(in));
			return ret_cal;
		}catch(Exception e){/*Didn't work*/}
			
		//
		for( int i = 1; i < date_formats.size(); i++ ){
			try{
				DateFormat one = date_formats.get(i);
				ret_cal.setTime(one.parse(in));
				
				//Looks like this one worked, make it the first one.
				date_formats.remove(i);
				date_formats.add(0,one);
				
				return ret_cal;
			}catch(Exception e){/* didn't work, try next */}
		}
		
		throw new Exception("No dateformat worked.");
		
	}
	
	public String format(Date in){
		return date_formats.get(0).format(in);
	}
}
