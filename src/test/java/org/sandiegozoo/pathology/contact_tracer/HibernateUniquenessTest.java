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

public class HibernateUniquenessTest extends TestCase {

	SessionFactory sessionFactory;
	
	public void setUp(){
		java.util.logging.Logger.getLogger("org.hibernate").setLevel(java.util.logging.Level.SEVERE);
	     // A SessionFactory is set up once for an application
        sessionFactory = new Configuration()
                .configure() // configures settings from hibernate.cfg.xml
                .buildSessionFactory();
	}
	
	public void testBasic(){
		/*
		Session session = sessionFactory.openSession();
        session.beginTransaction();
        
        Enclosure tmp = new Enclosure();
        tmp.setName("Temporary Enclosure");
        
        Enclosure tmp2 = new Enclosure();
        tmp2.setName("Temporary Enclosure");
        
        Animal paul = new Animal("Paul");
        Animal john = new Animal("John");
        
        Housing paulHouse = new Housing();
        paulHouse.animal_id = paul;
        paulHouse.enc_id = tmp;
        paulHouse.move_in = new java.util.Date();
        
        Housing johnHouse = new Housing();
        johnHouse.animal_id = john;
        johnHouse.enc_id = tmp2;
        johnHouse.move_in = new java.util.Date();
        
        
        
        session.save( paul );
        session.save( john );
        session.save( tmp );
        session.save( tmp2 );
        session.save(paulHouse);
        session.save(johnHouse);
        session.getTransaction().commit();
        session.close();
        
        session = sessionFactory.openSession();
        session.beginTransaction();
        List result = session.createQuery( "from Housing" ).list();
        for ( Housing house : (List<Housing>) result ) {
            System.out.println( "Housing, enclosureID : " + house.enc_id.getId());
        }
        session.getTransaction().commit();
        session.close();
        */
	}
	
}
