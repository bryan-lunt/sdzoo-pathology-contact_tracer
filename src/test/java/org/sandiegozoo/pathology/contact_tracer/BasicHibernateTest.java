package org.sandiegozoo.pathology.contact_tracer;

import java.util.List;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.sandiegozoo.pathology.database.domain.Animal;
import org.sandiegozoo.pathology.database.domain.Contamination;
import org.sandiegozoo.pathology.database.domain.Enclosure;
import org.sandiegozoo.pathology.database.domain.Housing;
import org.sandiegozoo.pathology.database.domain.Infection;

import junit.framework.TestCase;

import java.util.logging.Logger;
import java.util.logging.Level;

public class BasicHibernateTest extends TestCase {

	SessionFactory sessionFactory;
	
	public void setUp(){
		
		java.util.logging.Logger.getLogger("org.hibernate").setLevel(java.util.logging.Level.SEVERE);
		
	     // A SessionFactory is set up once for an application
        sessionFactory = new Configuration()
                .configure() // configures settings from hibernate.cfg.xml
                .buildSessionFactory();
	}
	
	public void testBasic(){
		Session session = sessionFactory.openSession();
        session.beginTransaction();
        
        Enclosure tmp = new Enclosure();
        tmp.setName("Temporary Enclosure");
        
        Animal paul = new Animal("Paul");
        
        Infection sick = new Infection();
        sick.name = "Paul is very ill";
        sick.animal_id = paul;
        
        Housing paulHouse = new Housing();
        paulHouse.animal_id = paul;
        paulHouse.move_in = new java.util.Date();
        paulHouse.enc_id = tmp;
        
        Contamination foo = new Contamination();
        foo.setEnclosure(tmp);
        foo.source_inf_id = sick;
        foo.setStartDate(new java.util.Date());
        foo.setEndDate(new java.util.Date());
        
        
        
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
        for ( Contamination event : (List<Contamination>) result ) {
            System.out.println( "Cont Enc : " + event.getId() + " : " + event.getEnclosure().getName() + " : " + event.source_inf_id.name);
        }
        session.getTransaction().commit();
        session.close();
	}
	
}
