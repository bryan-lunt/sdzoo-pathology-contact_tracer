package org.sandiegozoo.pathology.contact_tracer.dataimport;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.sandiegozoo.pathology.contact_tracer.dataimport.CSVImporter;

public abstract class HibernateImportHandler implements CSVImporter.ImportHandler{
	
	SessionFactory factory;
	Session session;
	
	private HibernateImportHandler(){}
	
	public HibernateImportHandler(SessionFactory sessionFact){
		factory = sessionFact;
	}
	
	public void begin(){
		session = factory.openSession();
		session.beginTransaction();
	}
	
	public void done(){
		session.getTransaction().commit();
        session.close();
	}
}