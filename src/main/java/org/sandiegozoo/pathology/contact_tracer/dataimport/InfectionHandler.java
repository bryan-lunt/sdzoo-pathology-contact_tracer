package org.sandiegozoo.pathology.contact_tracer.dataimport;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.sandiegozoo.pathology.contact_tracer.datautil.DateHandler;
import org.sandiegozoo.pathology.database.PathDBUtil;
import org.sandiegozoo.pathology.database.domain.*;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class InfectionHandler extends CSVInput {

	public InfectionHandler(File in) {
		super( in);
		// TODO Auto-generated constructor stub
	}

	public DateHandler onset_date_handler = new DateHandler();
	public DateHandler diagnosis_date_handler = new DateHandler();
	public DateHandler cure_date_handler = new DateHandler();
	

	
	public void handle_strarray(String[] nextLine) throws Exception{
        
		//FORMAT: Animal_Native_ID, linger_days, onset_date, (cure_date <default to never/today>, (diagnosis_date <default to today>, (name <no default>, ( notes <no default>))))
		
		String animal_native_id = nextLine[0].trim();
		
		int linger_days = 0;


		if(nextLine[1] != null && !"".equals(nextLine[1].trim())){
			linger_days = Integer.parseInt(nextLine[1].trim());
		}

		
		Calendar onset_date = onset_date_handler.parse(nextLine[2]);
		
		Calendar cure_date = new GregorianCalendar();//TODO: Should it really be today?
		if(nextLine.length >= 4){
			try{
				cure_date = cure_date_handler.parse(nextLine[3]);
			}catch(Exception e){cure_date = new GregorianCalendar();}
		}
		
		Calendar diagnosis_date = null;
		if(nextLine.length >= 5){
			try{
				//Convoluted, but ensures diagnosis_date remains null unless everything works.
				//I could null it out in the catch clause though. (But that then risks getting missed/separated?)
				diagnosis_date = diagnosis_date_handler.parse(nextLine[4]);
			}catch(Exception e){diagnosis_date = null;}
		}
		
		String name = null;
		if(nextLine.length >= 6){
			name = nextLine[5];
		}
		
		String notes = null;
		if(nextLine.length >= 7){
			notes = nextLine[6];
		}
		
		
		
    	//Figure out if the Animal already exists in the database.
    	Animal theAnimal = path_db_util.completeOrCreateAnimal(animal_native_id);
    	
		Infection theInfection = new Infection();
		theInfection.days_linger = linger_days;
		theInfection.onset_date = onset_date;
		theInfection.end_date = cure_date;
		theInfection.diagnosis_date = diagnosis_date;
		theInfection.name = name;
		theInfection.notes = notes;
		theInfection.animal_id = theAnimal;
    	
		
		//Already saved once
    	//session.save(theAnimal);
    	session.persist(theInfection);
 
	}
	
}
