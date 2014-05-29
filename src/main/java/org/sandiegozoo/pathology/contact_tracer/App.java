package org.sandiegozoo.pathology.contact_tracer;

import org.sandiegozoo.pathology.contact_tracer.dataimport.CSVImporter;
import org.sandiegozoo.pathology.contact_tracer.dataimport.InfectionHandler;
import org.sandiegozoo.pathology.contact_tracer.dataimport.TimelineHandler;
import org.sandiegozoo.pathology.database.domain.*;

import org.apache.commons.cli.*;

import org.hibernate.*;
import org.hibernate.cfg.Configuration;

import java.io.*;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
    	Options program_options = new Options();
    	program_options.addOption("t",true, "Timeline file");
    	program_options.addOption("i",true, "Infection file");
    	program_options.addOption("c",true, "Environmental contamination file");
    	
    	CommandLineParser parser = new PosixParser();
    	CommandLine cmd = parser.parse( program_options, args);
    	
    	
    	
    	SessionFactory sessionFactory;
    	CSVImporter my_csv_reader;
        my_csv_reader = new CSVImporter();
    	
   	 // A SessionFactory is set up once for an application
        sessionFactory = new Configuration()
                .configure() // configures settings from hibernate.cfg.xml
                .buildSessionFactory();
       
       if(cmd.hasOption("t")){
    	   
	       System.err.println("LOADING TIMELINE");
	       Reader timeline_reader = new FileReader(new File(cmd.getOptionValue("t")));
	       //Attempt loading the timeline file
	       try {
	       	my_csv_reader = new CSVImporter();
	       	my_csv_reader.setSource(timeline_reader);
	       	my_csv_reader.readInto(new TimelineHandler(sessionFactory));
			} catch (Exception e) {
				throw e;
			}
       
       }
       
       
       if(cmd.hasOption("i")){
	       System.err.println("LOADING INFECTIONS");
	       Reader infection_reader = new FileReader(new File(cmd.getOptionValue("i")));
	       //Attempt loading the infection file
	       try {
	       	my_csv_reader = new CSVImporter();
	       	my_csv_reader.setSource(infection_reader);
	       	my_csv_reader.readInto(new InfectionHandler(sessionFactory));
			} catch (Exception e) {
				throw e;
			}
       }
       
       ContactTracer myTracer = new ContactTracer(sessionFactory);
       
       myTracer.process_contaminations();
       myTracer.process_exposures();
       
       System.err.println("FINISHED CONTACT TRACER");
       
       Session session = sessionFactory.openSession();
       Query find_contaminations = session.createQuery("from Contamination");
       List<Contamination> my_contaminations = (List<Contamination>)find_contaminations.list();
       for(Contamination one_contam : my_contaminations){
    	   System.err.println("FOUND A CONTAMINATION: " + one_contam);
       }
       
       Query find_exposures = session.createQuery("from Exposure");
       List<Exposure> my_exposures = (List<Exposure>)find_exposures.list();
       for(Exposure one_exp : my_exposures){
    	   System.out.println(one_exp);
       }
       
       session.close();
    	
    	

    }
}
