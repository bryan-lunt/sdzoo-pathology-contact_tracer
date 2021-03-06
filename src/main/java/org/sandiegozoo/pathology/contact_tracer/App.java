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
    	OrderedOptions program_options = new OrderedOptions();
    	program_options.addOption("h", "help", false, "Print this help message.");
    	program_options.addOption("t", true, "Housing Timeline file");
    	program_options.addOption("i", true, "Infection file");
    	program_options.addOption("s", true, "Simple Diagnosis file (you must also use BETA and GAMMA when using this input.");
    	program_options.addOption("b", "beta", true, "The number of days before diagnosis date to assume as the onset date. 0,1,2, etc. ");
    	program_options.addOption("g", "gamma", true, "The number of days the contagion will remain in an enclosure after the sick animal leaves. 0,1,2, etc. ");
    	program_options.addOption("e", true, "Enclosure Contaminations Input file");
    	
    	program_options.addOption("c", true, "Contacts Output Filename");
    	program_options.addOption("d", true, "Save Enclosure Contaminations (including any in input)");
    	
    	program_options.addOption("o", "overlap", false, "KEEP overlapping contaminations/contacts from the same source infection and animal. (Default is to truncate the earlier one.)");
    	
    	
    	
    	CommandLineParser parser = new PosixParser();
    	CommandLine cmd = parser.parse( program_options, args);
    
    	
    	// automatically generate the help statement
    	if(cmd.hasOption("h")){
    		HelpFormatter formatter = program_options.getOrderedHelp();
    		
    		//CITATION:
    		//Copied from [ http://stackoverflow.com/questions/11158235/get-name-of-executable-jar-from-within-main-method ]
    		//To get the name of the jar:
    		String jar_name = new java.io.File(App.class.getProtectionDomain()
    				  .getCodeSource()
    				  .getLocation()
    				  .getPath())
    				.getName();
    		
    		formatter.printHelp("java -jar " + jar_name , program_options);
    		System.exit(64);
    	}
    	
    	if(show_gui){
    		System.err.println("Starting GUI, use the flag --help to get a description of command-line usage.");
    		
    		// take the menu bar off the jframe
    		System.setProperty("apple.laf.useScreenMenuBar", "true");
    		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "ContactTracer");
    		
    		//Display the GUI to setup and run the program
    		CTMainFrame myMainFrame = new CTMainFrame();
        	myMainFrame.setVisible(true);
        	
	    }else{
	    	//Use command-line options to run the program
	    	
	    	App main_app = new App();
	   	 
	    	
	    	//Setup INPUTS
	       if(cmd.hasOption("t")){
		       main_app.addInputHandler(new TimelineHandler(new File(cmd.getOptionValue( "t" ))));
	       }
	       
	       if(cmd.hasOption("i")){
	    	  main_app.addInputHandler(new InfectionHandler(new File(cmd.getOptionValue( "i"))));
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
	    	   main_app.addInputHandler(new BasicDiagnosisHandler(new File(cmd.getOptionValue("s")), cmdl_beta, cmdl_gamma));
	    	   
	       }
	       
	       if(cmd.hasOption("e")){
	    	   main_app.addInputHandler(new ContaminationHandler(new File(cmd.getOptionValue("e"))));
	       } 
	       
	       
	       
	       //setup OUTPUTS
	       
	       if(cmd.hasOption("c")){
	    	   main_app.exposure_output_file = new File(cmd.getOptionValue("c"));
	       }//Otherwise goes to STDOUT
	       
	       if(cmd.hasOption("d")){
	    	   main_app.contamination_output_file = new File(cmd.getOptionValue("d"));
	       }
	       
	      
	       main_app.truncate_overlapping = !cmd.hasOption("o");
	       
	       
	       //Main business logic!
	       main_app.call();
	    }

    }
    
    public boolean truncate_overlapping = false;
    
    public File exposure_output_file = null;
    public File contamination_output_file = null;
    
    public List<CTIOHandler> input_handlers = new ArrayList<CTIOHandler>();
    
    public void addInputHandler(CTIOHandler input){
    	this.input_handlers.add(input);
    }
    
    public SessionFactory sessionFactory;
    
    public App(){
// A SessionFactory is set up once for an application
        
    	sessionFactory = new Configuration()
        .configure() // configures settings from hibernate.cfg.xml
        .buildSessionFactory();
    	
    }
    
    public Object call() throws Exception{
    	
    	for(CTIOHandler one_input : input_handlers){
    		System.err.print("Doing one input handler...");
    		
    		one_input.setSessionFactory(sessionFactory);
    		one_input.call();
    		
    		System.err.println("DONE.");
    	}
	    
    	
    	
    	ContactTracer myTracer = new ContactTracer(sessionFactory);
        
        myTracer.process_contaminations(false,truncate_overlapping);
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
