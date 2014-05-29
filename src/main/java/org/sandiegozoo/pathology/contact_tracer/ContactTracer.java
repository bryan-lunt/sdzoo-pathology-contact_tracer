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
	
	public void process_contaminations(){
		process_contaminations(true);
	}
	public void process_contaminations(boolean truncate){
		Session session = factory.openSession();
		Transaction myT = session.beginTransaction();
		
		//Start with a clean slate?
		if(truncate){
			Query myQ = session.createQuery("delete from Contamination");
			myQ.executeUpdate();
			session.getTransaction().commit();
			session.beginTransaction();
		}
		
		Query findInfections = session.createQuery("from Infection");
		List<Infection> inf_iter = (List<Infection>)findInfections.list();//I would prefer an iterator, we don't want to load all of everything into client memory...
		
		for(Infection one_inf : inf_iter){
			//find all enclosures this animal has been in while infected.
			Query findHousing = session.createQuery("from Housing where animal_id.id = :aid and move_in <= :end and move_out >= :begin");
			findHousing.setLong("aid", one_inf.animal_id.getId());
			findHousing.setDate("end", one_inf.end_date);
			findHousing.setDate("begin", one_inf.onset_date);
			
			List<Housing> housing_iter = (List<Housing>)findHousing.list();
			for(Housing one_housing : housing_iter){
				//Housing one_housing = housing_iter.next();
				
				Date contamination_begin = date_max(one_inf.onset_date, one_housing.move_in);
				Date direct_contamination_end = date_min(one_inf.end_date, one_housing.move_out);
				Date contamination_end;
				
				//We have to do this rigamarole because we can't just add to a date.
				Calendar contamination_end_calendar = new GregorianCalendar();
				contamination_end_calendar.setTime(direct_contamination_end);
				contamination_end_calendar.add(Calendar.DATE, one_inf.days_linger);
				contamination_end = contamination_end_calendar.getTime();
				
				Contamination oneContamination = new Contamination();
				oneContamination.source_inf_id = one_inf;
				oneContamination.setEnclosure(one_housing.enc_id);
				oneContamination.setStartDate(contamination_begin);
				oneContamination.setEndDate(contamination_end);
				
				session.save(oneContamination);
			}
			
			
		}
		
		
		session.getTransaction().commit();
		session.close();
		
	}
	

	public void process_exposures(){
		Session session = factory.openSession();
		session.beginTransaction();
		
		Query find_contaminations = session.createQuery("from Contamination");
        List<Contamination> contaminations = (List<Contamination>)find_contaminations.list();
        for(Contamination one_contam : contaminations){
        	
        	Animal original_animal = one_contam.source_inf_id.animal_id;
        	
        	Query find_contacts;
        	
        	if(original_animal == null){//For example, environmental exposure or something
	        	find_contacts = session.createQuery("from Housing where enc_id.id = :eid and move_in <= :end and move_out >= :begin");
        	}else{//We have the original animal, remove it from the query.
        		find_contacts = session.createQuery("from Housing where enc_id.id = :eid and animal_id.id != :aid and move_in <= :end and move_out >= :begin");
        		find_contacts.setLong("aid", original_animal.getId());
        	}
        	//used in either case
        	find_contacts.setLong("eid",one_contam.getEnclosure().getId());
        	find_contacts.setDate("begin", one_contam.getStartDate());
        	find_contacts.setDate("end", one_contam.getEndDate());
        	
        	
        	List<Housing> overlapping_housing = (List<Housing>)find_contacts.list();
        	for(Housing one_house : overlapping_housing){
        		
        		//TODO: Continue here
        		Date exposure_begin_date = date_max(one_house.move_in, one_contam.getStartDate());
        		Date exposure_end_date = date_min(one_house.move_out, one_contam.getEndDate());
        		
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
	
	
	private Date date_min(Date one, Date two){
		if(one.before(two))
			return one;
		else
			return two;
	}
	
	private Date date_max(Date one, Date two){
		if(one.before(two))
			return two;
		else
			return one;
	}
	
}
