package org.sandiegozoo.pathology.contact_tracer;

import org.sandiegozoo.pathology.contact_tracer.dataexport.*;
import org.sandiegozoo.pathology.contact_tracer.dataimport.*;
import org.sandiegozoo.pathology.contact_tracer.gui.CTMainFrame;
import org.sandiegozoo.pathology.database.domain.*;

import org.apache.commons.cli.*;

import org.hibernate.*;
import org.hibernate.cfg.Configuration;

import java.io.*;
import java.util.*;
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
	       MAIN.input_handlers.add(new TimelineHandler(new File(cmd.getOptionValue( "t" ))));
       }
       
       
       if(cmd.hasOption("i")){
    	  MAIN.input_handlers.add(new InfectionHandler(new File(cmd.getOptionValue( "i"))));
       }
       
       if(cmd.hasOption("o")){
    	   MAIN.input_handlers.add(new ContaminationHandler(new File(cmd.getOptionValue("c"))));
       } 
       //Main business logic!
       MAIN.call();
    	

    }
    
    public File exposure_output_file = null;
    public File contamination_output_file = null;
    /*
    public File timeline_file = null;
    public File infection_file = null;
    public File contamination_file = null;
    public File limit_enclosures_file = null;
    */
    public List<CTIOHandler> input_handlers = new ArrayList<CTIOHandler>();
    
    private SessionFactory sessionFactory;
    
    public App(){
// A SessionFactory is set up once for an application
        
    	sessionFactory = new Configuration()
        .configure() // configures settings from hibernate.cfg.xml
        .buildSessionFactory();
    	
    }
    
    public Object call() throws Exception{
    	
    	for(CTIOHandler one_input : input_handlers){
    		one_input.setSessionFactory(sessionFactory);
    		one_input.call();
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
