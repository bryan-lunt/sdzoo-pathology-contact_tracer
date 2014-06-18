package org.sandiegozoo.pathology.database;

import java.util.*;

import org.hibernate.Query;
import org.hibernate.Session;
import org.sandiegozoo.pathology.database.domain.*;

public class PathDBUtil {

	Session session;
	Query animal_from_nid;
	Query enc_from_name;
	
	Map<Long, Animal> animals_by_id = new HashMap<Long,Animal>();
	Map<String, Enclosure> encs_by_id = new HashMap<String,Enclosure>();
	
	public PathDBUtil(Session in){
		session = in;
		animal_from_nid = session.createQuery( "from Animal where native_id = :nid" );
		enc_from_name = session.createQuery( "from Enclosure where name = :nname");
	}
	
	public Animal completeOrCreateAnimal(long native_ID){
		Animal retval = animals_by_id.get(new Long(native_ID));
		if(retval != null)
			return retval;
		
		animal_from_nid.setLong("nid", native_ID);
		retval = (Animal)animal_from_nid.uniqueResult();
		
		if(retval == null){
			retval = new Animal();
			retval.native_ID = native_ID;
			session.persist(retval);
		}
		
		animals_by_id.put(new Long(native_ID), retval);
		
		return retval;
	}
	
	public Enclosure completeOrCreateEnclosure(String enc_name){
		Enclosure retval = encs_by_id.get(enc_name);
		if(retval != null)
			return retval;
		
		enc_from_name.setText("nname", enc_name);
		retval = (Enclosure)enc_from_name.uniqueResult();
		
		if(retval == null){
			retval = new Enclosure();
			retval.name = enc_name;
			session.persist(retval);
		}
		
		encs_by_id.put(enc_name, retval);
		
		return retval;
	}
	
}
