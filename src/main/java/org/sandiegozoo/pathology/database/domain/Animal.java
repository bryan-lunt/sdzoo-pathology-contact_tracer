package org.sandiegozoo.pathology.database.domain;

import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;

@Entity
@Table( name = "Animal" )
public class Animal {

	@Id
	@GeneratedValue( generator="increment" )
	@GenericGenerator( name="increment", strategy="increment" )
	private long id;
	
	@Column( name = "species" )
	public String species = null;
	
	//@Id
	@Index( name = "animal_native_id_index" )
	@Column( name = "native_id", unique = true, nullable=false )
	public long native_ID = 0;
	
	
	public long getId() {
	    return id;
	}
	public void setId(long in){
		id = in;
	}
	
	public Animal(){}
	
	public Animal(long inID){
		this.native_ID = inID;
	}
	
	public String toString(){
		return "ANIMAL(" + id +") ID: " + this.native_ID + (this.species != null ? " : " + this.species : "");
	}
	
}
