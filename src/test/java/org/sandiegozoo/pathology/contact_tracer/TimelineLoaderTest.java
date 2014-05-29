package org.sandiegozoo.pathology.contact_tracer;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.sandiegozoo.pathology.contact_tracer.dataimport.CSVImporter;
import org.sandiegozoo.pathology.contact_tracer.dataimport.TimelineHandler;
import org.sandiegozoo.pathology.database.domain.Animal;
import org.sandiegozoo.pathology.database.domain.Contamination;
import org.sandiegozoo.pathology.database.domain.Enclosure;
import org.sandiegozoo.pathology.database.domain.Housing;
import org.sandiegozoo.pathology.database.domain.Infection;

import junit.framework.TestCase;

public class TimelineLoaderTest extends TestCase {

	SessionFactory sessionFactory;
	CSVImporter my_csv_reader;
	TimelineHandler my_handler;
	
	public void setUp(){
	     // A SessionFactory is set up once for an application
        sessionFactory = new Configuration()
                .configure() // configures settings from hibernate.cfg.xml
                .buildSessionFactory();
        
        Reader theReader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("basic_timeline.txt"));
        my_csv_reader = new CSVImporter();
        my_csv_reader.setSource(theReader);
        
        my_handler = new TimelineHandler(sessionFactory);
       
	}
	
	public void testBasic(){
        
        System.out.println("TESTING TIMELINE LOADER");
        
        //Attempt loading the file
        try {
        	my_csv_reader.readInto(my_handler);
		} catch (Exception e) {
			this.fail(e.getMessage());
		}
        
        
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        List result = session.createQuery( "from Housing" ).list();
        for ( Housing oneHouse : (List<Housing>) result ) {
        	
        	System.out.println("One Housing : ANIMAL:" + oneHouse.animal_id.getNativeID() + " IN:" + oneHouse.move_in.toGMTString() + " OUT:" + oneHouse.move_out.toGMTString() + " NAME:" + oneHouse.enc_id.getName());
       
        }
        
        List animals = session.createQuery("from Animal where native_id = 12345").list();
        this.assertTrue("Redundant Animals Created!", animals.size() == 1);
        
        session.getTransaction().commit();
        session.close();
        
	}
	
}
