package org.sandiegozoo.pathology.contact_tracer;

import java.io.File;
import org.hibernate.*;

public abstract class CTIOHandler {

	protected SessionFactory factory;
	
	public CTIOHandler(){}
	
	public void setSessionFactory(SessionFactory fact){
		factory = fact;
	}
	
	public void call() throws Exception {
		this.begin();
		this.handle();
		this.done();
	}
	
	public abstract void begin() throws Exception;
	public abstract void handle() throws Exception;
	public abstract void done() throws Exception;
	
}
