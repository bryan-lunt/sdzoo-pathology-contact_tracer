package org.sandiegozoo.pathology.contact_tracer;

import java.io.*;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.sandiegozoo.pathology.contact_tracer.dataimport.*;
import org.sandiegozoo.pathology.database.domain.*;

import junit.framework.TestCase;

public class InfectionLoaderTest extends TestCase {

	SessionFactory sessionFactory;
	CTIOHandler infection_reader;
	
	public void setUp(){
	     // A SessionFactory is set up once for an application
        sessionFactory = new Configuration()
                .configure() // configures settings from hibernate.cfg.xml
                .buildSessionFactory();
        
        infection_reader = new InfectionHandler(new File(getClass().getClassLoader().getResource("basic_infections.txt").getFile()));
        infection_reader.setSessionFactory(sessionFactory);
       
	}
	
	public void testBasic(){
        
        System.out.println("TESTING INFECTION LOADER");
        
        //Attempt loading the file
        try {
        	infection_reader.call();
		} catch (Exception e) {
			this.fail(e.getMessage());
		}
        
        
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        List result = session.createQuery( "from Infection" ).list();
        for ( Infection one_inf : (List<Infection>) result ) {
        	
        	System.out.println("ONE INFECTION: " + one_inf);
        	
        }
        
        List animals = session.createQuery("from Animal").list();
        for( Animal an : (List<Animal>)animals){
        	System.out.println("Animal created implicitly from infections: " + an);
        }
        
        session.getTransaction().commit();
        session.close();
        
	}
	
}
