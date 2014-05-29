package org.sandiegozoo.pathology.database.domain;

import java.util.Date;

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
	private String name = null;
	
	@ManyToOne
	@JoinColumn( name = "enc_id")
	private Enclosure enc_id;
	
	@Temporal(TemporalType.DATE)
	@Column( name = "start_date", nullable=false )
	private Date start_date;
	
	@Temporal(TemporalType.DATE)
	@Column( name = "end_date", nullable=false )
	private Date end_date;
	
	@Column( name = "is_direct" )
	private boolean is_direct;
	
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
	
	
	public String getName(){
		return name;
	}
	public void setName(String in){
		name = in;
	}
	
	public Enclosure getEnclosure(){
		return enc_id;
	}
	public void setEnclosure(Enclosure in){
		enc_id = in;
	}
	
	public Date getStartDate(){
		return start_date;
	}
	public void setStartDate(Date in){
		start_date = in;
	}
	
	public Date getEndDate(){
		return end_date;
	}
	public void setEndDate(Date in){
		end_date = in;
	}
	
	public boolean getIsDirect(){
		return is_direct;
	}
	public void setIsDirect(boolean in){
		is_direct = in;
	}
	
	
	public String toString(){
		return "CONTAMINATION(" + this.id + ") ENC: " + this.enc_id.getName() + " " + start_date + " " + end_date;
	}
	
}
