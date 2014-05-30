package org.sandiegozoo.pathology.contact_tracer.gui;

import javax.swing.*;



import java.util.*;

public class NamedComponentPanel extends JPanel {
	
	private Map<String, JComponent> myComponents = new HashMap<String, JComponent>();
	
	public void addNamed(String name, JComponent comp ){
		myComponents.put(name, comp);
		this.add(comp);
	}
	
	public JComponent getNamed(String name){
		return myComponents.get(name);
	}
}
