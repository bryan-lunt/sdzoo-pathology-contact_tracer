package org.sandiegozoo.pathology.database.domain;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import java.util.Date;

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
	public Date start_date;
	
	@Temporal(TemporalType.DATE)
	@Column( name = "end_date" )
	public Date end_date;
	
	public String toString(){
		return "EXPOSURE(" + id +") ANIM: " + animal_id.getNativeID() + " : " + start_date + " : " + end_date;
	}
	
}
