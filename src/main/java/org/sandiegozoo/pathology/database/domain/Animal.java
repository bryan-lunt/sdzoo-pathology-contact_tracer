package org.sandiegozoo.pathology.database.domain;

import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table( name = "Animal" )
public class Animal {

	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	private long id;
	
	@Column( name = "species" )
	public String species = null;
	
	@Column( name = "native_id", unique = true, nullable=false)
	public String native_ID = null;
	
	
	public long getId() {
	    return id;
	}
	public void setId(long in){
		id = in;
	}
	
	public Animal(){}
	
	public Animal(String inID){
		this.native_ID = inID;
	}
	
	public String toString(){
		return "ANIMAL(" + id +") ID: " + this.native_ID + (this.species != null ? " : " + this.species : "");
	}
	
}
