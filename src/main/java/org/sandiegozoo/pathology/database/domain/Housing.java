package org.sandiegozoo.pathology.database.domain;

import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.Calendar;

@Entity
@Table( name = "Housing" )
public class Housing {

	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	private long id;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "enc_id")
	public Enclosure enc_id;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "animal_id")
	public Animal animal_id;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "move_in", nullable=false)
	public Calendar move_in;

	@Temporal(TemporalType.DATE)
	@Column(name = "move_out", nullable=true)
	public Calendar move_out;
	
	
	public long getId() {
	    return id;
	}
	public void setId(long in){
		id = in;
	}
	
	public String toString(){
		return "HOUSING(" + id + ") : ANIM : " + animal_id + " : [ " + move_in.getTime() + " : " + move_out.getTime() + " ]" ;
	}
}
