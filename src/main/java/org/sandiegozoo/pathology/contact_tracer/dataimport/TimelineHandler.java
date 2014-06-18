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


public class TimelineHandler extends CSVInput {

	public TimelineHandler(File in) {
		super(in);
		// TODO Auto-generated constructor stub
	}

	public DateHandler move_in_handler = new DateHandler();
	public DateHandler move_out_handler = new DateHandler();
	
	private class EntryStruct{
		
		public String animal_native_id;
		public Calendar move_in;
		public Calendar move_out;
		public String enclosure_name;
		
		EntryStruct(String[] in) throws Exception{
			animal_native_id = in[0].trim();
			
			enclosure_name = in[1].trim();
			
			move_in = move_in_handler.parse(in[2]);
			
			//What if no move-out is available?
			move_out = move_out_handler.parse(in[3]);
			
			
		}
		
	}
	
	
	public void handle_strarray(String[] nextLine) throws Exception{
		//FORMAT: animal_native_id, move_in_date (as YYYY-MM-DD), move_out_date (as YYYY-MM-DD), enclosure_name
		
		
    	EntryStruct one_file_entry = new EntryStruct(nextLine);
    	
    	//Figure out if the Animal and/or Enclosure already exist in the database.
    	Animal theAnimal = path_db_util.completeOrCreateAnimal(one_file_entry.animal_native_id);
    	
    	//Figure out if the Enclosure already exists in the database.
    	Enclosure theEnclosure = path_db_util.completeOrCreateEnclosure(one_file_entry.enclosure_name);
    	
    	//Finally create the housing and save it to the database
    	Housing oneHousing = new Housing();
    	oneHousing.enc_id = theEnclosure;
    	oneHousing.animal_id = theAnimal;
    	oneHousing.move_in = one_file_entry.move_in;
    	oneHousing.move_out = one_file_entry.move_out;
    	
    	//These are already saved
    	//session.save(theAnimal);
    	//session.save(theEnclosure);
    	session.persist(oneHousing);
    	
	}
	
}
