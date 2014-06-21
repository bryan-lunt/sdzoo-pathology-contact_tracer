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


public class BasicDiagnosisHandler extends CSVInput {

	private int days_before;
	private int linger_days;
	
	public BasicDiagnosisHandler(File in, int days_before_in, int days_linger) {
		super( in);
		linger_days = days_linger;
		days_before = days_before_in;
		
	}

	public DateHandler diagnosis_date_handler = new DateHandler();
	

	
	public void handle_strarray(String[] nextLine) throws Exception{
        
		//FORMAT: Animal_ID, diagnosis_date, (name of disease?)
		
		String animal_native_id = nextLine[0].trim();
		
		Calendar diagnosis_date = diagnosis_date_handler.parse(nextLine[1]);
		
		Calendar onset_date = (Calendar)diagnosis_date.clone();
		onset_date.add(Calendar.DATE, -1 * days_before);
		
		String input_disease_name = null;
		if(nextLine.length >= 3){
			input_disease_name = nextLine[2].trim();
			if("".equals(input_disease_name)){
				input_disease_name = null;
			}
		}
		
		
		Calendar cure_date = new GregorianCalendar();//TODO: Should it really be today?
		
    	//Figure out if the Animal already exists in the database.
    	Animal theAnimal = path_db_util.completeOrCreateAnimal(animal_native_id);
    	
		Infection theInfection = new Infection();
		theInfection.days_linger = linger_days;
		theInfection.onset_date = onset_date;
		theInfection.end_date = cure_date;
		theInfection.diagnosis_date = diagnosis_date;
		theInfection.name = input_disease_name;
		theInfection.notes = null;
		theInfection.animal_id = theAnimal;
    	
		
		//Already saved once
    	//session.save(theAnimal);
    	session.save(theInfection);
 
	}
	
}
