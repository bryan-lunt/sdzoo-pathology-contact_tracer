package org.sandiegozoo.pathology.contact_tracer;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.sandiegozoo.pathology.contact_tracer.dataimport.*;
import org.sandiegozoo.pathology.database.domain.*;

import junit.framework.TestCase;

public class ContactTracerTest extends TestCase {

	SessionFactory sessionFactory;
	CSVImporter my_csv_reader;
	
	Reader timeline_reader;
	Reader infection_reader;
	
	public void setUp(){
		java.util.logging.Logger.getLogger("org.hibernate").setLevel(java.util.logging.Level.SEVERE);
		
	     // A SessionFactory is set up once for an application
        sessionFactory = new Configuration()
                .configure() // configures settings from hibernate.cfg.xml
                .buildSessionFactory();
        
       my_csv_reader = new CSVImporter();
       timeline_reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("basic_timeline.txt"));
       infection_reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("basic_infections.txt"));
        
       
	}
	
	public void testTracer(){
        
        System.out.println("LOADING TIMELINE");
        
        //Attempt loading the timeline file
        try {
        	my_csv_reader = new CSVImporter();
        	my_csv_reader.setSource(timeline_reader);
        	my_csv_reader.readInto(new TimelineHandler(sessionFactory));
		} catch (Exception e) {
			this.fail(e.getMessage());
		}
        
        System.out.println("LOADING INFECTIONS");
        
        //Attempt loading the infection file
        try {
        	my_csv_reader = new CSVImporter();
        	my_csv_reader.setSource(infection_reader);
        	my_csv_reader.readInto(new InfectionHandler(sessionFactory));
		} catch (Exception e) {
			this.fail(e.getMessage());
		}
        
        
        ContactTracer myTracer = new ContactTracer(sessionFactory);
        
        myTracer.process_contaminations();
        myTracer.process_exposures();
        
        System.out.println("FINISHED CONTACT TRACER");
        
        Session session = sessionFactory.openSession();
        Query find_contaminations = session.createQuery("from Contamination");
        List<Contamination> my_contaminations = (List<Contamination>)find_contaminations.list();
        for(Contamination one_contam : my_contaminations){
        	System.out.println("FOUND A CONTAMINATION: " + one_contam);
        }
        
        Query find_exposures = session.createQuery("from Exposure");
        List<Exposure> my_exposures = (List<Exposure>)find_exposures.list();
        for(Exposure one_exp : my_exposures){
        	System.out.println("FOUND AN EXPOSURE: " + one_exp);
        }
        
        session.close();
	}
	
}