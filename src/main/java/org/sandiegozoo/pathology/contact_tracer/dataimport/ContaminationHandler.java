package org.sandiegozoo.pathology.contact_tracer.dataimport;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.sandiegozoo.pathology.database.*;
import org.sandiegozoo.pathology.database.domain.*;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class ContaminationHandler extends CSVInput {

	public ContaminationHandler(File in) {
		super( in);
		// TODO Auto-generated constructor stub
	}

	static DateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
	

	
	public void handle_strarray(String[] nextLine) throws Exception{
        
		
		
		//FORMAT: Enclosure_id, start_date, end_date (today if blank), direct_or_environmental (environmental if blank)
		
		
		
		String enclosure_id = nextLine[0];
		Calendar start_date = new GregorianCalendar();
		start_date.setTime(date_format.parse(nextLine[1]));
		
		Calendar end_date = null;
		if(nextLine.length >= 3){
			try{
				end_date = new GregorianCalendar();
				end_date.setTime(date_format.parse(nextLine[2]));
			}catch(Exception e){
				end_date = new GregorianCalendar();//TODO: Should I make it tomorrow?
			}
		}
		
		Boolean is_direct = false;
		if(nextLine.length >= 4){
			is_direct = boolean_helper(nextLine[3]);
		}
		
    	Enclosure theEnclosure = new Enclosure();
    	theEnclosure.name = enclosure_id;
    	theEnclosure = PathDBUtil.completeOrCreateEnclosure(theEnclosure, session);
    	
    	Contamination theContamination = new Contamination();
    	theContamination.enc_id = theEnclosure;
    	theContamination.start_date = start_date;
    	theContamination.end_date = end_date;
    	theContamination.is_direct = is_direct;
    	
    	session.save(theEnclosure);
    	session.save(theContamination);
		
	}
	
	
	
}

