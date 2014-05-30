package org.sandiegozoo.pathology.database.domain;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import java.util.Calendar;

@Entity
@Table(name = "Exposure")
public class Exposure {

	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	private long id;
	
	public long getId(){ return id; }
	public void setId(long in){ id = in; }
	
	@ManyToOne
	@JoinColumn( name = "animal_id", nullable=false)
	public Animal animal_id;
	
	@ManyToOne
	@JoinColumn( name = "source" , nullable=false)
	public Contamination source;
	
	@Temporal(TemporalType.DATE)
	@Column( name = "start_date" )
	public Calendar start_date;
	
	@Temporal(TemporalType.DATE)
	@Column( name = "end_date" )
	public Calendar end_date;
	
	
	public String toString(){
		return "EXPOSURE(" + id +") ANIM: " + animal_id.native_ID + " : " + start_date.getTime() + " : " + end_date.getTime() + " Duration:" + this.getDurationDays();
	}
	
	public int getDurationDays(){
		//Should return end_date - start_date, but that would be too easy, wouldn't it.
		//Java has no timedelta or datedelta, so everyone implements their own. :'(
		
		long delta = end_date.getTimeInMillis() - start_date.getTimeInMillis();
		return (int)java.lang.Math.ceil(delta / (1000.0*60*60*24));
	}
	
	
	
}
