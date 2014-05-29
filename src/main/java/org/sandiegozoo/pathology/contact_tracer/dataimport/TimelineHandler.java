package org.sandiegozoo.pathology.contact_tracer.dataimport;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.sandiegozoo.pathology.database.PathDBUtil;
import org.sandiegozoo.pathology.database.domain.*;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class TimelineHandler extends HibernateImportHandler {

	public TimelineHandler(SessionFactory sessionFact) {
		super(sessionFact);
		// TODO Auto-generated constructor stub
	}

	static DateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
	
	private class EntryStruct{
		
		public String animal_native_id;
		public Date move_in;
		public Date move_out;
		public String enclosure_name;
		
		EntryStruct(String[] in) throws Exception{
			animal_native_id = in[0].trim();
			move_in = date_format.parse(in[1].trim());
			
			//What if no move-out is available?
			if(in[2] == null || in[2].trim().equals("")){
				move_out = new Date();
			}else{
				move_out = date_format.parse(in[2].trim());
			}
			
			enclosure_name = in[3].trim();
		}
		
	}
	
	public void handle(String[] nextLine) throws Exception{
        	
    	EntryStruct one_file_entry = new EntryStruct(nextLine);
    	
    	//Figure out if the Animal and/or Enclosure already exist in the database.
    	Animal theAnimal = new Animal();
    	theAnimal.setNativeID(one_file_entry.animal_native_id);
    	theAnimal = PathDBUtil.completeOrCreateAnimal(theAnimal, session);
    	
    	//Figure out if the Enclosure already exists in the database.
    	Enclosure theEnclosure = new Enclosure();
    	theEnclosure.setName(one_file_entry.enclosure_name);
    	theEnclosure = PathDBUtil.completeOrCreateEnclosure(theEnclosure, session);
    	
    	//Finally create the housing and save it to the database
    	Housing oneHousing = new Housing();
    	oneHousing.enc_id = theEnclosure;
    	oneHousing.animal_id = theAnimal;
    	oneHousing.move_in = one_file_entry.move_in;
    	oneHousing.move_out = one_file_entry.move_out;
    	
    	session.save(theAnimal);
    	session.save(theEnclosure);
    	session.save(oneHousing);
 
	}
	
}
