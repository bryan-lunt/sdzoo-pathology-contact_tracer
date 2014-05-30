package org.sandiegozoo.pathology.database;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.sandiegozoo.pathology.database.domain.*;

public class PathDBUtil {

	public static Animal completeOrCreateAnimal(Animal partial, Session session){
		
		Query myQ = session.createQuery( "from Animal where native_id = :nid" );
		myQ.setText("nid", partial.native_ID);
		
		Animal retval = (Animal)myQ.uniqueResult();
		
		if(retval == null){
			retval = partial;
			session.save(retval);
		}
		
		return retval;
	}
	
	public static Enclosure completeOrCreateEnclosure(Enclosure partial, Session session){
		
		Query myQ = session.createQuery( "from Enclosure where name = :nname");
		myQ.setText("nname", partial.name);
		
		Enclosure retval = (Enclosure)myQ.uniqueResult();
		
		if(retval == null){
			retval = partial;
			session.save(retval);
		}
		
		return retval;
	}
	
}
