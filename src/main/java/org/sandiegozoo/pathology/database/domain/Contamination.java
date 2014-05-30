package org.sandiegozoo.pathology.database.domain;

import java.util.Calendar;

import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table( name = "Contamination" )
public class Contamination {

	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	private long id;
	
	@Column( name = "name", nullable=true )
	public String name = null;
	
	@ManyToOne
	@JoinColumn( name = "enc_id")
	public Enclosure enc_id;
	
	@Temporal(TemporalType.DATE)
	@Column( name = "start_date", nullable=false )
	public Calendar start_date;
	
	@Temporal(TemporalType.DATE)
	@Column( name = "end_date", nullable=false )
	public Calendar end_date;
	
	@Column( name = "is_direct" )
	public boolean is_direct;
	
	@ManyToOne
	@JoinColumn( name = "source_inf_id", nullable=true)
	public Infection source_inf_id;
	
	//METHODS
	
	public long getId() {
	    return id;
	}
	public void setId(long in){
		id = in;
	}
	
	
	public String toString(){
		return "CONTAMINATION(" + this.id + ") ENC: " + this.enc_id.name + " [ " + this.start_date.getTime() + " : " + this.end_date.getTime() + " ]";
	}
	
}
