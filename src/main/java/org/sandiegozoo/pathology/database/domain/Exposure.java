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
		
		//Deals with the fact that we really want to count the days, but we're unfortunately doing that by dividing by hours.
		//Calendar.get(Calendar.DST_OFFSET) will get the number of milliseconds that the timezone was offset from its normal time as a result of DST in that location.
		//TODO: Consider making the whole program timezone-agnostic. (Pretend everything happens in Greenwich, England?)
		
		long offset_delta_dst = end_date.get(end_date.DST_OFFSET) - start_date.get(start_date.DST_OFFSET);
		
		long delta = end_date.getTimeInMillis() - start_date.getTimeInMillis() + offset_delta_dst;
		return (int)java.lang.Math.ceil(delta / (1000.0*60*60*24));
	}
	
	
	
}
