package org.sandiegozoo.pathology.contact_tracer;

import java.io.*;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.sandiegozoo.pathology.contact_tracer.dataimport.*;
import org.sandiegozoo.pathology.database.domain.*;

import junit.framework.TestCase;

public class DaylightSavingsTimeTest extends MyBase {

	
	CTIOHandler timeline_reader;
	CTIOHandler infection_reader;
	
	public void setUp(){
		
	     // A SessionFactory is set up once for an application
       this.setupFactory();
       
       timeline_reader = new TimelineHandler(new File(getClass().getClassLoader().getResource("DSTTestFiles/dst_timeline.txt").getFile()));
       timeline_reader.setSessionFactory(sessionFactory);
       infection_reader = new InfectionHandler(new File(getClass().getClassLoader().getResource("DSTTestFiles/dst_infections.txt").getFile()));
       infection_reader.setSessionFactory(sessionFactory);
        
       
	}
	
	public void testTracer(){
        
        System.out.println("LOADING TIMELINE");
        
        //Attempt loading the timeline file
        try {
        	timeline_reader.call();
		} catch (Exception e) {
			this.fail(e.getMessage());
		}
        
        System.out.println("LOADING INFECTIONS");
        
        //Attempt loading the infection file
        try {
        	infection_reader.call();
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
