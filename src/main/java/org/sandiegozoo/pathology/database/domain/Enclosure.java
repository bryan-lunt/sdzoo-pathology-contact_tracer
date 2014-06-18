package org.sandiegozoo.pathology.database.domain;

import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;

@Entity
@Table( name = "Enclosure" )
public class Enclosure {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	//@GenericGenerator(name="increment", strategy = "increment")
	private long id;
	
	@Index(name = "enclosure_name_index")
	@Column( name = "name", unique = true )
	public String name = null;
	
	
	public long getId() {
	    return id;
	}
	public void setId(long in){
		id = in;
	}
	
	public Enclosure(){}
	
	public String toString(){
		return "ENCLOSURE(" + id +") " + name;
	}
}
