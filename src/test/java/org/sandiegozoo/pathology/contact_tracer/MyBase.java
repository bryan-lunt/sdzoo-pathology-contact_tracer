package org.sandiegozoo.pathology.contact_tracer;

import junit.framework.TestCase;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class MyBase extends TestCase {

	SessionFactory sessionFactory;
	
	public void setupFactory(){
		
		sessionFactory = new Configuration()
        .configure() // configures settings from hibernate.cfg.xml
        .buildSessionFactory();
		
		Session session = sessionFactory.openSession();
		
		
		String[] foo = {"Exposure","Contamination", "Infection", "Housing", "Animal", "Enclosure"};
		
		for(String s : foo)
		{
			session.beginTransaction();
			Query myQ = session.createQuery("delete from " + s);
			myQ.executeUpdate();
			session.flush();
			session.clear();
			session.getTransaction().commit();
		}
		
		
	}
	
}
