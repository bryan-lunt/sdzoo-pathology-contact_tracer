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
    	boolean show_gui = false;
    	
    	if(args.length == 0){
    		show_gui = true;
    	}
    	
    	//Command line options.
    	Options program_options = new Options();
    	program_options.addOption("t", true, "Timeline file");
    	program_options.addOption("i", true, "Infection file");
    	program_options.addOption("s", true, "Simple Diagnosis file (you must also use BETA and GAMMA when using this input.");
    	program_options.addOption("b", "beta", true, "The number of days before DoDx to assume as the onset date. 0,1,2, etc. ");
    	program_options.addOption("g", "gamma", true, "The number of days the contagion will linger in an enclosure after the sick animal leaves. 0,1,2, etc. ");
    	program_options.addOption("c", true, "Environmental contamination file");
    	
    	program_options.addOption("o", true, "Output Exposures Filename");
    	program_options.addOption("d", true, "Save Contaminations");
    	
    	program_options.addOption("h", "help", false, "Print this help message.");
    	
    	CommandLineParser parser = new PosixParser();
    	CommandLine cmd = parser.parse( program_options, args);
    
    	
    	// automatically generate the help statement
    	if(cmd.hasOption("h")){
    		HelpFormatter formatter = new HelpFormatter();
    		formatter.printHelp("java -jar this-jar-file.jar", program_options);
    		System.exit(64);
    	}
    	
    	if(show_gui){
    		System.err.println("Starting GUI, use the flag --help to get a description of command-line usage.");
    		//Display the GUI to setup and run the program
    		CTMainFrame myMainFrame = new CTMainFrame();
        	myMainFrame.setVisible(true);
        	
	    }else{
	    	//Use command-line options to run the program
	    	
	    	App MAIN = new App();
	   	 
	    	
	    	//Setup INPUTS
	       if(cmd.hasOption("t")){
		       MAIN.input_handlers.add(new TimelineHandler(new File(cmd.getOptionValue( "t" ))));
	       }
	       
	       if(cmd.hasOption("i")){
	    	  MAIN.input_handlers.add(new InfectionHandler(new File(cmd.getOptionValue( "i"))));
	       }
	       
	       if(cmd.hasOption("s")){
	    	   if(!(cmd.hasOption("b") && cmd.hasOption("g"))){
	    		   System.err.println("A simple input was given, but no values specified for BETA or GAMMA (see help).");
	    		   System.exit(64);
	    	   }
	    	   
	    	   int cmdl_beta = 0;
	    	   int cmdl_gamma = 0;
	    	   try{
	    		   cmdl_beta = Integer.parseInt(cmd.getOptionValue("b"));
	    		   cmdl_gamma = Integer.parseInt(cmd.getOptionValue("g"));
	    		   if(cmdl_beta < 0 || cmdl_gamma < 0){
	    			   throw new Exception("Negative values");
	    		   }
	    	   }catch(Exception e){
	    		   System.err.println("Invalid integer format for BETA or GAMMA, valid values are \"0\", \"1\", \"2\", etc.");
	    		   System.exit(64);
	    	   }
	    	   
	    	   //OK, things should work.
	    	   MAIN.input_handlers.add(new BasicDiagnosisHandler(new File(cmd.getOptionValue("s")), cmdl_beta, cmdl_gamma));
	    	   
	       }
	       
	       if(cmd.hasOption("c")){
	    	   MAIN.input_handlers.add(new ContaminationHandler(new File(cmd.getOptionValue("c"))));
	       } 
	       
	       
	       
	       //setup OUTPUTS
	       
	       if(cmd.hasOption("o")){
	    	   MAIN.exposure_output_file = new File(cmd.getOptionValue("o"));
	       }//Otherwise goes to STDOUT
	       
	       if(cmd.hasOption("d")){
	    	   MAIN.contamination_output_file = new File(cmd.getOptionValue("d"));
	       }
	       
	       
	       //Main business logic!
	       MAIN.call();
	    }

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
        
        ExposureWriter my_e_writer;
        if(this.exposure_output_file != null){
        	my_e_writer = new ExposureWriter(this.exposure_output_file);
        }else{
        	my_e_writer = new ExposureWriter(new PrintWriter(System.out));
       }
       
       my_e_writer.setSessionFactory(sessionFactory);
       my_e_writer.call();
       
        //CONTAMINATIONS (if requested)
        
       if(this.contamination_output_file != null){
    	   ContaminationWriter my_c_writer = new ContaminationWriter(this.contamination_output_file);
    	   my_c_writer.setSessionFactory(sessionFactory);
    	   my_c_writer.call();
       }
        
        return null;
    }
    
}
