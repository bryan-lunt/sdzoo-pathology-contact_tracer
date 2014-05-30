package org.sandiegozoo.pathology.contact_tracer;

import java.util.*;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.sandiegozoo.pathology.database.domain.Animal;
import org.sandiegozoo.pathology.database.domain.Contamination;
import org.sandiegozoo.pathology.database.domain.Enclosure;
import org.sandiegozoo.pathology.database.domain.Housing;
import org.sandiegozoo.pathology.database.domain.Infection;

import junit.framework.TestCase;


public class BasicHibernateTest extends TestCase {

	SessionFactory sessionFactory;
	
	public void setUp(){
		
		
	     // A SessionFactory is set up once for an application
        sessionFactory = new Configuration()
                .configure() // configures settings from hibernate.cfg.xml
                .buildSessionFactory();
	}
	
	public void testBasic(){
		Session session = sessionFactory.openSession();
        session.beginTransaction();
        
        Enclosure tmp = new Enclosure();
        tmp.name = "Temporary Enclosure";
        
        Animal paul = new Animal("Paul");
        
        Infection sick = new Infection();
        sick.name = "Paul is very ill";
        sick.animal_id = paul;
        
        Housing paulHouse = new Housing();
        paulHouse.animal_id = paul;
        paulHouse.move_in = new GregorianCalendar();
        paulHouse.enc_id = tmp;
        
        Contamination foo = new Contamination();
        foo.enc_id = tmp;
        foo.source_inf_id = sick;
        foo.start_date = new GregorianCalendar();
        foo.end_date = new GregorianCalendar();
        
        
        
        session.save( paul );
        session.save( paulHouse );
        session.save( sick );
        session.save( tmp );
        session.save( foo );
        session.getTransaction().commit();
        session.close();
        
        session = sessionFactory.openSession();
        session.beginTransaction();
        List result = session.createQuery( "from Contamination" ).list();
        for ( Contamination one_contam : (List<Contamination>) result ) {
            System.out.println( one_contam );
        }
        session.getTransaction().commit();
        session.close();
	}
	
}
