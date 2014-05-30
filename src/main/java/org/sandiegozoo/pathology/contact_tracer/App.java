package org.sandiegozoo.pathology.contact_tracer;

import org.sandiegozoo.pathology.contact_tracer.dataexport.CSVExporter;
import org.sandiegozoo.pathology.contact_tracer.dataexport.ExposureFormat;
import org.sandiegozoo.pathology.contact_tracer.dataimport.CSVImporter;
import org.sandiegozoo.pathology.contact_tracer.dataimport.DataImport;
import org.sandiegozoo.pathology.contact_tracer.gui.CTMainFrame;
import org.sandiegozoo.pathology.database.domain.*;

import org.apache.commons.cli.*;

import org.hibernate.*;
import org.hibernate.cfg.Configuration;

import java.io.*;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Hello world!
 *
 */
public class App implements Callable<Object>
{
    public static void main( String[] args ) throws Exception
    {
    	CTMainFrame myMainFrame = new CTMainFrame();
    	myMainFrame.setVisible(true);
    	
    	Thread.sleep(1000000000);
    	
    	//DEBUG
    	Options program_options = new Options();
    	program_options.addOption("t",true, "Timeline file");
    	program_options.addOption("i",true, "Infection file");
    	program_options.addOption("c",true, "Environmental contamination file");
    	
    	program_options.addOption("o",true, "Output Exposures Filename");
    	
    	CommandLineParser parser = new PosixParser();
    	CommandLine cmd = parser.parse( program_options, args);
    
    	
    	
    	App MAIN = new App();
   	 
       if(cmd.hasOption("t")){
	       MAIN.timeline_file = new File(cmd.getOptionValue( "t" ));
       }
       
       
       if(cmd.hasOption("i")){
	      MAIN.infection_file = new File(cmd.getOptionValue("i"));
       }
       
       if(cmd.hasOption("o")){
    	   MAIN.exposure_output_file = new File(cmd.getOptionValue("o"));
       } 
       //Main business logic!
       MAIN.call();
    	

    }
    
    public File exposure_output_file = null;
    public File contamination_output_file = null;
    public File timeline_file = null;
    public File infection_file = null;
    public File contamination_file = null;
    public File limit_enclosures_file = null;
    
    private SessionFactory sessionFactory;
    private DataImport myImporter;
    
    public App(){
// A SessionFactory is set up once for an application
        
    	sessionFactory = new Configuration()
        .configure() // configures settings from hibernate.cfg.xml
        .buildSessionFactory();
    	myImporter = new DataImport(sessionFactory);
    	
    	
    	
    }
    
    public Object call() throws Exception{
    	//Load the various infiles
    	System.err.println("LOADING TIMELINE");
	    myImporter.loadTimelineFile(timeline_file);
	    
	    if(infection_file != null){
	    	System.err.println("LOADING INFECTIONS");
	    	myImporter.loadInfectionFile(infection_file);
	    }
	    
	    if(contamination_file != null){
	    	System.err.println("LOADING CONTAMINATIONS");
	    	myImporter.loadContaminationFile(contamination_file);
	    }
	    
    	
    	
    	ContactTracer myTracer = new ContactTracer(sessionFactory);
        
        myTracer.process_contaminations(false);
        myTracer.process_exposures();
        
        System.err.println("FINISHED CONTACT TRACER");
        
        /*
         * OUTPUT
         */
        Session session = sessionFactory.openSession();
        
        //EXPOSURES
        Query find_exposures = session.createQuery("from Exposure");
        List<Exposure> my_exposures = (List<Exposure>)find_exposures.list();
        
        CSVExporter theExporter = new CSVExporter();
        Writer theWriter;
        if(this.exposure_output_file != null){
	        theWriter = new FileWriter(this.exposure_output_file);
        }else{
        	theWriter = new PrintWriter(System.out);
        }
        theExporter.setDestination(theWriter);
        theExporter.writeCSV(new ExposureFormat(my_exposures));
       
        //CONTAMINATIONS (if requested)
        
        
        
        session.close();
        
        return null;
    }
    
}
