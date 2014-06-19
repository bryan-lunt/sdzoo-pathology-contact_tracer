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
		session.beginTransaction();
		path_db_util = new PathDBUtil(session);
	}
	
	public void done(){
		session.getTransaction().commit();
        session.close();
        try{
        	myreader.close();
        }catch(Exception e){}
	}
	
	static int NUM_PER_BATCH = 100;
	private int num_handled_batch = 0;
	static int NUM_PER_TRANSACTION = 10000;
	private int num_handled_transaction = 0;
	
	public void handle() throws Exception {
		
        try{
	        String [] nextLine;
	        while ((nextLine = myreader.readNext()) != null) {
	        		
		        	this.handle_strarray(nextLine);
		        	
		            num_handled_batch = (num_handled_batch+1) % NUM_PER_BATCH;
		            if(num_handled_batch == 0){
		            	session.flush();
		            	session.clear();
		            	path_db_util.clear();
		            }
		        	
		            num_handled_transaction = (num_handled_transaction+1) % NUM_PER_TRANSACTION;
		            if(num_handled_transaction == 0){
		            	session.getTransaction().commit();
		            	session.beginTransaction();
		            }
	           
	            
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
