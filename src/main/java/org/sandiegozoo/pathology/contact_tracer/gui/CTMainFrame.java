package org.sandiegozoo.pathology.contact_tracer.gui;

import java.awt.event.ActionEvent;

import javax.swing.*;

import org.sandiegozoo.pathology.contact_tracer.App;

public class CTMainFrame extends JFrame {

	private NamedComponentPanel basicPanel = new NamedComponentPanel();
	private NamedComponentPanel advancedPanel = new NamedComponentPanel();
	
	private JButton advancedGo;
	
	//TODO: Add an icon?
	class AdvancedGoAction extends AbstractAction {
		
		public AdvancedGoAction(){
			super("GO");
		}
		
		public void actionPerformed(ActionEvent arg0) {
			App myApp = new App();
			
			myApp.exposure_output_file = ((FileSelectorPanel)advancedPanel.getNamed("output_file")).getSelectedFile();
			myApp.timeline_file = ((FileSelectorPanel)advancedPanel.getNamed("timeline_file")).getSelectedFile();
			myApp.infection_file = ((FileSelectorPanel)advancedPanel.getNamed("infection_file")).getSelectedFile();
			myApp.limit_enclosures_file = ((FileSelectorPanel)advancedPanel.getNamed("enclosures_file")).getSelectedFile();
			myApp.contamination_file = ((FileSelectorPanel)advancedPanel.getNamed("contamination_file")).getSelectedFile();
			
			//TODO: Use a background thread for this.
			try{
				myApp.call();
			}catch(Exception e){
				System.err.println(e);
				e.printStackTrace();
			}
			
		}
		
	}
	
	public CTMainFrame(){
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		//setup the advancedPanel
		this.createAdvancedPanel();
		
		
		this.add(advancedPanel);
		this.setSize(this.getPreferredSize());
		
		
	}
	
	//Cant be static because the action needs a reference to an instance.
	private void createAdvancedPanel(){
		advancedPanel.setLayout(new BoxLayout(advancedPanel,BoxLayout.Y_AXIS));
		advancedPanel.add(new JLabel("Outputs:"));
		FileSelectorPanel output_file_panel = new FileSelectorPanel("Exposures Output File");
		output_file_panel.setMode(FileSelectorPanel.SAVE_FILE);
		advancedPanel.addNamed("output_file", output_file_panel);
		
		advancedPanel.add(new JSeparator(JSeparator.HORIZONTAL));
		advancedPanel.add(new JLabel("Inputs:"));
		
		advancedPanel.addNamed("timeline_file", new FileSelectorPanel("Timeline Input File"));
		advancedPanel.addNamed("infection_file", new FileSelectorPanel("Infection Input File"));
		advancedPanel.addNamed("enclosures_file", new FileSelectorPanel("Limit Enclosures File"));
		advancedPanel.addNamed("contamination_file", new FileSelectorPanel("Other Enclosure Contaminations (ex: Environmental) File"));
		
		advancedGo = new JButton(new AdvancedGoAction());//TODO: Swap out for an action.
		advancedPanel.addNamed("go_button", advancedGo);
		advancedPanel.add(new JSeparator(JSeparator.HORIZONTAL));
	}
	
}
