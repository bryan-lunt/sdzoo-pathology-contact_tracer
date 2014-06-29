package org.sandiegozoo.pathology.contact_tracer.dataimport;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.sandiegozoo.pathology.contact_tracer.datautil.DateHandler;
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

	public DateHandler start_date_handler = new DateHandler();
	public DateHandler end_date_handler = new DateHandler();

	
	public void handle_strarray(String[] nextLine) throws Exception{
        
		
		
		//FORMAT: Enclosure_id, start_date, end_date (today if blank), {Subsequent columns Ignored}
		
		
		
		String enclosure_id = nextLine[0];
		Calendar start_date = start_date_handler.parse(nextLine[1]);
		
		Calendar end_date = null;
		if(nextLine.length >= 3){
			try{
				end_date = end_date_handler.parse(nextLine[2]);
			}catch(Exception e){
				end_date = new GregorianCalendar();//TODO: Should I make it tomorrow?
			}
		}
		
    	Enclosure theEnclosure = path_db_util.completeOrCreateEnclosure(enclosure_id);
    	
    	Contamination theContamination = new Contamination();
    	theContamination.enc_id = theEnclosure;
    	theContamination.start_date = start_date;
    	theContamination.end_date = end_date;
    	
    	//Already saved once
    	//session.save(theEnclosure);
    	session.persist(theContamination);
		
	}
	
	
	
}

