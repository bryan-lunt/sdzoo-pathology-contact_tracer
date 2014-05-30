package org.sandiegozoo.pathology.contact_tracer.dataimport;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.sandiegozoo.pathology.database.PathDBUtil;
import org.sandiegozoo.pathology.database.domain.*;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;


import au.com.bytecode.opencsv.*;

public class CSVImporter {
	
	private CSVReader reader;

	public interface ImportHandler{
		public void begin() throws Exception;
		public void handle(String[] one_line) throws Exception;
		public void done() throws Exception;
	}
	
	public CSVImporter(){};
	
	public void setSource(Reader inreader){
		reader = new CSVReader(inreader);
	}
	
	public void readInto(ImportHandler myHandler) throws Exception {
		myHandler.begin();
		
        try{
	        String [] nextLine;
	        while ((nextLine = reader.readNext()) != null) {
	        		myHandler.handle(nextLine);
	        	
	        }
        }finally{
        
        	myHandler.done();
        }
	}
	
}