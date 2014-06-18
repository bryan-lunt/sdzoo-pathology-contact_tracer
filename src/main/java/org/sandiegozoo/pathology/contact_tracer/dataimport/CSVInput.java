package org.sandiegozoo.pathology.contact_tracer.dataimport;

import java.io.*;

import org.hibernate.*;
import org.sandiegozoo.pathology.contact_tracer.CTIOHandler;
import org.sandiegozoo.pathology.database.PathDBUtil;

import au.com.bytecode.opencsv.CSVReader;

public abstract class CSVInput extends CTIOHandler {

	CSVReader myreader;
	Session session;
	
	PathDBUtil path_db_util;
	
	public CSVInput(File inputfile){
		try {
			myreader = new CSVReader( new FileReader(inputfile) );
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public CSVInput(Reader inputReader){
		try{
			myreader = new CSVReader(inputReader);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void begin(){
		session = factory.openSession();
		
		path_db_util = new PathDBUtil(session);
	}
	
	public void done(){
        session.close();
        try{
        	myreader.close();
        }catch(Exception e){}
	}
	
	static int NUM_PER_TRANSACTION = 10;
	private int num_handled = 0;
	
	public void handle() throws Exception {
		
        try{
	        String [] nextLine;
	        while ((nextLine = myreader.readNext()) != null) {
	        	if(num_handled == 0)
	    			session.beginTransaction();
	        	
	        	this.handle_strarray(nextLine);
	        	
	            num_handled = (num_handled+1) % NUM_PER_TRANSACTION;
	            if(num_handled == 0)
	            	session.getTransaction().commit();
	        		
	        }
        }finally{
        }
        
	}
	
	public abstract void handle_strarray(String[] in) throws Exception;
	
	public static boolean boolean_helper(String in){
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
