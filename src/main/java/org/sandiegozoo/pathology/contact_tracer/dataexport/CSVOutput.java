package org.sandiegozoo.pathology.contact_tracer.dataexport;

import java.io.*;
import java.util.List;

import org.hibernate.*;
import org.sandiegozoo.pathology.contact_tracer.CTIOHandler;

import au.com.bytecode.opencsv.CSVWriter;

public abstract class CSVOutput extends CTIOHandler {

	CSVWriter mywriter;
	Session session;
	String my_query_string;
	Query my_query;
	
	
	public CSVOutput(File outputFile){
		try {
			mywriter = new CSVWriter( new FileWriter(outputFile) );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public CSVOutput(Writer outputWriter){
		try{
			mywriter = new CSVWriter(outputWriter);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void setQuery(String q){
		my_query_string = q;
	}
	
	public void begin(){
		session = factory.openSession();
		session.beginTransaction();
		
		my_query = session.createQuery(my_query_string);
	}
	
	public void done(){
		session.getTransaction().commit();
        session.close();
        try{
        	mywriter.close();
        }catch(Exception e){}
	}
	
	public void handle() throws Exception {
		
		List results = my_query.list();
		
        try{
	        for(Object o : results){
	        	mywriter.writeNext(this.handle_object(o));
	        }
        }finally{
        }
        
	}
	
	public abstract String[] handle_object(Object in) throws Exception;
	
	
}
