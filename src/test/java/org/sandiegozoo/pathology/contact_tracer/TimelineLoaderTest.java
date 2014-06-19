package org.sandiegozoo.pathology.contact_tracer;

import java.io.*;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.sandiegozoo.pathology.contact_tracer.dataimport.*;
import org.sandiegozoo.pathology.database.domain.*;

import junit.framework.TestCase;

public class TimelineLoaderTest extends MyBase {

	CTIOHandler timeline_reader;
	
	public void setUp(){
	     // A SessionFactory is set up once for an application
        this.setupFactory();
        
        Reader theReader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("basic_timeline.txt"));
        
        timeline_reader = new TimelineHandler(new File(getClass().getClassLoader().getResource("basic_timeline.txt").getFile()));
        timeline_reader.setSessionFactory(sessionFactory);
       
	}
	
	public void testBasic(){
        
        System.out.println("TESTING TIMELINE LOADER");
        
        //Attempt loading the file
        try {
        	timeline_reader.call();
		} catch (Exception e) {
			this.fail(e.getMessage());
		}
        
        
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        List result = session.createQuery( "from Housing" ).list();
        for ( Housing oneHouse : (List<Housing>) result ) {
        	
        	System.out.println("One Housing : " + oneHouse);
        }
        
        List animals = session.createQuery("from Animal where native_id = 12345").list();
        this.assertTrue("Redundant Animals Created!", animals.size() == 1);
        
        session.getTransaction().commit();
        session.close();
        
	}
	
}
