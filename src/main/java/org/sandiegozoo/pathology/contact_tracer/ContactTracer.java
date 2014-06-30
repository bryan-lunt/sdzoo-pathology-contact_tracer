package org.sandiegozoo.pathology.contact_tracer;

import java.util.Iterator;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import org.sandiegozoo.pathology.database.domain.*;

public class ContactTracer {

	/*flow:
	 * <assumed to be done>
	 * load animal timeline
	 * load infections
	 * 
	 * <done here>
	 * determine contaminations
	 * determine contacts (exposure to contamination)
	 * 
	 */
	
	private SessionFactory factory;
	private int state = 0;
	
	public ContactTracer(SessionFactory fact){
		factory = fact;
	}
	
	
	
	
	
	public void process_contaminations(){process_contaminations(true,false);}
	public void process_contaminations(boolean truncate_table, boolean truncate_overlapping){
		Session session = factory.openSession();
		Transaction myT = session.beginTransaction();
		
		//Start with a clean slate?
		if(truncate_table){
			Query myQ = session.createQuery("delete from Contamination");
			myQ.executeUpdate();
		}
		
		Query findInfections = session.createQuery("from Infection");
		List<Infection> inf_iter = (List<Infection>)findInfections.list();//I would prefer an iterator, we don't want to load all of everything into client memory...
		
		for(Infection one_inf : inf_iter){
			//find all enclosures this animal has been in while infected.
			Query findHousing = session.createQuery("from Housing where animal_id.id = :aid and move_in <= :end and move_out >= :begin");
			findHousing.setLong("aid", one_inf.animal_id.getId());
			findHousing.setCalendar("end", one_inf.end_date);
			findHousing.setCalendar("begin", one_inf.onset_date);
			
			List<Housing> housing_iter = (List<Housing>)findHousing.list();
			for(Housing one_housing : housing_iter){
				//Housing one_housing = housing_iter.next();
				
				Calendar contamination_begin = date_max(one_inf.onset_date, one_housing.move_in);
				Calendar direct_contamination_end = date_min(one_inf.end_date, one_housing.move_out);
				Calendar contamination_end;
				
				//We have to do this rigamarole because we can't just add to a date.
				contamination_end = (Calendar)direct_contamination_end.clone();
				contamination_end.add(Calendar.DATE, one_inf.days_linger);
				
				Contamination oneContamination = new Contamination();
				oneContamination.source_inf_id = one_inf;
				oneContamination.enc_id = one_housing.enc_id;
				oneContamination.start_date = contamination_begin;
				oneContamination.end_date = contamination_end;
				
				session.save(oneContamination);
			}
			
			session.flush();
			
			if(truncate_overlapping){
				this.truncate_overlapping_contaminations(session, one_inf);
				session.flush();
			}
			
		}
		
		
		session.getTransaction().commit();
		session.close();
		
	}
	
	private void truncate_overlapping_contaminations(Session session, Infection one_inf){
		//We truncate overlapping contaminations _of the same enclosure_ here.
		
		
		
		//If we are going to truncate or combine overlapping contaminations, it should happen here.
		Query find_contaminations_by_infection = session.createQuery("from Contamination where source_inf_id = :sinf order by enc_id.id asc, start_date asc");
		find_contaminations_by_infection.setEntity("sinf", one_inf);
		
		List<Contamination> contaminations_from_this_infection = (List<Contamination>)find_contaminations_by_infection.list();
		
		//DEBUG
		for(Contamination c : contaminations_from_this_infection){
			System.err.println("DEBUG" + c.toString());
		}
		
		//We really want to look at these two at a time, thus the weird loop.
		//From the query, we are already sorted.
		if(contaminations_from_this_infection.size() > 1){
			Contamination previous_contam = contaminations_from_this_infection.get(0);
			
			int upper_limit = contaminations_from_this_infection.size();
			for(int i = 1;i< upper_limit;i++){
				Contamination current_contam = contaminations_from_this_infection.get(i);
				
				//They must be contaminations of the same enclosure, and they must overlap.
				if(previous_contam.enc_id.equals(current_contam.enc_id) && !previous_contam.end_date.before(current_contam.start_date) ){
					Calendar tmp_end = (Calendar) current_contam.start_date.clone();
					tmp_end.add(Calendar.DATE, -1);//Minus one so that that date doesn't get double counted.
					
					previous_contam.end_date = tmp_end;
					
					session.update(previous_contam);
				}
				
				previous_contam = current_contam;
			}
		}
	}
	

	public void process_exposures(){process_exposures(true);}
	public void process_exposures(boolean truncate){
		Session session = factory.openSession();
		session.beginTransaction();
		
		//Start with a clean slate?
		if(truncate){
			Query myQ = session.createQuery("delete from Exposure");
			myQ.executeUpdate();
			//session.getTransaction().commit();
			//session.beginTransaction();
		}
		
		Query find_contaminations = session.createQuery("from Contamination");
        List<Contamination> contaminations = (List<Contamination>)find_contaminations.list();
        for(Contamination one_contam : contaminations){
        	
        	Animal original_animal = one_contam.source_inf_id == null ? null : one_contam.source_inf_id.animal_id;
        	
        	Query find_contacts;
        	
        	if(original_animal == null){//For example, environmental exposure or something
	        	find_contacts = session.createQuery("from Housing where enc_id.id = :eid and move_in <= :end and move_out >= :begin");
        	}else{//We have the original animal, remove it from the query.
        		find_contacts = session.createQuery("from Housing where enc_id.id = :eid and animal_id.id != :aid and move_in <= :end and move_out >= :begin");
        		find_contacts.setLong("aid", original_animal.getId());
        	}
        	//used in either case
        	find_contacts.setLong("eid",one_contam.enc_id.getId());
        	find_contacts.setCalendar("begin", one_contam.start_date);
        	find_contacts.setCalendar("end", one_contam.end_date);
        	
        	
        	List<Housing> overlapping_housing = (List<Housing>)find_contacts.list();
        	for(Housing one_house : overlapping_housing){
        		
        		//TODO: Continue here
        		Calendar exposure_begin_date = date_max(one_house.move_in, one_contam.start_date);//TODO:remove the gettime
        		Calendar exposure_end_date = date_min(one_house.move_out, one_contam.end_date);//TODO: remove the gettime once we switch everything to Calendar
        		
        		Exposure one_exposure = new Exposure();
        		
        		one_exposure.source = one_contam;
        		one_exposure.animal_id = one_house.animal_id;
        		one_exposure.start_date = exposure_begin_date;
        		one_exposure.end_date = exposure_end_date;
        		
        		session.save(one_exposure);
        	}
        	
        	
        }
		
		session.getTransaction().commit();
		session.close();
	}
	
	
	private Calendar date_min(Calendar one, Calendar two){
		return (one.compareTo(two) < 0) ? one : two;
	}
	private Calendar date_max(Calendar one, Calendar two){
		return (one.compareTo(two) < 0) ? two : one;
	}
	
}
