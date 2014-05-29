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
	private String species = null;
	
	@Column( name = "native_id", unique = true, nullable=false)
	private String native_ID = null;
	
	
	public long getId() {
	    return id;
	}
	public void setId(long in){
		id = in;
	}
	
	
	public String getSpecies(){
		return species;
	}
	public void setSpecies(String in){
		species = in;
	}
	
	
	public String getNativeID(){
		return native_ID;
	}
	public void setNativeID(String in){
		native_ID = in;
	}
	
	public Animal(){}
	
	public Animal(String inID){
		this.setNativeID(inID);
	}
	
	
}
