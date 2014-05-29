package org.sandiegozoo.pathology.contact_tracer;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.sandiegozoo.pathology.contact_tracer.dataimport.CSVImporter;
import org.sandiegozoo.pathology.contact_tracer.dataimport.InfectionHandler;
import org.sandiegozoo.pathology.contact_tracer.dataimport.TimelineHandler;
import org.sandiegozoo.pathology.database.domain.Animal;
import org.sandiegozoo.pathology.database.domain.Contamination;
import org.sandiegozoo.pathology.database.domain.Enclosure;
import org.sandiegozoo.pathology.database.domain.Housing;
import org.sandiegozoo.pathology.database.domain.Infection;

import junit.framework.TestCase;

public class InfectionLoaderTest extends TestCase {

	SessionFactory sessionFactory;
	CSVImporter my_csv_reader;
	InfectionHandler my_handler;
	
	public void setUp(){
		java.util.logging.Logger.getLogger("org.hibernate").setLevel(java.util.logging.Level.SEVERE);
	     // A SessionFactory is set up once for an application
        sessionFactory = new Configuration()
                .configure() // configures settings from hibernate.cfg.xml
                .buildSessionFactory();
        
        Reader theReader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("basic_infections.txt"));
        my_csv_reader = new CSVImporter();
        my_csv_reader.setSource(theReader);
        
        my_handler = new InfectionHandler(sessionFactory);
       
	}
	
	public void testBasic(){
        
        System.out.println("TESTING INFECTION LOADER");
        
        //Attempt loading the file
        try {
        	my_csv_reader.readInto(my_handler);
		} catch (Exception e) {
			this.fail(e.getMessage());
		}
        
        
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        List result = session.createQuery( "from Infection" ).list();
        for ( Infection one_inf : (List<Infection>) result ) {
        	
        	System.out.println("ONE INFECTION:");
        	System.out.println(one_inf.animal_id.getNativeID());
        	System.out.println(one_inf.onset_date);
        	System.out.println(one_inf.end_date);
        	System.out.println(one_inf.getId());
        	
        }
        
        List animals = session.createQuery("from Animal").list();
        for( Animal an : (List<Animal>)animals){
        	System.out.println("Animal created implicitly from infections: " + an.getNativeID());
        }
        
        session.getTransaction().commit();
        session.close();
        
	}
	
}