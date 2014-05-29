package org.sandiegozoo.pathology.contact_tracer.dataimport;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.sandiegozoo.pathology.database.*;
import org.sandiegozoo.pathology.database.domain.*;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class ContaminationHandler extends HibernateImportHandler {

	public ContaminationHandler(SessionFactory sessionFact) {
		super(sessionFact);
		// TODO Auto-generated constructor stub
	}

	static DateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
	

	
	public void handle(String[] nextLine) throws Exception{
        
		
		
		//FORMAT: Enclosure_id, start_date, end_date (today if blank), direct_or_environmental (environmental if blank)
		
		
		
		String enclosure_id = nextLine[0];
		Date start_date = date_format.parse(nextLine[1]);
		
		Date end_date = null;
		if(nextLine.length >= 3){
			try{
				end_date = date_format.parse(nextLine[2]);
			}catch(Exception e){
				end_date = new Date();//TODO: Should I make it tomorrow?
			}
		}
		
		Boolean is_direct = false;
		if(nextLine.length >= 4){
			is_direct = boolean_helper(nextLine[3]);
		}
		
    	Enclosure theEnclosure = new Enclosure();
    	theEnclosure.setName(enclosure_id);
    	theEnclosure = PathDBUtil.completeOrCreateEnclosure(theEnclosure, session);
    	
    	Contamination theContamination = new Contamination();
    	theContamination.setEnclosure(theEnclosure);
    	theContamination.setStartDate(start_date);
    	theContamination.setEndDate(end_date);
    	theContamination.setIsDirect(is_direct);
    	
    	session.save(theEnclosure);
    	session.save(theContamination);
		
		
 
	}
	
	
	private boolean boolean_helper(String in){
		if(in == null || "".equals(in.trim())){
			return false;
		}
		
		try{
			return !(Integer.parseInt(in.trim()) != 0);
		}catch(Exception e){}
		
		try{
			return Boolean.parseBoolean(in.trim());
		}catch(Exception e){}
		
		return false;
	}
}

