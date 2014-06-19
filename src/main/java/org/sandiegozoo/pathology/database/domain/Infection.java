package org.sandiegozoo.pathology.database.domain;

import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.*;

@Entity
@Table( name = "Infection" )
public class Infection {

	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	private long id;
	
	@ManyToOne
	@JoinColumn( name = "animal_id" )
	public Animal animal_id;
	
	@Column( name = "name" )
	public String name = null;
	
	@Temporal(TemporalType.DATE)
	@Column( name = "onset_date" )
	public Calendar onset_date;
	
	@Temporal(TemporalType.DATE)
	@Column( name = "diagnosis_date" )
	public Calendar diagnosis_date;
	
	@Temporal(TemporalType.DATE)
	@Column( name = "end_date" )
	public Calendar end_date;
	
	@Column( name = "linger" )
	public int days_linger = 0;
	
	@Column( name = "notes" )
	public String notes;
	
	//METHODS
	
	public long getId() {
	    return id;
	}
	public void setId(long in){
		id = in;
	}
	
	public String toString(){
		String ret = "INFECTION(" + id + ")";
		ret += " ANIM: " + animal_id;
		ret += " [ " + onset_date.getTime() + " : ";
		ret += (end_date != null ? end_date.getTime().toString() : "ONGOING") + " ]";
		ret += "  DoDx:" + (diagnosis_date != null ? diagnosis_date.getTime() : "NONE");
		ret += " lingers " + days_linger + " days.";
				
		return ret;
	}
	
}