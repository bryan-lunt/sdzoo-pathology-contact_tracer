package org.sandiegozoo.pathology.database.domain;

import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;

@Entity
@Table( name = "Housing" )
public class Housing {

	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	private long id;
	
	@ManyToOne
	@JoinColumn(name = "enc_id")
	public Enclosure enc_id;
	
	@ManyToOne
	@JoinColumn(name = "animal_id")
	public Animal animal_id;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "move_in", nullable=false)
	public Date move_in;

	@Temporal(TemporalType.DATE)
	@Column(name = "move_out", nullable=true)
	public Date move_out;
	
	
	public long getId() {
	    return id;
	}
	public void setId(long in){
		id = in;
	}
	
	
}
